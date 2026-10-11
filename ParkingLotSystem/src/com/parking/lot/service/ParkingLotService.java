package com.parking.lot.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import com.parking.lot.enums.SlotStatus;
import com.parking.lot.enums.TicketState;
import com.parking.lot.model.ParkingLot;
import com.parking.lot.model.ParkingSlot;
import com.parking.lot.model.Ticket;
import com.parking.lot.model.Vehicle;
import com.parking.lot.utils.RateCalculator;

public class ParkingLotService {
	private final ParkingLot parkingLot;
	private final TicketService ticketService;

	public ParkingLotService(ParkingLot parkingLot) {
		// TODO Auto-generated constructor stub
		this.parkingLot = parkingLot;
		this.ticketService = new TicketService();
	}

	public void addSlot(ParkingSlot st) throws Exception {
		// TODO Auto-generated method stub

		List<ParkingSlot> slots = parkingLot.getSlots();
		boolean exist = slots.stream().anyMatch(slot -> slot.getSlotId().longValue() == st.getSlotId().longValue());
		if (exist) {
			throw new Exception("Slot already exist");
		}
		parkingLot.addSlot(st);
	}

	private ParkingSlot findSlotAndBook(Vehicle vehicle) throws Exception {
		ReentrantLock lock = parkingLot.getLock();

		lock.lock();
		try {
			List<ParkingSlot> slots = parkingLot.getSlots();
			Optional<ParkingSlot> available_slot = parkingLot.getStrategy().findSlot(slots, vehicle.getWheel_type());

			if (available_slot.isEmpty()) {
				throw new Exception("No slot available!!!");
			}

			ParkingSlot slot = available_slot.get();
			slot.setVehicle(vehicle);
			slot.setStatus(SlotStatus.PARKED);

			return slot;

		} finally {
			lock.unlock();

		}
	}

	public Ticket parkVehicle(Vehicle vehicle) throws Exception {
		// TODO Auto-generated method stub

		ParkingSlot slot = findSlotAndBook(vehicle);

		Ticket ticket = ticketService.generateTicket(slot);

		return ticket;
	}

	public void payAndUnPark(Ticket ticket, Long price_to_pay) throws Exception {
		// TODO Auto-generated method stub

		if (!TicketState.OPEN.equals(ticket.getContext().getCurrentState().getState())) {
			throw new Exception("Ticket is either paid or closed!!!");
		}

		Long ticket_price = RateCalculator.getPrice(ticket.getParkingAt(), ticket.getSlot().getHourlyRate());
		if (price_to_pay < ticket_price) {
			throw new Exception("Pay full price - " + ticket_price + " to unpark...");
		}

		if (TicketState.OPEN.equals(ticket.getContext().getCurrentState().getState())) {
			ticketService.payTicket(ticket);
		}

		String username = ticket.getSlot().getVehicle().getUser().getUser_name();

		ParkingSlot slot = ticket.getSlot();
		slot.setVehicle(null);
		slot.setStatus(SlotStatus.AVAILABLE);

		System.out.println("Ticket id " + ticket.getTicketId() + " has been paid by user " + username);

	}
}
