package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberSeqAndCheckedFalse(Integer memberSeq);
}
