package com.lld.lbm.modal;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a physical library branch.
 * Holds its own inventory of {@link BookItem}s.
 */
public class Library {

    private final String branchId;
    private final String name;
    private final String address;

    // itemId → BookItem; ConcurrentHashMap for safe concurrent reads/writes
    private final ConcurrentHashMap<String, BookItem> inventory = new ConcurrentHashMap<>();

    public Library(String branchId, String name, String address) {
        this.branchId = branchId;
        this.name     = name;
        this.address  = address;
    }

    public void addBookItem(BookItem item) {
        if (!item.getBranchId().equals(branchId)) {
            throw new IllegalArgumentException(
                    "BookItem branch mismatch: expected " + branchId
                    + " but got " + item.getBranchId());
        }
        inventory.put(item.getItemId(), item);
    }

    public BookItem getBookItem(String itemId) {
        return inventory.get(itemId);
    }

    public Collection<BookItem> getAllItems() {
        return Collections.unmodifiableCollection(inventory.values());
    }

    public String getBranchId() { return branchId; }
    public String getName()     { return name; }
    public String getAddress()  { return address; }

    @Override
    public String toString() {
        return String.format("Library{id='%s', name='%s', items=%d}",
                branchId, name, inventory.size());
    }
}