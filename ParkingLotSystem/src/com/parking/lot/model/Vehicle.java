package com.parking.lot.model;

import com.parking.lot.enums.WheelType;

public class Vehicle {
	private Long vehicle_no;
	private String vehicle_name;
	private WheelType wheel_type;
	private User user;

	public Long getVehicle_no() {
		return vehicle_no;
	}

	public String getVehicle_name() {
		return vehicle_name;
	}

	public void setVehicle_name(String vehicle_name) {
		this.vehicle_name = vehicle_name;
	}
	
	

	public WheelType getWheel_type() {
		return wheel_type;
	}

	public void setWheel_type(WheelType wheel_type) {
		this.wheel_type = wheel_type;
	}
	
	
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Vehicle(Long vehicle_no, String vehicle_name, WheelType wheel_type, User user) {
		super();
		this.vehicle_no = vehicle_no;
		this.vehicle_name = vehicle_name;
		this.wheel_type = wheel_type;
		this.user = user;
	}

}
