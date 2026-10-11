package com.split.wise.repository;

import java.util.List;

import com.split.wise.split.Split;

public interface SplitRepository extends Repository<Split> {
    List<Split> findByGroupId(Long groupId);
}
