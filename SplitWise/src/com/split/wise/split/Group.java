package com.split.wise.split;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Group {
	private Long group_id;
	private User admin_user;
	private List<User> members;
	private List<Split> splits;

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long id) {
		this.group_id = id;
	}

	public User getAdmin_user() {
		return admin_user;
	}

	public List<User> getMembers() {
		return members;
	}

	public void addMembers(List<User> members) {
		if (members == null) return;
		this.members.addAll(members);
	}

	public void removeMember(User member) {
		this.members.remove(member);
	}

	public Group(Long group_id, User admin_user, List<User> members) {
		super();
		this.group_id = group_id;
		this.admin_user = admin_user;
		this.members = new CopyOnWriteArrayList<>();
		if (members != null) this.members.addAll(members);
		this.splits = new CopyOnWriteArrayList<>();
	}

	public Group(Long group_id, User admin_user) {
		super();
		this.group_id = group_id;
		this.admin_user = admin_user;
		this.members = new CopyOnWriteArrayList<>();
		this.members.add(admin_user);
		this.splits = new CopyOnWriteArrayList<>();
	}

	public List<Split> getSplits() {
		return splits;
	}

	public void addSplit(Split split) {
		this.splits.add(split);
	}

	@Override
	public String toString() {
		return "Group{id=" + group_id + ", admin=" + admin_user.getName() + ", members=" + members + ", splits=" + splits + "}";
	}
}
