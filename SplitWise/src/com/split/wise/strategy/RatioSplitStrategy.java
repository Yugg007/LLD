package com.split.wise.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.split.wise.split.User;

public class RatioSplitStrategy implements SplitStrategy {

    @Override
    public Map<Long, Long> split(long totalCostCents, List<User> participants, Map<Long, Double> params)
            throws Exception {
        if (params == null || params.isEmpty())
            throw new Exception("Ratios required for ratio split");
        double sumRatio = 0;
        for (Double r : params.values()) {
            if (r == null || r < 0) throw new Exception("Invalid ratio value");
            sumRatio += r;
        }
        if (sumRatio <= 0) throw new Exception("Sum of ratios must be positive");
        Map<Long, Long> result = new HashMap<>();
        long allocated = 0;
        for (Map.Entry<Long, Double> e : params.entrySet()) {
            long amt = Math.round(totalCostCents * e.getValue() / sumRatio);
            result.put(e.getKey(), amt);
            allocated += amt;
        }
        long rem = totalCostCents - allocated;
        if (rem > 0 && participants != null) {
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
