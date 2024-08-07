package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Modified;
import com.gam.hikingclub.repository.ModifiedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final ModifiedRepository modifiedRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void checkForNewModifications() {
        // 최근 1시간 이내에 추가된 MODIFIED 데이터 검색
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Modified> recentModifications = modifiedRepository.findByCreatedDateAfter(oneHourAgo);

        for (Modified modified : recentModifications) {
            notificationService.checkForNewNotifications(modified);
        }

        // 작업 완료 후 MODIFIED 테이블 비우기
        clearModifiedTable();
    }

    private void clearModifiedTable() {
        modifiedRepository.deleteAll(); // 모든 데이터를 삭제
    }
}
