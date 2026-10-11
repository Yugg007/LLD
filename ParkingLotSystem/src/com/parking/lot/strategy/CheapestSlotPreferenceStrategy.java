package com.parking.lot.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.parking.lot.enums.SlotStatus;
import com.parking.lot.enums.WheelType;
import com.parking.lot.model.ParkingSlot;

public class CheapestSlotPreferenceStrategy extends SlotPreferenceStrategy {

	@Override
	public Optional<ParkingSlot> findSlot(List<ParkingSlot> slots, WheelType wheelType) {
		// TODO Auto-generated method stub
		if (slots == null || slots.isEmpty())
			return Optional.empty();

		return slots.stream().filter(
				slot -> (slot.getWheelType().equals(wheelType) && SlotStatus.AVAILABLE.equals(slot.getStatus())))
				.min(Comparator.comparingLong(ParkingSlot::getHourlyRate));
	}

}
