package com.book.show.modal;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Booking state of ONE physical seat for ONE show.
 *
 * All state changes are lock-free compare-and-set (CAS) operations on an immutable SeatState, so thousands of
 * threads can fight for the same seat and exactly one wins, without any thread ever blocking.
 *
 * AVAILABLE --tryLock--> LOCKED (held by a booking, expires) --confirm--> BOOKED
 *                          \--release / expiry--> AVAILABLE
 */
public class ShowSeat {

	private static final class SeatState {
		static final SeatState AVAILABLE = new SeatState(BookStatus.AVAILABLE, 0L, 0L);

		final BookStatus status;
		final long bookingId;
		final long expiresAt; // only meaningful for LOCKED

		SeatState(BookStatus status, long bookingId, long expiresAt) {
			this.status = status;
			this.bookingId = bookingId;
			this.expiresAt = expiresAt;
		}
	}

	private final Seat seat;
	private final AtomicReference<SeatState> state = new AtomicReference<>(SeatState.AVAILABLE);

	public ShowSeat(Seat seat) {
		this.seat = seat;
	}

	/** Hold the seat for a booking. Fails if it is booked or held (not expired) by someone else. */
	public boolean tryLock(long bookingId, long ttlMillis) {
		while (true) {
			SeatState cur = state.get();
			long now = System.currentTimeMillis();
			if (cur.status == BookStatus.BOOKED) {
				return false;
			}
			if (cur.status == BookStatus.LOCKED && cur.expiresAt > now && cur.bookingId != bookingId) {
				return false;
			}
			if (state.compareAndSet(cur, new SeatState(BookStatus.LOCKED, bookingId, now + ttlMillis))) {
				return true;
			}
			// someone changed the state between get() and CAS: re-read and decide again
		}
	}

	/** Keep an existing, still-valid hold alive (used while a payment is in progress). */
	public boolean extendLock(long bookingId, long ttlMillis) {
		while (true) {
			SeatState cur = state.get();
			long now = System.currentTimeMillis();
			if (cur.status != BookStatus.LOCKED || cur.bookingId != bookingId || cur.expiresAt <= now) {
				return false;
			}
			if (state.compareAndSet(cur, new SeatState(BookStatus.LOCKED, bookingId, now + ttlMillis))) {
				return true;
			}
		}
	}

	/** LOCKED (by this booking, not expired) -> BOOKED. */
	public boolean confirm(long bookingId) {
		while (true) {
			SeatState cur = state.get();
			if (cur.status != BookStatus.LOCKED || cur.bookingId != bookingId
					|| cur.expiresAt <= System.currentTimeMillis()) {
				return false;
			}
			if (state.compareAndSet(cur, new SeatState(BookStatus.BOOKED, bookingId, 0L))) {
				return true;
			}
		}
	}

	/** Give the seat back. Does nothing if this booking no longer owns the hold. */
	public void release(long bookingId) {
		while (true) {
			SeatState cur = state.get();
			if (cur.status != BookStatus.LOCKED || cur.bookingId != bookingId) {
				return;
			}
			if (state.compareAndSet(cur, SeatState.AVAILABLE)) {
				return;
			}
		}
	}

	/** Undo a confirm() (only used to roll back a partly confirmed multi-seat booking). */
	public void revertBooked(long bookingId) {
		SeatState cur = state.get();
		if (cur.status == BookStatus.BOOKED && cur.bookingId == bookingId) {
			state.compareAndSet(cur, SeatState.AVAILABLE);
		}
	}

	/** Effective status: a LOCKED seat whose hold has expired counts as AVAILABLE. */
	public BookStatus getBooked() {
		SeatState cur = state.get();
		if (cur.status == BookStatus.LOCKED && cur.expiresAt <= System.currentTimeMillis()) {
			return BookStatus.AVAILABLE;
		}
		return cur.status;
	}

	public boolean isAvailable() {
		return getBooked() == BookStatus.AVAILABLE;
	}

	public boolean isBooked() {
		return state.get().status == BookStatus.BOOKED;
	}

	public Seat getSeat() {
		return seat;
	}
}