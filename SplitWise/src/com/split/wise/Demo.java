package com.split.wise;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.split.wise.dto.GroupDto;
import com.split.wise.dto.SplitDto;
import com.split.wise.dto.SplitPaymentResultDto;
import com.split.wise.service.GroupService;
import com.split.wise.service.UserService;
import com.split.wise.split.User;
import com.split.wise.strategy.SplitType;

public class Demo {
	public static void main(String args[]) throws Exception {
		UserService us = new UserService();
		GroupService gs = new GroupService();

		User alice = us.createUser(1L, "Alice");
		User bob = us.createUser(2L, "Bob");
		User carol = us.createUser(3L, "Carol");

		GroupDto g = gs.createGroup(100L, alice);
		List<User> participants = Arrays.asList(alice, bob, carol);
		g = gs.addMembers(g.getGroupId(), Arrays.asList(bob, carol));
		// create an equal split of 300.00 among Alice, Bob, Carol
		SplitDto s = gs.initiateSplit(10L, g.getGroupId(), 300.00, SplitType.EQUAL, participants, null);

		System.out.println("Initial splits for split id " + s.getSplitId() + " : " + centsMapToDollars(s.getAllocations()));

		SplitPaymentResultDto paymentResult = gs.paySplit(s.getSplitId(), bob.getUser_id(), 5000L);
		System.out.println("Bob paid 50.00, remaining owed: " + (paymentResult.getRemainingCents() / 100.0));

		System.out.println("Updated splits: " + centsMapToDollars(s.getAllocations()));

		System.out.println("Group splits:");
		for (SplitDto splitDto : gs.viewGroupSplits(g.getGroupId())) {
			System.out.println(" - split " + splitDto.getSplitId() + ": " + centsMapToDollars(splitDto.getAllocations()));
		}
        
		Map<Long, Double> perc = new HashMap<>();
		perc.put(alice.getUser_id(), 50.0);
		perc.put(bob.getUser_id(), 30.0);
		perc.put(carol.getUser_id(), 20.0);
		SplitDto s2 = gs.initiateSplit(11L, g.getGroupId(), 200.00, SplitType.PERCENTAGE, participants, perc);
		System.out.println("Percentage split: " + centsMapToDollars(s2.getAllocations()));

		Map<Long, Double> ratios = new HashMap<>();
		ratios.put(alice.getUser_id(), 1.0);
		ratios.put(bob.getUser_id(), 2.0);
		ratios.put(carol.getUser_id(), 1.0);
		SplitDto s3 = gs.initiateSplit(12L, g.getGroupId(), 400.00, SplitType.RATIO, participants, ratios);
		System.out.println("Ratio split: " + centsMapToDollars(s3.getAllocations()));

	}

	private static Map<Long, Double> centsMapToDollars(Map<Long, Long> map) {
		Map<Long, Double> out = new HashMap<>();
		if (map == null) return out;
		for (Map.Entry<Long, Long> e : map.entrySet()) {
			out.put(e.getKey(), e.getValue() / 100.0);
		}
		return out;
	}

}
