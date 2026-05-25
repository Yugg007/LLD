package com.lld.lbm.config;

public final class LibraryConfig {
	private LibraryConfig() {
		
	}
	
	// Borrowing limits per member type
	public static final int STANDARD_MEMBER_BORROW_LIMIT = 5;
	public static final int PREMIUM_MEMBER_BORROW_LIMIT = 10;
	
	// Loan duration in days
	public static final int STANDARD_LOAN_DURATION_DAYS = 14;
	public static final int PREMIUM_LOAN_DURATION_DAYS = 30;
	
	//Fine policy
	public static final double FINE_PER_DAY_RUPEES = 10.0;

}
