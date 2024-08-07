package com.gam.hikingclub.listener;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.Modified;
import com.gam.hikingclub.entity.Notification;
import com.gam.hikingclub.event.ModifiedEvent;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModifiedEventListener {
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleModifiedEvent(ModifiedEvent event) {
        Modified modified = event.getModified();
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            String keywordsString = member.getInterestKeyword();
            if (keywordsString != null && !keywordsString.trim().isEmpty()) {
                List<String> interestKeywords = List.of(keywordsString.split(","));
                for (String keyword : interestKeywords) {
                    if (modified.getModifiedTitle().contains(keyword.trim())) {
                        Notification notification = new Notification();
                        notification.setMemberSeq(member.getSeq());
                        notification.setMessage(modified.getModifiedTitle()); // MODIFIED 타이틀 설정
                        notification.setChecked(false); // 초기 상태는 미확인
                        notificationRepository.save(notification);
                        break; // 키워드 일치 시 알림 생성 후 다음 멤버로
                    }
                }
            }
        }

    }
}