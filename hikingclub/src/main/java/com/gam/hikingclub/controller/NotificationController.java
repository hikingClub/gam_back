package com.gam.hikingclub.controller;

import com.gam.hikingclub.entity.Notification;
import com.gam.hikingclub.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 멤버의 미확인 알람 확인
    @GetMapping("/unchecked")
    public ResponseEntity<List<Notification>> getUncheckedNotifications(HttpSession session) {
        Integer memberSeq = (Integer) session.getAttribute("memberSeq");
        List<Notification> notifications = notificationService.getUncheckedNotifications(memberSeq);
        return ResponseEntity.ok(notifications);
    }

    // 멤버의 전체 알람 확인
    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications(HttpSession session) {
        Integer memberSeq = (Integer) session.getAttribute("memberSeq");
        List<Notification> notifications = notificationService.getAllNotifications(memberSeq);
        return ResponseEntity.ok(notifications);
    }

    // 알림 확인
    @PostMapping("/checked/{id}")
    public ResponseEntity<String> markAsChecked(@PathVariable Long id) {
        notificationService.markAsChecked(id);
        return ResponseEntity.ok("알림이 확인되었습니다.");
    }

    // 알림 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("알림이 삭제되었습니다.");
    }
}
