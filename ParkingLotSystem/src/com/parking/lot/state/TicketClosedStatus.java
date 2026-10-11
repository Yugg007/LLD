package com.parking.lot.state;

import com.parking.lot.enums.TicketState;

public class TicketClosedStatus implements TicketStatus {
	private final TicketState state = TicketState.OPEN;

	@Override
	public void handleRequest(TicketStatusContext context) {
		// TODO Auto-generated method stub
		System.out.println("This is close state");
		
		throw new IllegalStateException("Cannot perform operations on a closed ticket.");
    }

	@Override
	public TicketState getState() {
		// TODO Auto-generated method stub
		return state;
	}
}
