package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.RecommendedField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendFieldRepository extends JpaRepository<RecommendedField, Integer> {
    Optional<RecommendedField> findByRecIndex(Integer recIndex);
}
