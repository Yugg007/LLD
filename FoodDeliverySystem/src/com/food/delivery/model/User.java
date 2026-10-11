package com.food.delivery.model;

public class User {
	private Long userId;
	private String userName;

	public User(String name) {
		// TODO Auto-generated constructor stub
		this.userName = name;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + "]";
	}
	
	
	

}