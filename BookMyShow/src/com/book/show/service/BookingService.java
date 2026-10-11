package com.book.show.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.book.show.exception.SeatUnavailableException;
import com.book.show.modal.Booking;
import com.book.show.modal.Payment;
import com.book.show.modal.Seat;
import com.book.show.modal.Show;
import com.book.show.modal.ShowSeat;
import com.book.show.repository.InMemoryBookingRepository;
import com.book.show.repository.InMemoryShowRepository;

/**
 * Seat booking flow:   bookSeats() --> (user pays within the hold time) --> pay()   or   cancelBooking() / expiry
 *
 * Concurrency design - there is NO global or per-show lock anywhere on this path:
 *  - each seat is a CAS state machine (ShowSeat), so two users can never hold the same seat;
 *  - seats of one booking are locked in ascending id order and rolled back on failure, so two overlapping
 *    bookings can't deadlock or both fail by grabbing seats in opposite orders;
 *  - a booking's status moves only through CAS transitions, so pay / cancel / expiry can't race each other.
 */
public class BookingService {
	public static final long DEFAULT_LOCK_TTL_MS = 5 * 60 * 1000L; // how long a user may hold seats before paying
	public static final long DEFAULT_PAYMENT_TTL_MS = 2 * 60 * 1000L; // hold time granted once payment starts
	private static final int MAX_SEATS_PER_BOOKING = 10;

	private final InMemoryShowRepository showRepository;
	private final InMemoryBookingRepository bookingRepository;
	private final PaymentGateway paymentGateway;
	private final long lockTtlMs;
	private final long paymentTtlMs;

	private final AtomicLong bookingIds = new AtomicLong(1);
	// bookings still waiting for payment; bounded by the number of seats currently held
	private final ConcurrentHashMap<Long, Booking> pending = new ConcurrentHashMap<>();
	private final ScheduledExecutorService sweeper;

	public BookingService(InMemoryShowRepository showRepository, InMemoryBookingRepository bookingRepository,
			PaymentGateway paymentGateway) {
		this(showRepository, bookingRepository, paymentGateway, DEFAULT_LOCK_TTL_MS, DEFAULT_PAYMENT_TTL_MS);
	}

	public BookingService(InMemoryShowRepository showRepository, InMemoryBookingRepository bookingRepository,
			PaymentGateway paymentGateway, long lockTtlMs, long paymentTtlMs) {
		this.showRepository = showRepository;
		this.bookingRepository = bookingRepository;
		this.paymentGateway = paymentGateway;
		this.lockTtlMs = lockTtlMs;
		this.paymentTtlMs = paymentTtlMs;
		this.sweeper = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "booking-expiry-sweeper");
			t.setDaemon(true);
			return t;
		});
		this.sweeper.scheduleWithFixedDelay(this::sweepExpired, 200, 200, TimeUnit.MILLISECONDS);
	}

	public void shutdown() {
		sweeper.shutdownNow();
	}

	/** Seats of a show that can be booked right now (expired holds count as available). */
	public List<Seat> getAvailableSeats(long showId) {
		Show show = findShow(showId);
		List<Seat> available = new ArrayList<>();
		for (ShowSeat ss : show.getShowSeats()) {
			if (ss.isAvailable()) {
				available.add(ss.getSeat());
			}
		}
		return available;
	}

	/**
	 * Step 1: hold the seats for the user (all of them or none). Returns a PENDING_PAYMENT booking that must be
	 * paid before it expires.
	 *
	 * @throws SeatUnavailableException if any requested seat is booked or held by someone else
	 */
	public Booking bookSeats(long showId, List<Long> seatIds, String userId) {
		if (seatIds == null || seatIds.isEmpty() || seatIds.size() > MAX_SEATS_PER_BOOKING) {
			throw new IllegalArgumentException("Select between 1 and " + MAX_SEATS_PER_BOOKING + " seats");
		}
		Show show = findShow(showId);
		TreeSet<Long> orderedIds = new TreeSet<>(seatIds); // sorted + duplicates removed

		long bookingId = bookingIds.getAndIncrement();
		List<ShowSeat> held = new ArrayList<>(orderedIds.size());
		long amount = 0;

		for (Long seatId : orderedIds) {
			ShowSeat showSeat = show.getShowSeat(seatId);
			if (showSeat == null) {
				releaseAll(held, bookingId);
				throw new IllegalArgumentException("Seat " + seatId + " does not exist in this show");
			}
			if (!showSeat.tryLock(bookingId, lockTtlMs)) {
				releaseAll(held, bookingId); // roll back: all or nothing
				throw new SeatUnavailableException("Seat " + seatId + " is not available");
			}
			held.add(showSeat);
			amount += showSeat.getSeat().getSeatCategory().getPrice();
		}

		long now = System.currentTimeMillis();
		Booking booking = new Booking(bookingId, show, userId, held, amount, now, now + lockTtlMs);
		bookingRepository.save(booking);
		pending.put(bookingId, booking);
		return booking;
	}

	/**
	 * Step 2: pay for a held booking. Safe to call twice (a CONFIRMED booking just returns its payment); only
	 * one concurrent call can win the PENDING_PAYMENT -> PAYMENT_IN_PROGRESS transition.
	 */
	public Payment pay(long bookingId, String paymentMethod) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new IllegalArgumentException("Booking " + bookingId + " not found or expired"));

		if (booking.getStatus() == Booking.Status.CONFIRMED) {
			return booking.getPayment();
		}
		if (!booking.transition(Booking.Status.PENDING_PAYMENT, Booking.Status.PAYMENT_IN_PROGRESS)) {
			throw new IllegalStateException("Booking " + bookingId + " is " + booking.getStatus() + ", cannot pay");
		}
		pending.remove(bookingId); // the expiry sweeper must leave it alone from now on

		boolean confirmed = false;
		try {
			// keep the seats ours while the gateway is being called
			for (ShowSeat s : booking.getSeats()) {
				if (!s.extendLock(bookingId, paymentTtlMs)) {
					booking.setStatus(Booking.Status.EXPIRED);
					throw new IllegalStateException("Seat hold expired, please select seats again");
				}
			}

			Payment payment = paymentGateway.charge(bookingId, booking.getAmount(), paymentMethod);
			if (payment.getStatus() != Payment.Status.SUCCESS) {
				booking.setStatus(Booking.Status.FAILED);
				return payment; // the finally block frees the seats
			}

			if (!confirmAll(booking)) {
				paymentGateway.refund(payment);
				booking.setStatus(Booking.Status.FAILED);
				throw new IllegalStateException("Seats could not be confirmed, payment refunded");
			}

			booking.setPayment(payment);
			booking.setStatus(Booking.Status.CONFIRMED);
			confirmed = true;
			return payment;
		} finally {
			if (!confirmed) {
				if (booking.getStatus() == Booking.Status.PAYMENT_IN_PROGRESS) {
					booking.setStatus(Booking.Status.FAILED); // gateway threw an exception
				}
				releaseAll(booking.getSeats(), bookingId);
				bookingRepository.delete(bookingId);
			}
		}
	}

	/** User gave up before paying: free the seats immediately. */
	public void cancelBooking(long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new IllegalArgumentException("Booking " + bookingId + " not found or expired"));
		if (!booking.transition(Booking.Status.PENDING_PAYMENT, Booking.Status.CANCELLED)) {
			throw new IllegalStateException("Booking " + bookingId + " is " + booking.getStatus() + ", cannot cancel");
		}
		pending.remove(bookingId);
		releaseAll(booking.getSeats(), bookingId);
		bookingRepository.delete(bookingId);
	}

	/** All seats become BOOKED, or (if one hold was lost) none of them. */
	private boolean confirmAll(Booking booking) {
		long bookingId = booking.getBookingId();
		List<ShowSeat> done = new ArrayList<>(booking.getSeats().size());
		for (ShowSeat s : booking.getSeats()) {
			if (s.confirm(bookingId)) {
				done.add(s);
			} else {
				for (ShowSeat d : done) {
					d.revertBooked(bookingId);
				}
				return false;
			}
		}
		return true;
	}

	private void releaseAll(List<ShowSeat> seats, long bookingId) {
		for (ShowSeat s : seats) {
			s.release(bookingId);
		}
	}

	/** Runs every 200 ms: frees seats of bookings that were never paid. */
	private void sweepExpired() {
		try {
			long now = System.currentTimeMillis();
			for (Booking b : pending.values()) {
				if (b.getExpiresAt() <= now && b.transition(Booking.Status.PENDING_PAYMENT, Booking.Status.EXPIRED)) {
					pending.remove(b.getBookingId());
					releaseAll(b.getSeats(), b.getBookingId());
					bookingRepository.delete(b.getBookingId());
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace(); // never let the scheduled task die
		}
	}

	private Show findShow(long showId) {
		return showRepository.findById(showId)
				.orElseThrow(() -> new IllegalArgumentException("Show " + showId + " not found"));
	}
}