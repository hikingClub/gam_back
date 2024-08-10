package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUid(String uid);
    Optional<Member> findBySeq(Integer seq);

    Optional<Member> findByNicknameAndEmail(String nickname, String email);
    Optional<Member> findByUidAndEmail(String uid, String email);
}
