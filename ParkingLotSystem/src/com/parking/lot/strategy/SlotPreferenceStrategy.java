package com.parking.lot.strategy;

import java.util.List;
import java.util.Optional;

import com.parking.lot.enums.WheelType;
import com.parking.lot.model.ParkingSlot;

public abstract class SlotPreferenceStrategy implements SlotPreferenceInterface {
	public abstract Optional<ParkingSlot> findSlot(List<ParkingSlot> slots, WheelType wheelType);
}
