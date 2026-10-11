package com.book.show.service;

import java.util.concurrent.atomic.AtomicLong;

import com.book.show.modal.Payment;

/** Fake gateway for local testing: every method succeeds except the method "FAIL". */
public class SimulatedPaymentGateway implements PaymentGateway {
	private final AtomicLong paymentIds = new AtomicLong(1);

	@Override
	public Payment charge(long bookingId, long amount, String method) {
		boolean ok = method != null && !"FAIL".equalsIgnoreCase(method);
		return new Payment(paymentIds.getAndIncrement(), bookingId, amount, method,
				ok ? Payment.Status.SUCCESS : Payment.Status.FAILED);
	}

	@Override
	public void refund(Payment payment) {
		payment.setStatus(Payment.Status.REFUNDED);
	}
}