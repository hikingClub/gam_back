package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.Modified;
import com.gam.hikingclub.entity.Notification;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.ModifiedRepository;
import com.gam.hikingclub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModifiedRepository modifiedRepository;
    private final MemberRepository memberRepository;

    public void checkForNewNotifications(Modified modified) {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            String keywordsString = member.getInterestKeyword();
            if (keywordsString != null && !keywordsString.trim().isEmpty()) {
                List<String> interestKeywords = List.of(keywordsString.split(","));
                for (String keyword : interestKeywords) {
                    if (modified.getModifiedTitle().contains(keyword)) {
                        Notification notification = new Notification();
                        notification.setMemberSeq(member.getSeq());
                        notification.setModified(modified);
                        notification.setChecked(false);
                        notificationRepository.save(notification);
                        break;
                    }
                }
            }
        }
    }

    public List<Notification> getUncheckedNotifications(Integer memberSeq) {
        return notificationRepository.findByMemberSeqAndCheckedFalse(memberSeq);
    }

    public void markAsChecked(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setChecked(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Scheduled(fixedDelay = 600000) // 10분마다 실행
    public void checkModifiedUpdates() {
        List<Modified> recentModified = modifiedRepository.findAll();
        for (Modified modified : recentModified) {
            checkForNewNotifications(modified);
        }
        modifiedRepository.deleteAll(); // 모든 데이터를 삭제
    }
}
