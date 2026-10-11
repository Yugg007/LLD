package com.food.delivery.service;

import com.food.delivery.model.Item;
import com.food.delivery.model.Restaurant;
import com.food.delivery.repository.InMemoryItemRespository;

public class ItemService {
	private final InMemoryItemRespository itemRepository;
	
	public ItemService() {
		this.itemRepository = new InMemoryItemRespository();
	}

	
	public Item saveItem(String itemName, Long quantity, Restaurant restaurant) {
		Item item = new Item(itemName, quantity, restaurant);
		item = itemRepository.save(item);
		return item;
	}

}
