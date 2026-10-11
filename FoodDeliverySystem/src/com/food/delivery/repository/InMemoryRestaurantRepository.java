package com.food.delivery.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.food.delivery.model.Restaurant;

public class InMemoryRestaurantRepository implements RestaurantRepository {
	private ConcurrentHashMap<Long, Restaurant> restaurantStore = new ConcurrentHashMap<>();
	private AtomicLong idGen = new AtomicLong(0);
	
	@Override
	public Restaurant save(Restaurant restaurant) {
		// TODO Auto-generated method stub
		if(restaurant.getRestaurantId() == null) {
			restaurant.setRestaurantId(idGen.incrementAndGet());
		}
		restaurantStore.put(restaurant.getRestaurantId(), restaurant);
		return restaurant;
	}

	@Override
	public List<Restaurant> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(restaurantStore.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		restaurantStore.remove(id);
		
	}

	@Override
	public Optional<Restaurant> findById(Long id) {
		// TODO Auto-generated method stub
		if(!restaurantStore.containsKey(id)) {
			return Optional.empty();
		}
		
		return Optional.of(restaurantStore.get(id));
		
	}

}
