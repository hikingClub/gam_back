package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Empathy;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpathyRepository extends JpaRepository<Empathy, Integer> {
    Optional<Empathy> findByIdx(Integer idx);
    Integer countByDocId(String docId);
    List<Empathy> findBySeq(Integer seq);
    void deleteBySeq(Integer seq);
    void deleteByIdx(Integer idx);

    boolean existsBySeq(Integer seq);
}
