package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {
    Optional<SearchHistory> findByIdx(Integer idx);
    Integer countBySeq(Integer seq);
    List<SearchHistory> findBySeq(Integer seq);
    List<SearchHistory> findBySeq(Integer seq, Pageable pageable); // 페이징 및 정렬을 지원하는 메서드 추가
    void deleteBySeq(Integer seq);
    void deleteByIdx(Integer idx);

    boolean existsBySeq(Integer seq);
}
