package com.book.show.exception;

/**
 * Thrown when a seat can't be held. This is an expected outcome under heavy load, so the stack trace is
 * disabled (creating stack traces is slow and would hurt throughput on the hot path).
 */
public class SeatUnavailableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SeatUnavailableException(String message) {
		super(message, null, false, false);
	}
}