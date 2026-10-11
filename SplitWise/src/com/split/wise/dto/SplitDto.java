package com.split.wise.dto;

import java.util.Collections;
import java.util.Map;

public class SplitDto {
    private final Long splitId;
    private final Long groupId;
    private final long totalCostCents;
    private final Map<Long, Long> allocations;

    public SplitDto(Long splitId, Long groupId, long totalCostCents, Map<Long, Long> allocations) {
        this.splitId = splitId;
        this.groupId = groupId;
        this.totalCostCents = totalCostCents;
        this.allocations = allocations == null ? Collections.emptyMap() : Collections.unmodifiableMap(allocations);
    }

    public Long getSplitId() {
        return splitId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public long getTotalCostCents() {
        return totalCostCents;
    }

    public Map<Long, Long> getAllocations() {
        return allocations;
    }
}
