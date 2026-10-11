package com.book.show.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.book.show.modal.Booking;

public class InMemoryBookingRepository implements Repository<Booking> {
	private final ConcurrentHashMap<Long, Booking> store = new ConcurrentHashMap<>();

	@Override
	public Booking save(Booking booking) {
		store.put(booking.getBookingId(), booking);
		return booking;
	}

	@Override
	public List<Booking> findAll() {
		return new ArrayList<>(store.values());
	}

	@Override
	public void delete(Long id) {
		store.remove(id);
	}

	@Override
	public Optional<Booking> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}
}