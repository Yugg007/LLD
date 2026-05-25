package com.lld.lbm.repository;


import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.lld.lbm.modal.Member;

/**
 * Thread-safe, in-memory store for {@link Member}s.
 */
public class MemberRepository implements Repository<Member, String> {

    private final ConcurrentHashMap<String, Member> store = new ConcurrentHashMap<>();

    @Override
    public void save(Member member) {
        store.put(member.getMemberId(), member);
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return Optional.ofNullable(store.get(memberId));
    }

    @Override
    public Collection<Member> findAll() {
        return Collections.unmodifiableCollection(store.values());
    }

    @Override
    public void delete(String memberId) {
        store.remove(memberId);
    }
}