package com.parking.lot.model;

import java.time.LocalDateTime;

import com.parking.lot.state.TicketStatusContext;


public class Ticket {
	private Long ticketId;
	private LocalDateTime parkingAt;
	private ParkingSlot slot;
	private Long ticketPrice;
	private TicketStatusContext context;


	public Long getTicketId() {
		return ticketId;
	}


	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}


	public LocalDateTime getParkingAt() {
		return parkingAt;
	}



	public void setParkingAt(LocalDateTime parkingAt) {
		this.parkingAt = parkingAt;
	}



	public ParkingSlot getSlot() {
		return slot;
	}



	public void setSlot(ParkingSlot slot) {
		this.slot = slot;
	}



	public Long getTicketPrice() {
		return ticketPrice;
	}



	public void setTicketPrice(Long ticketPrice) {
		this.ticketPrice = ticketPrice;
	}



	public TicketStatusContext getContext() {
		return context;
	}



	public void setContext(TicketStatusContext context) {
		this.context = context;
	}



	public Ticket(ParkingSlot slot, TicketStatusContext context, LocalDateTime parkingAt) {
		super();
		this.slot = slot;
		this.context = context;
		this.parkingAt = parkingAt;
	}


	@Override
	public String toString() {
		return "Ticket [ticketId=" + ticketId + ", parkingAt=" + parkingAt + ", slot=" + slot + ", ticketPrice="
				+ ticketPrice + ", context=" + context + "]";
	}
	
	

}
