package com.split.wise.service;

import com.split.wise.dto.UserDto;
import com.split.wise.repository.InMemoryUserRepository;
import com.split.wise.repository.UserRepository;
import com.split.wise.split.User;

public class UserService {
	private final UserRepository repo;

	public UserService() {
		this.repo = new InMemoryUserRepository();
	}

	public UserService(UserRepository repo) {
		this.repo = repo;
	}

	public User createUser(Long id, String name) {
		User u = new User(id, name);
		return repo.save(u);
	}

	public UserDto toDto(User user) {
		if (user == null) return null;
		return new UserDto(user.getUser_id(), user.getName());
	}

	public User getUser(Long id) {
		return repo.findById(id);
	}

	public void deleteUser(Long id) {
		repo.delete(id);
	}
}
