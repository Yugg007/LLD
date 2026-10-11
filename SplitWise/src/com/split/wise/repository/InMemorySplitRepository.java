package com.split.wise.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.split.wise.split.Split;

public class InMemorySplitRepository implements SplitRepository {
    private final ConcurrentMap<Long, Split> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public Split save(Split s) {
        if (s.getSplit_id() == null) {
            long id = idGen.getAndIncrement();
            s.setSplit_id(id);
        }
        store.put(s.getSplit_id(), s);
        return s;
    }

    public Split findById(Long id) {
        return store.get(id);
    }

    public Collection<Split> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(Long id) {
        store.remove(id);
    }

    public List<Split> findByGroupId(Long groupId) {
        List<Split> res = new ArrayList<>();
        for (Split s : store.values()) {
            if (s.getGroup() != null && s.getGroup().getGroup_id() != null && s.getGroup().getGroup_id().equals(groupId)) {
                res.add(s);
            }
        }
        return res;
    }
}
