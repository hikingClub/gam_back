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
            List<String> interestKeywords = List.of(member.getInterestKeyword().split(","));
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