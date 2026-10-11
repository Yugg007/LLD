package com.parking.lot.state;

import com.parking.lot.enums.TicketState;

public class TicketOpenState implements TicketStatus {
	private final TicketState state = TicketState.OPEN;

	@Override
	public void handleRequest(TicketStatusContext context) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("This is open state");
		
		context.changeState(new TicketPaidStatus());
		
	}

	@Override
	public TicketState getState() {
		// TODO Auto-generated method stub
		return state;
	}

}
