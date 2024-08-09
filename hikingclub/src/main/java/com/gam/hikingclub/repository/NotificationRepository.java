package com.gam.hikingclub.repository;

import com.gam.hikingclub.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberSeqAndCheckedFalse(Integer memberSeq);
    List<Notification> findByMemberSeq(Integer memberSeq);

    // 멤버 seq와 타이틀이 존재하는 여부 확인
    boolean existsByMemberSeqAndMessage(Integer memberSeq, String message);
}
