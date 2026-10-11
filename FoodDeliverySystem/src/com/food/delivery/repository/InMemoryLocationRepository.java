package com.food.delivery.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.food.delivery.model.Location;

public class InMemoryLocationRepository implements LocationRepository {
	private ConcurrentHashMap<Long, Location> locationStore = new ConcurrentHashMap<>();
	private AtomicLong idGen = new AtomicLong(0);
	
	@Override
	public Location save(Location location) {
		// TODO Auto-generated method stub
		if(location.getLocationId() == null) {
			location.setLocationId(idGen.incrementAndGet());
		}
		locationStore.put(location.getLocationId(), location);
		return location;
	}

	@Override
	public List<Location> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(locationStore.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		locationStore.remove(id);
		
	}

	@Override
	public Optional<Location> findById(Long id) {
		// TODO Auto-generated method stub
		if(!locationStore.containsKey(id)) {
			return Optional.empty();
		}
		
		return Optional.of(locationStore.get(id));
		
	}

}
