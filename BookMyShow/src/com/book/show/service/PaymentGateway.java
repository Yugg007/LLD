package com.book.show.service;

import com.book.show.modal.Payment;

/** Plug a real payment provider in here. bookingId doubles as the idempotency key for the provider. */
public interface PaymentGateway {
	Payment charge(long bookingId, long amount, String method);

	void refund(Payment payment);
}