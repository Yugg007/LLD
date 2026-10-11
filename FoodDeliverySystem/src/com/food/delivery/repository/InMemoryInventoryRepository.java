package com.food.delivery.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.food.delivery.model.Inventory;

public class InMemoryInventoryRepository implements InventoryRepository {
	private ConcurrentHashMap<Long, Inventory> inventoryStore = new ConcurrentHashMap<>();
	private AtomicLong idGen = new AtomicLong(0);
	
	@Override
	public Inventory save(Inventory inventory) {
		// TODO Auto-generated method stub
		if(inventory.getInventoryId() == null) {
			inventory.setInventoryId(idGen.incrementAndGet());
		}
		inventoryStore.put(inventory.getInventoryId(), inventory);
		return inventory;
	}

	@Override
	public List<Inventory> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(inventoryStore.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		inventoryStore.remove(id);
		
	}

	@Override
	public Optional<Inventory> findById(Long id) {
		// TODO Auto-generated method stub
		if(!inventoryStore.containsKey(id)) {
			return Optional.empty();
		}
		
		return Optional.of(inventoryStore.get(id));
		
	}

}
