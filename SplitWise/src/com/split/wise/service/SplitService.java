package com.split.wise.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.split.wise.dto.SplitDto;
import com.split.wise.dto.SplitPaymentResultDto;
import com.split.wise.exception.InvalidSplitException;
import com.split.wise.exception.NotFoundException;
import com.split.wise.exception.PaymentException;
import com.split.wise.repository.GroupLockManager;
import com.split.wise.repository.GroupRepository;
import com.split.wise.repository.InMemoryGroupRepository;
import com.split.wise.repository.InMemorySplitRepository;
import com.split.wise.repository.SplitRepository;
import com.split.wise.split.Group;
import com.split.wise.split.Split;
import com.split.wise.split.User;
import com.split.wise.strategy.SplitStrategy;
import com.split.wise.strategy.SplitStrategyFactory;
import com.split.wise.strategy.SplitType;

public class SplitService {
    private final SplitRepository splitRepo;
    private final GroupRepository groupRepo;
    private final GroupLockManager lockManager;
    private final List<String> auditLog = new CopyOnWriteArrayList<>();

    public SplitService() {
        this(new InMemorySplitRepository(), new InMemoryGroupRepository(), new GroupLockManager());
    }

    public SplitService(SplitRepository splitRepo, GroupRepository groupRepo, GroupLockManager lockManager) {
        this.splitRepo = splitRepo;
        this.groupRepo = groupRepo;
        this.lockManager = lockManager;
    }

    public SplitDto initiateSplit(Long splitId, Group group, double totalCost, SplitType type, List<User> participants,
            Map<Long, Double> params) {
        if (group == null) throw new NotFoundException("Group not found");
        if (participants == null || participants.isEmpty())
            throw new InvalidSplitException("Participants required");
        if (!group.getMembers().containsAll(participants))
            throw new InvalidSplitException("All participants must be members of the group");
        if (totalCost < 0) throw new InvalidSplitException("Total cost must be non-negative");

        long totalCents = Math.round(totalCost * 100);

        if (type == SplitType.PERCENTAGE) {
            double sumPercent = params.values().stream().filter(e -> e != null).mapToDouble(Double::doubleValue).sum();
            if (Math.abs(sumPercent - 100.0) > 0.001)
                throw new InvalidSplitException("Percentages must sum to 100");
        }

        if (type == SplitType.EXACT) {
            double sum = params.values().stream().filter(e -> e != null).mapToDouble(Double::doubleValue).sum();
            long sumCents = Math.round(sum * 100);
            if (Math.abs(sumCents - totalCents) > 1)
                throw new InvalidSplitException("Exact amounts do not add up to total");
        }

        SplitStrategy strategy = SplitStrategyFactory.getStrategy(type);
        if (strategy == null) throw new InvalidSplitException("Unsupported split type");

        Map<Long, Long> allocations;
        try {
            allocations = strategy.split(totalCents, participants, params);
        } catch (Exception e) {
            throw new InvalidSplitException(e.getMessage());
        }

        Split split = new Split(splitId, group, totalCents, allocations);
        ReentrantReadWriteLock lock = lockManager.getLockForGroup(group.getGroup_id());
        lock.writeLock().lock();
        try {
            Split saved = splitRepo.save(split);
            group.addSplit(saved);
            groupRepo.save(group);
            auditLog.add("Created split " + saved.getSplit_id() + " for group " + group.getGroup_id());
            return toDto(saved);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public SplitPaymentResultDto paySplit(Long splitId, Long userId, long centsToPay) {
        if (centsToPay < 0) throw new PaymentException("Payment amount must be non-negative");
        Split split = splitRepo.findById(splitId);
        if (split == null) throw new NotFoundException("Split not found");
        Group group = split.getGroup();
        if (group == null || group.getGroup_id() == null) throw new InvalidSplitException("Split is not associated with a group");

        ReentrantReadWriteLock lock = lockManager.getLockForGroup(group.getGroup_id());
        lock.writeLock().lock();
        try {
            long remaining = split.payPersonSplitById(userId, centsToPay);
            splitRepo.save(split);
            auditLog.add("Payment of " + centsToPay + " cents on split " + splitId + " by user " + userId);
            return new SplitPaymentResultDto(splitId, userId, remaining);
        } catch (IllegalArgumentException e) {
            throw new PaymentException(e.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public SplitDto getSplit(Long splitId) {
        Split split = splitRepo.findById(splitId);
        if (split == null) throw new NotFoundException("Split not found");
        return toDto(split);
    }

    public List<SplitDto> viewGroupSplits(Long groupId) {
        List<SplitDto> dtoList = new ArrayList<>();
        for (Split split : splitRepo.findByGroupId(groupId)) {
            dtoList.add(toDto(split));
        }
        return dtoList;
    }

    public List<SplitDto> viewAllSplits() {
        List<SplitDto> dtoList = new ArrayList<>();
        for (Split split : splitRepo.findAll()) {
            dtoList.add(toDto(split));
        }
        return dtoList;
    }

    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }

    private SplitDto toDto(Split split) {
        return new SplitDto(split.getSplit_id(), split.getGroup().getGroup_id(), split.getTotal_cost_cents(), split.getPerson_to_split());
    }
}
