package com.lld.lbm.repository;


import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.lld.lbm.modal.Library;

/**
 * Thread-safe, in-memory store for {@link Library} branches.
 */
public class LibraryRepository implements Repository<Library, String> {

    private final ConcurrentHashMap<String, Library> store = new ConcurrentHashMap<>();

    @Override
    public void save(Library library) {
        store.put(library.getBranchId(), library);
    }

    @Override
    public Optional<Library> findById(String branchId) {
        return Optional.ofNullable(store.get(branchId));
    }

    @Override
    public Collection<Library> findAll() {
        return Collections.unmodifiableCollection(store.values());
    }

    @Override
    public void delete(String branchId) {
        store.remove(branchId);
    }
}