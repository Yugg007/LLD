package com.split.wise.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GroupLockManager {
    private final ConcurrentMap<Long, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();

    public ReentrantReadWriteLock getLockForGroup(Long groupId) {
        return locks.computeIfAbsent(groupId, id -> new ReentrantReadWriteLock());
    }
}
