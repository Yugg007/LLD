package com.split.wise.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.split.wise.split.User;

public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public Map<Long, Long> split(long totalCostCents, List<User> participants, Map<Long, Double> params)
            throws Exception {
        if (participants == null || participants.isEmpty())
            throw new Exception("Participants required for equal split");
        int n = participants.size();
        long each = totalCostCents / n;
        Map<Long, Long> result = new HashMap<>();
        for (User u : participants) {
            result.put(u.getUser_id(), each);
        }
        // distribute remainder cents to first participants
        long rem = totalCostCents - each * n;
        for (int i = 0; rem > 0 && i < participants.size(); i++, rem--) {
            Long id = participants.get(i).getUser_id();
            result.put(id, result.get(id) + 1);
        }
        return result;
    }

}
