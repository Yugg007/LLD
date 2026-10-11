package com.parking.lot.state;

import com.parking.lot.enums.TicketState;

public class TicketPaidStatus  implements TicketStatus	{
	private final TicketState state = TicketState.PAID;
	
	@Override
	public void handleRequest(TicketStatusContext context) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("This is paid state");
		
		context.changeState(new TicketClosedStatus());
	}
	
	@Override
	public TicketState getState() {
		// TODO Auto-generated method stub
		return state;
	}
}
