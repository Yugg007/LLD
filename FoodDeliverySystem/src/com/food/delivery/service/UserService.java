package com.food.delivery.service;

import com.food.delivery.model.User;
import com.food.delivery.repository.InMemoryUserRepository;

public class UserService {
	private final InMemoryUserRepository userRepository;
	
	public UserService() {
		this.userRepository = new InMemoryUserRepository();
	}
	
	public User saveUser(String name) {
		User user = new User(name);
		user = userRepository.save(user);
		return user;
	}

}
