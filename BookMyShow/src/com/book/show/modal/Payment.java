package com.book.show.modal;

public class Payment {
	public enum Status {
		SUCCESS, FAILED, REFUNDED
	}

	private final long paymentId;
	private final long bookingId;
	private final long amount;
	private final String method;
	private volatile Status status;

	public Payment(long paymentId, long bookingId, long amount, String method, Status status) {
		this.paymentId = paymentId;
		this.bookingId = bookingId;
		this.amount = amount;
		this.method = method;
		this.status = status;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public long getBookingId() {
		return bookingId;
	}

	public long getAmount() {
		return amount;
	}

	public String getMethod() {
		return method;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}