package com.split.wise.split;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Split {
	private Long split_id;
	private Group group;
	// store money as cents to avoid floating point rounding issues
	private long total_cost_cents;
	private Map<Long, Long> person_to_split;

	public Long getSplit_id() {
		return split_id;
	}

	public void setSplit_id(Long split_id) {
		this.split_id = split_id;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public long getTotal_cost_cents() {
		return total_cost_cents;
	}

	public void setTotal_cost_cents(long total_cost_cents) {
		this.total_cost_cents = total_cost_cents;
	}

	public Map<Long, Long> getPerson_to_split() {
		return person_to_split;
	}

	public Split(Long split_id, Group group, long total_cost_cents, Map<Long, Long> person_to_split) {
		this.split_id = split_id;
		this.group = group;
		this.total_cost_cents = total_cost_cents;
		if (person_to_split instanceof ConcurrentHashMap) {
			this.person_to_split = person_to_split;
		} else {
			this.person_to_split = new ConcurrentHashMap<>();
			if (person_to_split != null) this.person_to_split.putAll(person_to_split);
		}
	}

	@Override
	public String toString() {
		return "Split{id=" + split_id + ", totalCents=" + total_cost_cents + ", perPerson=" + person_to_split + "}";
	}
	
	public long findPersonSplit(User person) {
		if (person == null || getPerson_to_split() == null) return 0L;
		Long person_split = getPerson_to_split().get(person.getUser_id());
		if (person_split == null) return 0L;
		return person_split;
	}
    
	public long payPersonSplit(User person, long centsToPay) {
		if (person == null || getPerson_to_split() == null)
			throw new IllegalArgumentException("Invalid person or split data");
		return payPersonSplitById(person.getUser_id(), centsToPay);
	}

	public long payPersonSplitById(Long userId, long centsToPay) {
		if (userId == null || getPerson_to_split() == null)
			throw new IllegalArgumentException("Invalid person or split data");
		if (centsToPay < 0) throw new IllegalArgumentException("Payment amount must be non-negative");
		final long[] result = new long[1];
		getPerson_to_split().compute(userId, (k, old) -> {
			if (old == null) throw new IllegalArgumentException("No split found for this person");
			if (centsToPay > old) throw new IllegalArgumentException("Paying money more than required!!");
			long rem = old - centsToPay;
			result[0] = rem;
			return rem;
		});
		return result[0];
	}


	



}
