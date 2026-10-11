package com.parking.lot;

import com.parking.lot.enums.ParkingType;
import com.parking.lot.enums.WheelType;
import com.parking.lot.model.ParkingLot;
import com.parking.lot.model.ParkingSlot;
import com.parking.lot.model.Ticket;
import com.parking.lot.model.User;
import com.parking.lot.model.Vehicle;
import com.parking.lot.service.ParkingLotService;
import com.parking.lot.service.TicketService;

public class Demo {
	public static void main(String[] args) {
		ParkingLotService ps_service = new ParkingLotService(new ParkingLot(1L));
		TicketService ts = new TicketService();

		ParkingSlot st1 = new ParkingSlot(1L, ParkingType.FREE, WheelType.TWO_WHEELER);
		ParkingSlot st2 = new ParkingSlot(1L, ParkingType.FREE, WheelType.TWO_WHEELER);
		ParkingSlot st3 = new ParkingSlot(1L, ParkingType.PREMIUM, WheelType.TWO_WHEELER);
		ParkingSlot st4 = new ParkingSlot(1L, ParkingType.FREE, WheelType.FOUR_WHEELER);
		ParkingSlot st5 = new ParkingSlot(1L, ParkingType.PREMIUM, WheelType.TWO_WHEELER);
		
		try {
			ps_service.addSlot(st1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		User user1 = new User(1L, "user1");
		Vehicle vh1 = new Vehicle(1L, "Bike1", WheelType.TWO_WHEELER, user1);
		try {
			Ticket ticket = ps_service.parkVehicle(vh1);
			System.out.println("Vehicle no " + vh1.getVehicle_name() + " is parked at slot id " + ticket.getSlot().getSlotId());
			
			Long estimate_price = ts.getParkEstimation(ticket);
			ps_service.payAndUnPark(ticket, estimate_price);
			System.out.println("Vehicle no " + vh1.getVehicle_name() + " is paid and unparked at slot id " + ticket.getSlot().getSlotId());
			
			ps_service.payAndUnPark(ticket, estimate_price);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
