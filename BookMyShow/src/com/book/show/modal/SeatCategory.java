package com.book.show.modal;

public abstract class SeatCategory {
	public String category;
	private final long price;

	public SeatCategory(String category, long price) {
		this.category = category;
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public long getPrice() {
		return price;
	}
}