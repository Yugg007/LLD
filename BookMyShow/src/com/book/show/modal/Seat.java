package com.book.show.modal;

public class Seat {
	private Long id;
	private int row;
	private int column;
	private SeatCategory seatCategory;

	public Seat(Long id, int row, int column, SeatCategory seatCategory) {
		this.id = id;
		this.row = row;
		this.column = column;
		this.seatCategory = seatCategory;
	}

	public Long getId() {
		return id;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public SeatCategory getSeatCategory() {
		return seatCategory;
	}
}