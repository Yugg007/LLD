package com.parking.lot.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParkingSlotLockManager {
	private ConcurrentHashMap<Long, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();

	
	public ReentrantReadWriteLock getSlotLock(Long slotId) {
		return locks.computeIfAbsent(slotId, id -> new ReentrantReadWriteLock());
	}
	
	private ParkingSlotLockManager() {}

    private static class Holder {
        private static final ParkingSlotLockManager INSTANCE = new ParkingSlotLockManager();
    }

    public static ParkingSlotLockManager getInstance() {
        return Holder.INSTANCE;
    }

}
