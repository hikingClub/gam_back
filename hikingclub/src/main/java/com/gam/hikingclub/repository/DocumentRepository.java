package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Document;
import com.gam.hikingclub.entity.Modified;
import com.gam.hikingclub.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    Optional<Document> findByDocId(String docId);
}
