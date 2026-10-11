package com.split.wise.strategy;

import java.util.List;
import java.util.Map;

import com.split.wise.split.User;

public interface SplitStrategy {
    // returns a map of userId -> amount owed in cents
    Map<Long, Long> split(long totalCostCents, List<User> participants, Map<Long, Double> params) throws Exception;
}
