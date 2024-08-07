package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.Modified;
import com.gam.hikingclub.entity.Notification;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.ModifiedRepository;
import com.gam.hikingclub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    // Modified 데이터를 기반으로 사용자의 알람을 생성하는 메서드
    public void checkForNewNotifications(Modified modified) {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            String keywordsString = member.getInterestKeyword();
            if (keywordsString != null && !keywordsString.trim().isEmpty()) {
                // 중복 키워드 제거
                List<String> interestKeywords = List.of(keywordsString.split(",")).stream()
                        .map(String::trim)
                        .distinct()
                        .collect(Collectors.toList());

                for (String keyword : interestKeywords) {
                    if (modified.getModifiedTitle().contains(keyword)) {
                        // 동일한 메시지로 이미 알림이 존재하는지 확인
                        String message = modified.getModifiedTitle();
                        boolean exists = notificationRepository.existsByMemberSeqAndMessage(member.getSeq(), message);

                        if (!exists) {
                            Notification notification = new Notification();
                            notification.setMemberSeq(member.getSeq());
                            notification.setMessage(message);
                            notification.setChecked(false);
                            notification.setModifiedDate(modified.getCreatedDate());
                            notificationRepository.save(notification);
                        }
                        break; // 키워드 일치 시 중복 알림 방지를 위해 반복 종료
                    }
                }
            }
        }
    }

    // 사용자가 확인하지 않은 알림 목록을 반환하는 메서드
    public List<Notification> getUncheckedNotifications(Integer memberSeq) {
        return notificationRepository.findByMemberSeqAndCheckedFalse(memberSeq);
    }

    // 알림을 확인한 상태로 표시 변경하는 메서드
    public void markAsChecked(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setChecked(true);
        notificationRepository.save(notification);
    }

    // 알람 삭제 메서드
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // 사용자의 모든 알람을 확인하는 메서드
    public List<Notification> getAllNotifications(Integer memberSeq) {
        return notificationRepository.findByMemberSeq(memberSeq);
    }
}
