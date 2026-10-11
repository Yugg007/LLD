package com.parking.lot.config;


import java.util.Map;

import com.parking.lot.enums.ParkingType;

public class RateConfig {
	private static Map<ParkingType, Long> mp = Map.of(ParkingType.FREE, 10L, ParkingType.PREMIUM, 50L);
	
	public static Long getPrice(ParkingType type) {
		Long price = mp.get(type);
		if(price == null) {
			return 10L;
		}
		
		return price;
	}

}
