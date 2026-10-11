package com.parking.lot.service;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.parking.lot.model.ParkingSlot;
import com.parking.lot.model.Ticket;
import com.parking.lot.repository.InMemoryTicketRepository;
import com.parking.lot.repository.ParkingSlotLockManager;
import com.parking.lot.repository.TicketRepository;
import com.parking.lot.state.TicketStatusContext;
import com.parking.lot.utils.RateCalculator;

public class TicketService {
	private final TicketRepository repository;
	private final ParkingSlotLockManager lockManager;
	private Long extra_estimate = 10L;
	
	public TicketService() {
		this.repository = new InMemoryTicketRepository();
		this.lockManager = ParkingSlotLockManager.getInstance();
		
	}

	public Long getParkEstimation(Ticket ticket) {
		// TODO Auto-generated method stub
		return RateCalculator.getPrice(ticket.getParkingAt(), ticket.getSlot().getHourlyRate()) + extra_estimate;
	}

	public Ticket generateTicket(ParkingSlot slot) {
		// TODO Auto-generated method stub
		ReentrantReadWriteLock lock = lockManager.getSlotLock(slot.getSlotId());
		lock.writeLock().lock();
		try {
			TicketStatusContext context = new TicketStatusContext();
			LocalDateTime now = LocalDateTime.now();
			Ticket ticket = new Ticket(slot, context, now);
			repository.save(ticket);
			System.out.println(ticket.toString());
			return ticket;
			
		} finally {
			// TODO: handle finally clause
			lock.writeLock().unlock();
		}

	}

	public void payTicket(Ticket ticket) throws Exception {
		// TODO Auto-generated method stub
		ticket.getContext().applyAction();		
	}

}
