package com.split.wise.dto;

import java.util.Collections;
import java.util.List;

public class GroupDto {
    private final Long groupId;
    private final Long adminUserId;
    private final List<Long> memberIds;
    private final List<Long> splitIds;

    public GroupDto(Long groupId, Long adminUserId, List<Long> memberIds, List<Long> splitIds) {
        this.groupId = groupId;
        this.adminUserId = adminUserId;
        this.memberIds = memberIds == null ? Collections.emptyList() : Collections.unmodifiableList(memberIds);
        this.splitIds = splitIds == null ? Collections.emptyList() : Collections.unmodifiableList(splitIds);
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public List<Long> getSplitIds() {
        return splitIds;
    }
}
