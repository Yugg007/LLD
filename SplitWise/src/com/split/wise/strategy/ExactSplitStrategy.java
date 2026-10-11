package com.split.wise.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.split.wise.split.User;

public class ExactSplitStrategy implements SplitStrategy {

    @Override
    public Map<Long, Long> split(long totalCostCents, List<User> participants, Map<Long, Double> params)
            throws Exception {
        if (params == null || params.isEmpty())
            throw new Exception("Exact amounts required for exact split");
        Map<Long, Long> result = new HashMap<>();
        long sum = 0;
        for (Map.Entry<Long, Double> e : params.entrySet()) {
            long cents = Math.round(e.getValue() * 100);
            result.put(e.getKey(), cents);
            sum += cents;
        }
        if (Math.abs(sum - totalCostCents) > 1)
            throw new Exception("Exact split amounts do not add up to total cost");
        if (participants != null) {
            for (User u : participants) {
                result.putIfAbsent(u.getUser_id(), 0L);
            }
        }
        return result;
    }

}
