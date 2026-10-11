package com.split.wise.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.split.wise.split.User;

public class PercentageSplitStrategy implements SplitStrategy {

    @Override
    public Map<Long, Long> split(long totalCostCents, List<User> participants, Map<Long, Double> params)
            throws Exception {
        if (params == null || params.isEmpty())
            throw new Exception("Percentage map required for percentage split");
        double sumPercent = 0;
        Map<Long, Long> result = new HashMap<>();
        long allocated = 0;
        for (Map.Entry<Long, Double> e : params.entrySet()) {
            double p = e.getValue();
            if (p < 0 || p > 100) throw new Exception("Invalid percentage value");
            sumPercent += p;
            long cents = Math.round(totalCostCents * p / 100.0);
            result.put(e.getKey(), cents);
            allocated += cents;
        }
        if (Math.abs(sumPercent - 100.0) > 0.001)
            throw new Exception("Percentages must sum to 100");
        long rem = totalCostCents - allocated;
        if (rem > 0 && participants != null) {
            // distribute remaining cents
            int i = 0;
            List<User> parts = participants;
            while (rem > 0 && i < parts.size()) {
                Long id = parts.get(i).getUser_id();
                result.put(id, result.getOrDefault(id, 0L) + 1);
                rem--;
                i++;
            }
        }
        if (participants != null) {
            for (User u : participants) {
                result.putIfAbsent(u.getUser_id(), 0L);
            }
        }
        return result;
    }

}
