package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUid(String uid);
    Optional<Member> findBySeq(Integer seq);

}
