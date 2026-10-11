package com.split.wise.service;

import java.util.ArrayList;
import java.util.List;

import com.split.wise.dto.GroupDto;
import com.split.wise.dto.SplitDto;
import com.split.wise.dto.SplitPaymentResultDto;
import com.split.wise.repository.GroupRepository;
import com.split.wise.repository.GroupLockManager;
import com.split.wise.repository.InMemoryGroupRepository;
import com.split.wise.repository.InMemorySplitRepository;
import com.split.wise.split.Group;
import com.split.wise.split.User;
import com.split.wise.strategy.SplitType;
import com.split.wise.exception.NotFoundException;

public class GroupService {
    private final GroupRepository groupRepo;
    private final SplitService splitService;
    private final GroupLockManager lockManager;

    public GroupService() {
        this.groupRepo = new InMemoryGroupRepository();
        this.lockManager = new GroupLockManager();
        this.splitService = new SplitService(new InMemorySplitRepository(), groupRepo, lockManager);
    }

    public GroupService(GroupRepository groupRepo, SplitService splitService, GroupLockManager lockManager) {
        this.groupRepo = groupRepo;
        this.splitService = splitService;
        this.lockManager = lockManager;
    }

    public GroupDto createGroup(Long groupId, User admin) {
        Group g = new Group(groupId, admin);
        Group saved = groupRepo.save(g);
        return toDto(saved);
    }

    public GroupDto getGroup(Long groupId) {
        Group g = groupRepo.findById(groupId);
        if (g == null) throw new NotFoundException("Group not found");
        return toDto(g);
    }

    public SplitDto initiateSplit(Long splitId, Long groupId, double totalCost, SplitType type, List<User> participants,
            java.util.Map<Long, Double> params) {
        Group g = groupRepo.findById(groupId);
        if (g == null) throw new NotFoundException("Group not found");
        return splitService.initiateSplit(splitId, g, totalCost, type, participants, params);
    }

    public SplitPaymentResultDto paySplit(Long splitId, Long userId, long centsToPay) {
        return splitService.paySplit(splitId, userId, centsToPay);
    }

    public GroupDto addMembers(Long groupId, List<User> members) {
        Group g = groupRepo.findById(groupId);
        if (g == null) throw new NotFoundException("Group not found");
        if (members == null || members.isEmpty()) {
            return toDto(g);
        }
        java.util.concurrent.locks.ReentrantReadWriteLock lock = lockManager.getLockForGroup(groupId);
        lock.writeLock().lock();
        try {
            g.addMembers(members);
            groupRepo.save(g);
            return toDto(g);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<SplitDto> viewGroupSplits(Long groupId) {
        Group g = groupRepo.findById(groupId);
        if (g == null) throw new NotFoundException("Group not found");
        return splitService.viewGroupSplits(groupId);
    }

    public List<SplitDto> viewAllSplits() {
        return splitService.viewAllSplits();
    }

    private GroupDto toDto(Group g) {
        List<Long> memberIds = new ArrayList<>();
        for (User member : g.getMembers()) {
            memberIds.add(member.getUser_id());
        }
        List<Long> splitIds = new ArrayList<>();
        for (com.split.wise.split.Split split : g.getSplits()) {
            splitIds.add(split.getSplit_id());
        }
        return new GroupDto(g.getGroup_id(), g.getAdmin_user().getUser_id(), memberIds, splitIds);
    }
}
