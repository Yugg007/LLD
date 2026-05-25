package com.lld.lbm.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.lld.lbm.modal.BorrowRecord;

/**
 * Thread-safe, in-memory store for {@link BorrowRecord}s.
 */
public class BorrowRecordRepository implements Repository<BorrowRecord, String> {

    private final ConcurrentHashMap<String, BorrowRecord> store = new ConcurrentHashMap<>();

    @Override
    public void save(BorrowRecord record) {
        store.put(record.getRecordId(), record);
    }

    @Override
    public Optional<BorrowRecord> findById(String recordId) {
        return Optional.ofNullable(store.get(recordId));
    }

    @Override
    public Collection<BorrowRecord> findAll() {
        return Collections.unmodifiableCollection(store.values());
    }

    @Override
    public void delete(String recordId) {
        store.remove(recordId);
    }

    /** Find the active (not yet returned) record for a specific book item. */
    public Optional<BorrowRecord> findActiveByItemId(String itemId) {
        return store.values().stream()
                .filter(r -> r.getBookItemId().equals(itemId) && !r.isReturned())
                .findFirst();
    }

    /** All records (active and historical) for a given member. */
    public List<BorrowRecord> findByMemberId(String memberId) {
        return store.values().stream()
                .filter(r -> r.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }
}