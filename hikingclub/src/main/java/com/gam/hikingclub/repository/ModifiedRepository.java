package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Modified;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ModifiedRepository extends JpaRepository<Modified, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Modified")
    void deleteAllInBatch();

    List<Modified> findByCreatedDateAfter(LocalDateTime tenMinutesAgo);
}
