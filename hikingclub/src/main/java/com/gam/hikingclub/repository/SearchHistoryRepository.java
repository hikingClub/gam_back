package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {
    List<SearchHistory> findBySeq(Integer seq);

    void deleteBySeq(Integer seq);
}
