package com.parking.lot.state;

public class TicketStatusContext {
	
	private TicketStatus currentState;
	
	
	public TicketStatusContext() {
		this.currentState = new TicketOpenState();
	}
	
	public TicketStatus changeState(TicketStatus nextStatus) throws Exception {
		if(nextStatus == null) {
			throw new Exception("Next State can not be null!!!");
		}
		this.currentState = nextStatus;
		return this.currentState;
	}
	
	public void applyAction() throws Exception {
	    this.currentState.handleRequest(this); 
	}
	
	public TicketStatus getCurrentState() {
	    return this.currentState;
	}
}
