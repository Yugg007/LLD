package com.food.delivery.service;

import com.food.delivery.model.Restaurant;
import com.food.delivery.repository.InMemoryRestaurantRepository;

public class RestaurantService {
	private InMemoryRestaurantRepository restaurantRepository;
	
	public RestaurantService() {
		this.restaurantRepository = new InMemoryRestaurantRepository();
	}
	
	public Restaurant saveRestaurant(String name) {
		Restaurant restaurant = new Restaurant(name);
		restaurant = restaurantRepository.save(restaurant);
		
		return restaurant;
	}

}
