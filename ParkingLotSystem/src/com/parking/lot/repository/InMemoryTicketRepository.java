package com.parking.lot.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.parking.lot.model.Ticket;

public class InMemoryTicketRepository implements TicketRepository {
	private ConcurrentHashMap<Long, Ticket> store = new ConcurrentHashMap<>();
	private AtomicLong id = new AtomicLong(1);
	
	@Override
	public Ticket save(Ticket slot) {
		// TODO Auto-generated method stub
		Long slotId = slot.getTicketId();
		if(slotId == null) {
			slotId = id.incrementAndGet();
			slot.setTicketId(slotId);
		}
		store.put(slotId, slot);
		return slot;
	}

	@Override
	public List<Ticket> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(store.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		store.remove(id);
	}

	@Override
	public Optional<Ticket> findById(Long id) {
		// TODO Auto-generated method stub
		Ticket slot = store.get(id);
		if(slot == null) {
			return Optional.empty();
		}
		return Optional.of(slot);
	}

}
