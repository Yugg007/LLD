package com.split.wise.split;

public class User {
	private Long user_id;
	private String name;

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User(Long user_id, String name) {
		super();
		this.user_id = user_id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "User{id=" + user_id + ", name=" + name + "}";
	}

}
