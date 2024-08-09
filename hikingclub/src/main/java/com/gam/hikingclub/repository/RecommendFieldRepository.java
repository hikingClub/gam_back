package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.RecommendedField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendFieldRepository extends JpaRepository<RecommendedField, Integer> {
    Optional<RecommendedField> findByRecIndex(Integer recIndex);
}
