package com.parking.lot.model;

public class User {
	private Long user_id;
	private String user_name;

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public User(Long user_id, String user_name) {
		super();
		this.user_id = user_id;
		this.user_name = user_name;
	}

}
