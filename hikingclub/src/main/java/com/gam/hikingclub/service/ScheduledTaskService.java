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

    @Scheduled(fixedRate = 600000) // 10분마다 실행
    public void checkForNewModifications() {
        // 최근 10분 이내에 추가된 MODIFIED 데이터 검색
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Modified> recentModifications = modifiedRepository.findByCreatedDateAfter(tenMinutesAgo);

        for (Modified modified : recentModifications) {
            notificationService.checkForNewNotifications(modified);
        }

        // 작업 완료 후 MODIFIED 테이블 비우기
        clearModifiedTable();
    }

    private void clearModifiedTable() {
        modifiedRepository.truncateTable();
    }
}
