package com.parking.lot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.parking.lot.strategy.NearestSlotPreferenceStrategy;
import com.parking.lot.strategy.SlotPreferenceStrategy;

public class ParkingLot {
	private final ReentrantLock lock = new ReentrantLock();
	private Long id;
	private List<ParkingSlot> slots;
	private SlotPreferenceStrategy strategy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ParkingSlot> getSlots() {
		return slots;
	}
	
	public void addSlot(ParkingSlot ps) {
		this.slots.add(ps);
	}

	public ParkingLot(Long id) {
		super();
		this.id = id;
		this.slots = new ArrayList<>();
		this.strategy = new NearestSlotPreferenceStrategy();
	}
	
	public ParkingLot(Long id, SlotPreferenceStrategy strategy) {
		super();
		this.id = id;
		this.slots = new ArrayList<>();
		this.strategy = strategy;
	}

	public SlotPreferenceStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(SlotPreferenceStrategy strategy) {
		this.strategy = strategy;
	}
	
	public ReentrantLock getLock() {
        return lock;
    }
}
