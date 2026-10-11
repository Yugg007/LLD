package com.parking.lot.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.parking.lot.model.ParkingSlot;

public class InMemoryParkingSlotRepository implements ParkingSlotRepository {
	private ConcurrentHashMap<Long, ParkingSlot> store = new ConcurrentHashMap<>();
	private AtomicLong id = new AtomicLong(1);

	@Override
	public ParkingSlot save(ParkingSlot slot) {
		// TODO Auto-generated method stub
		Long slotId = slot.getSlotId();
		if(slotId == null) {
			slotId = id.incrementAndGet();
			slot.setSlotId(slotId);
		}
		store.put(slotId, slot);
		return slot;
	}

	@Override
	public List<ParkingSlot> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(store.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		store.remove(id);
		
	}

	@Override
	public Optional<ParkingSlot> findById(Long id) {
		// TODO Auto-generated method stub
		ParkingSlot slot = store.get(id);
		if(slot == null) {
			return Optional.empty();
		}
		return Optional.of(slot);
	}

}
