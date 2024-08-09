package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.Modified;
import com.gam.hikingclub.entity.Notification;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final MailService mailService;

    // 모든 Modified 데이터를 기반으로 사용자의 알람을 생성하는 메서드
    public void checkForNewNotifications(List<Modified> modifiedList) {

        System.out.println("Modified 테이블에서 사용자의 키워드를 찾아 알림 테이블로 값을 넘깁니다.");

        List<Member> members = memberRepository.findAll();
        List<Notification> notificationsToSave = new ArrayList<>();

        for (Member member : members) {

            String keywordsString = member.getInterestKeyword();

            // interestKeyword가 null이거나 비어있다면 다음 멤버로 넘어감
            if (keywordsString == null || keywordsString.trim().isEmpty()) {
                continue;
            }

            String email = member.getEmail();
            List<String> notificationsToSend = new ArrayList<>();

            List<String> interestKeywords = List.of(keywordsString.split(",")).stream()
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());

            for (Modified modified : modifiedList) {
                for (String keyword : interestKeywords) {
                    if (modified.getModifiedTitle().contains(keyword)) {
                        String message = modified.getModifiedTitle();
                        boolean exists = notificationRepository.existsByMemberSeqAndMessage(member.getSeq(), message);

                        if (!exists) {
                            Notification notification = new Notification();
                            notification.setMemberSeq(member.getSeq());
                            notification.setMessage(message);
                            notification.setChecked(false);
                            notification.setModifiedDate(modified.getCreatedDate());
                            notification.setModifiedId(modified.getModifiedId());
                            notification.setDate(modified.getDate());
                            notificationsToSave.add(notification);

                            // 이메일 알림 내용에 추가
                            notificationsToSend.add(message);
                        }
                        break;
                    }
                }
            }

            // 모아둔 알림들을 한 번에 이메일로 발송
            if (!notificationsToSend.isEmpty()) {
                mailService.sendNotificationMail(email, notificationsToSend);
            }
        }

        // 배치로 알림 저장
        if (!notificationsToSave.isEmpty()) {
            notificationRepository.saveAll(notificationsToSave);
        }

        // 로직 종료 후 콘솔 메시지 출력
        System.out.println("종료 1시간 뒤 재 시작됩니다");
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
