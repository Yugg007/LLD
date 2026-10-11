package com.split.wise.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.split.wise.split.Group;

public class InMemoryGroupRepository implements GroupRepository {
    private final ConcurrentMap<Long, Group> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public Group save(Group g) {
        if (g.getGroup_id() == null) {
            long id = idGen.getAndIncrement();
            g.setGroup_id(id);
        }
        store.put(g.getGroup_id(), g);
        return g;
    }

    public Group findById(Long id) {
        return store.get(id);
    }

    public Collection<Group> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(Long id) {
        store.remove(id);
    }
}
