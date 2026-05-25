package com.lld.lbm.service;

import com.lld.lbm.modal.Member;
import com.lld.lbm.modal.PremiumMember;
import com.lld.lbm.modal.StandardMember;
import com.lld.lbm.repository.MemberRepository;

/**
 * Manages member registration and lifecycle.
 */
public class MemberService {

	private final MemberRepository memberRepo;

	public MemberService(MemberRepository memberRepo) {
		this.memberRepo = memberRepo;
	}

	/**
	 * Register a new standard member.
	 *
	 * @param memberId unique identifier
	 * @param name     member's full name
	 */
	public void registerStandardMember(String memberId, String name) {
		Member member = new StandardMember(memberId, name);
		memberRepo.save(member);
		System.out.println("[MEMBER] Registered: " + member);
	}

	/**
	 * Register a new premium member.
	 *
	 * @param memberId unique identifier
	 * @param name     member's full name
	 */
	public void registerPremiumMember(String memberId, String name) {
		Member member = new PremiumMember(memberId, name);
		memberRepo.save(member);
		System.out.println("[MEMBER] Registered: " + member);
	}

}
