package com.book.show;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

import com.book.show.exception.SeatUnavailableException;
import com.book.show.modal.Booking;
import com.book.show.modal.City;
import com.book.show.modal.Movie;
import com.book.show.modal.Screen;
import com.book.show.modal.Seat;
import com.book.show.modal.SeatCategory;
import com.book.show.modal.Show;
import com.book.show.modal.StandardSeatCategory;
import com.book.show.modal.Theater;
import com.book.show.modal.TwoDHindiMovie;
import com.book.show.repository.InMemoryBookingRepository;
import com.book.show.repository.InMemoryCatalogRepository;
import com.book.show.repository.InMemoryShowRepository;
import com.book.show.service.BookingService;
import com.book.show.service.ShowService;
import com.book.show.service.SimulatedPaymentGateway;

/**
 * Hammers BookingService from many threads, then checks that no seat was sold twice.
 * Run: java -Xmx2g -cp out com.book.show.LoadTest
 */
public class LoadTest {
	static final int SCREENS = 500, SEATS_PER_SCREEN = 200, SECONDS = 5;

	public static void main(String[] args) throws Exception {
		InMemoryCatalogRepository catalog = new InMemoryCatalogRepository();
		InMemoryShowRepository showRepo = new InMemoryShowRepository();
		InMemoryBookingRepository bookingRepo = new InMemoryBookingRepository();
		ShowService showService = new ShowService(catalog, showRepo);
		// short hold time so the expiry sweeper is exercised too
		BookingService bookingService = new BookingService(showRepo, bookingRepo, new SimulatedPaymentGateway(), 300, 5000);

		SeatCategory std = new StandardSeatCategory("Standard", 250);
		Movie movie = new Movie(1L, "LoadMovie", 7200L, new TwoDHindiMovie());
		catalog.addMovie(movie);
		Theater theater = new Theater(1L, "T1");
		List<Screen> screens = new ArrayList<>();
		for (int i = 1; i <= SCREENS; i++) {
			Screen s = new Screen(i, "S" + i);
			for (int k = 1; k <= SEATS_PER_SCREEN; k++) {
				s.addSeat(new Seat((long) k, (k - 1) / 20 + 1, (k - 1) % 20 + 1, std));
			}
			theater.addScreen(s);
			screens.add(s);
		}
		City city = new City(1L, "LoadCity");
		city.addTheater(theater);
		catalog.addCity(city);

		List<Show> shows = new ArrayList<>();
		for (Screen s : screens) {
			shows.add(showService.createShow(movie, s, "10/10/2026", "10:00", "13:00"));
		}

		int threads = Runtime.getRuntime().availableProcessors() * 2;
		LongAdder attempts = new LongAdder(), held = new LongAdder(), rejected = new LongAdder(),
				paid = new LongAdder(), payFailed = new LongAdder(), cancelled = new LongAdder();
		long end = System.nanoTime() + SECONDS * 1_000_000_000L;

		List<Thread> workers = new ArrayList<>();
		for (int t = 0; t < threads; t++) {
			final String user = "u" + t;
			Thread th = new Thread(() -> {
				ThreadLocalRandom r = ThreadLocalRandom.current();
				while (System.nanoTime() < end) {
					Show show = shows.get(r.nextInt(shows.size()));
					int n = 1 + r.nextInt(4);
					List<Long> ids = new ArrayList<>(n);
					for (int i = 0; i < n; i++) { // 30% of picks target the 20 "best" seats (hot seats)
						ids.add(1L + (r.nextInt(100) < 30 ? r.nextInt(20) : r.nextInt(SEATS_PER_SCREEN)));
					}
					attempts.increment();
					try {
						Booking b = bookingService.bookSeats(show.getShowId(), ids, user);
						held.increment();
						int dice = r.nextInt(100);
						if (dice < 70) {
							try {
								bookingService.pay(b.getBookingId(), "CARD");
								paid.increment();
							} catch (IllegalStateException e) {
								payFailed.increment(); // hold expired before pay: legitimate
							}
						} else if (dice < 85) {
							try {
								bookingService.cancelBooking(b.getBookingId());
								cancelled.increment();
							} catch (IllegalStateException e) {
								// expired in the meantime
							}
						} // else: abandoned, the sweeper frees it
					} catch (SeatUnavailableException e) {
						rejected.increment();
					}
				}
			});
			workers.add(th);
			th.start();
		}
		for (Thread th : workers) {
			th.join();
		}
		Thread.sleep(800); // let the sweeper release abandoned holds
		bookingService.shutdown();

		// ---- verification ----
		Set<String> sold = new HashSet<>();
		long confirmedSeats = 0;
		boolean ok = true;
		for (Booking b : bookingRepo.findAll()) {
			if (b.getStatus() != Booking.Status.CONFIRMED) {
				System.out.println("Leftover non-confirmed booking: " + b.getBookingId() + " " + b.getStatus());
				ok = false;
				continue;
			}
			for (Long seatId : b.getSeatIds()) {
				confirmedSeats++;
				if (!sold.add(b.getShow().getShowId() + ":" + seatId)) {
					System.out.println("DOUBLE BOOKING " + b.getShow().getShowId() + ":" + seatId);
					ok = false;
				}
			}
		}
		long bookedInShows = 0, stillLocked = 0;
		for (Show show : shows) {
			for (com.book.show.modal.ShowSeat ss : show.getShowSeats()) {
				if (ss.isBooked()) {
					bookedInShows++;
				} else if (!ss.isAvailable()) {
					stillLocked++;
				}
			}
		}
		if (bookedInShows != confirmedSeats || stillLocked != 0) {
			ok = false;
		}

		System.out.println("threads=" + threads + " cores=" + Runtime.getRuntime().availableProcessors());
		System.out.printf("bookSeats calls: %,d  (%,d/sec)%n", attempts.sum(), attempts.sum() / SECONDS);
		System.out.printf("held=%,d rejected=%,d paid=%,d payFailed(expired)=%,d cancelled=%,d%n", held.sum(),
				rejected.sum(), paid.sum(), payFailed.sum(), cancelled.sum());
		System.out.printf("confirmed seats in bookings=%,d, BOOKED seats in shows=%,d, stuck locks=%d%n",
				confirmedSeats, bookedInShows, stillLocked);
		System.out.println(ok ? "RESULT: OK - no seat sold twice, no leaked holds" : "RESULT: FAILED");
	}
}