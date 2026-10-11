package com.food.delivery.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Inventory {
    private Long inventoryId;
    private String inventoryName;
    private final List<Item> items = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Inventory(String name) {
        this.inventoryName = name;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public List<Item> getItems() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(items));
        } finally {
            lock.readLock().unlock();
        }
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }

    public boolean addOrIncrementItem(Item incomingItem) {
        if (incomingItem == null || incomingItem.getItemId() == null || incomingItem.getQuantity() == null
                || incomingItem.getQuantity() <= 0) {
            return false;
        }

        lock.writeLock().lock();
        try {
            for (Item item : items) {
                if (item.getItemId().equals(incomingItem.getItemId())) {
                    item.setQuantity(item.getQuantity() + incomingItem.getQuantity());
                    return true;
                }
            }

            items.add(new Item(incomingItem.getItemName(), incomingItem.getQuantity(), incomingItem.getRestaurant(), incomingItem.getItemId()));
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean tryReserveItem(Long itemId, long quantity) {
        if (itemId == null || quantity <= 0) {
            return false;
        }

        lock.writeLock().lock();
        try {
            for (Item item : items) {
                if (item.getItemId().equals(itemId)) {
                    if (item.getQuantity() < quantity) {
                        return false;
                    }
                    item.setQuantity(item.getQuantity() - quantity);
                    return true;
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean restoreReservedItem(Long itemId, long quantity) {
        if (itemId == null || quantity <= 0) {
            return false;
        }

        lock.writeLock().lock();
        try {
            for (Item item : items) {
                if (item.getItemId().equals(itemId)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    return true;
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
