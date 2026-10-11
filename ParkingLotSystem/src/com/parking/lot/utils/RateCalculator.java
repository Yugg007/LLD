package com.parking.lot.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class RateCalculator {



	public static Long getPrice(LocalDateTime create_at, Long hourly_rate) {
	    if (create_at == null || hourly_rate == null) {
	        throw new IllegalArgumentException("Timestamps and rates cannot be null");
	    }

	    LocalDateTime now = LocalDateTime.now();

	    if (create_at.isAfter(now)) {
	        return 0L;
	    }

	    Duration duration = Duration.between(create_at, now);
	    long totalMinutes = duration.toMinutes();


	    long billableHours = (long) Math.ceil((double) totalMinutes / 60.0);

	    if (billableHours == 0) {
	        billableHours = 1L;
	    }

	    return billableHours * hourly_rate;
	}

}
