package com.food.delivery.model;

import java.util.List;

public class Restaurant {
	private Long restaurantId;
	private String name;
	private List<Item> items;
	private Address address;

	public Restaurant(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	public Long getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Long restaurantId) {
		this.restaurantId = restaurantId;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
