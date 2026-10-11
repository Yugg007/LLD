package com.food.delivery.service;

import com.food.delivery.model.Inventory;
import com.food.delivery.model.Item;
import com.food.delivery.repository.InMemoryInventoryRepository;

public class InventoryService {
    private final InMemoryInventoryRepository repository;

    public InventoryService() {
        this.repository = new InMemoryInventoryRepository();
    }

    public Inventory saveInventory(String name) {
        Inventory inv = new Inventory(name);
        return repository.save(inv);
    }

    public void addItemToInventory(Inventory inventory, Item item) {
        if (inventory == null || item == null || item.getItemId() == null) {
            throw new IllegalArgumentException("Inventory and item must be non-null and item must have an id");
        }

        boolean updated = inventory.addOrIncrementItem(item);
        if (!updated) {
            throw new IllegalStateException("Unable to add item to inventory");
        }

        repository.save(inventory);
    }

    public boolean tryReserveItem(Inventory inventory, Item item, long quantity) {
        if (inventory == null || item == null || item.getItemId() == null || quantity <= 0) {
            throw new IllegalArgumentException("Inventory, item, and quantity must be valid");
        }

        boolean reserved = inventory.tryReserveItem(item.getItemId(), quantity);
        if (reserved) {
            repository.save(inventory);
        }
        return reserved;
    }

    public boolean restoreReservedItem(Inventory inventory, Item item, long quantity) {
        if (inventory == null || item == null || item.getItemId() == null || quantity <= 0) {
            throw new IllegalArgumentException("Inventory, item, and quantity must be valid");
        }

        boolean restored = inventory.restoreReservedItem(item.getItemId(), quantity);
        if (restored) {
            repository.save(inventory);
        }
        return restored;
    }
}
