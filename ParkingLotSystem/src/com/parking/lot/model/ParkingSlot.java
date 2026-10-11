package com.parking.lot.model;

import com.parking.lot.config.RateConfig;
import com.parking.lot.enums.ParkingType;
import com.parking.lot.enums.SlotStatus;
import com.parking.lot.enums.WheelType;

public class ParkingSlot {
	private Long slotId;
	private ParkingType parkingType;
	private Vehicle vehicle;
	private WheelType wheelType;
	private SlotStatus status;
	private Long hourlyRate;

	public Long getSlotId() {
		return slotId;
	}

	public void setSlotId(Long slotId) {
		this.slotId = slotId;
	}

	public ParkingType getParkingType() {
		return parkingType;
	}

	public void setParkingType(ParkingType parkingType) {
		this.parkingType = parkingType;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public WheelType getWheelType() {
		return wheelType;
	}

	public void setWheelType(WheelType wheelType) {
		this.wheelType = wheelType;
	}

	public SlotStatus getStatus() {
		return status;
	}

	public void setStatus(SlotStatus status) {
		this.status = status;
	}

	public Long getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(Long hourlyRate) {
		this.slotId = hourlyRate;
	}

	public ParkingSlot(Long slotId, ParkingType parkingType, WheelType wheelType) {
		super();
		this.slotId = slotId;
		this.parkingType = parkingType;
		this.wheelType = wheelType;
		this.status = SlotStatus.AVAILABLE;
		this.hourlyRate = RateConfig.getPrice(parkingType);
	}

}
