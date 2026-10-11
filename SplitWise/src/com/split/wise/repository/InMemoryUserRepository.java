package com.split.wise.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.split.wise.split.User;

public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentMap<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public User save(User user) {
        if (user.getUser_id() == null) {
            long id = idGen.getAndIncrement();
            user.setUser_id(id);
        }
        store.put(user.getUser_id(), user);
        return user;
    }

    public User findById(Long id) {
        return store.get(id);
    }

    public Collection<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(Long id) {
        store.remove(id);
    }
}
