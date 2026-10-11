package com.split.wise.dto;

public class SplitPaymentResultDto {
    private final Long splitId;
    private final Long userId;
    private final long remainingCents;

    public SplitPaymentResultDto(Long splitId, Long userId, long remainingCents) {
        this.splitId = splitId;
        this.userId = userId;
        this.remainingCents = remainingCents;
    }

    public Long getSplitId() {
        return splitId;
    }

    public Long getUserId() {
        return userId;
    }

    public long getRemainingCents() {
        return remainingCents;
    }
}
