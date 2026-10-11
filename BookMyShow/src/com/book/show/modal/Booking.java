package com.book.show.modal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A user's claim on a set of seats of one show. The status is changed only through CAS transitions, so two
 * threads (e.g. "pay" and "expiry sweeper") can never both win.
 */
public class Booking {
	public enum Status {
		PENDING_PAYMENT, PAYMENT_IN_PROGRESS, CONFIRMED, FAILED, EXPIRED, CANCELLED
	}

	private final long bookingId;
	private final Show show;
	private final String userId;
	private final List<ShowSeat> seats;
	private final long amount;
	private final long createdAt;
	private final long expiresAt;
	private final AtomicReference<Status> status = new AtomicReference<>(Status.PENDING_PAYMENT);
	private volatile Payment payment;

	public Booking(long bookingId, Show show, String userId, List<ShowSeat> seats, long amount, long createdAt,
			long expiresAt) {
		this.bookingId = bookingId;
		this.show = show;
		this.userId = userId;
		this.seats = seats;
		this.amount = amount;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}

	public boolean transition(Status from, Status to) {
		return status.compareAndSet(from, to);
	}

	public void setStatus(Status newStatus) {
		status.set(newStatus);
	}

	public Status getStatus() {
		return status.get();
	}

	public List<Long> getSeatIds() {
		List<Long> ids = new ArrayList<>(seats.size());
		for (ShowSeat s : seats) {
			ids.add(s.getSeat().getId());
		}
		return ids;
	}

	public long getBookingId() {
		return bookingId;
	}

	public Show getShow() {
		return show;
	}

	public String getUserId() {
		return userId;
	}

	public List<ShowSeat> getSeats() {
		return seats;
	}

	public long getAmount() {
		return amount;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public long getExpiresAt() {
		return expiresAt;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
}