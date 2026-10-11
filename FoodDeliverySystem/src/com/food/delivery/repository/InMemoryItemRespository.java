package com.food.delivery.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.food.delivery.model.Item;

public class InMemoryItemRespository implements ItemRespository {
	private ConcurrentHashMap<Long, Item> itemStore = new ConcurrentHashMap<>();
	private AtomicLong idGen = new AtomicLong(0);
	
	@Override
	public Item save(Item item) {
		// TODO Auto-generated method stub
		if(item.getItemId()== null) {
			item.setItemId(idGen.incrementAndGet());
		}
		itemStore.put(item.getItemId(), item);
		return item;
	}

	@Override
	public List<Item> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(itemStore.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		itemStore.remove(id);
		
	}

	@Override
	public Optional<Item> findById(Long id) {
		// TODO Auto-generated method stub
		if(!itemStore.containsKey(id)) {
			return Optional.empty();
		}
		
		return Optional.of(itemStore.get(id));
		
	}

}
