package com.parking.lot.state;

import com.parking.lot.enums.TicketState;

public interface TicketStatus {
	void handleRequest(TicketStatusContext context) throws Exception;
	TicketState getState();

}
