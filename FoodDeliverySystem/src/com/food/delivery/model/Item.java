package com.food.delivery.model;

public class Item {
	private Long itemId;
	private String itemName;
	private Long quantity;
	private Restaurant restaurant;

	public Item(String itemName, Long quantity, Restaurant restaurant) {
		this.itemName = itemName;
		this.quantity = quantity;
		this.restaurant = restaurant;
	}

	public Item(String itemName, Long quantity, Restaurant restaurant, Long itemId) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.quantity = quantity;
		this.restaurant = restaurant;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

}
