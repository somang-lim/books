package com.books.app.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByUsername(String username);
}
