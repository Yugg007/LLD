package com.food.delivery.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.food.delivery.model.User;

public class InMemoryUserRepository implements UserRepository {
	private ConcurrentHashMap<Long, User> userStore = new ConcurrentHashMap<>();
	private AtomicLong idGen = new AtomicLong(0);

	@Override
	public User save(User user) {
		// TODO Auto-generated method stub
		if(user.getUserId() == null) {
			user.setUserId(idGen.incrementAndGet());
		}
		userStore.put(user.getUserId(), user);
		return user;
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(userStore.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		userStore.remove(id);
		
	}

	@Override
	public Optional<User> findById(Long id) {
		// TODO Auto-generated method stub
		if(!userStore.containsKey(id)) {
			return Optional.empty();
		}
		
		return Optional.of(userStore.get(id));
		
	}

}
