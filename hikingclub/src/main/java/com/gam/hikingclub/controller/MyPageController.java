package com.gam.hikingclub.controller;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    // 현재 로그인한 유저를 세션에서 가져오는 메서드
    private Member getLoggedInUser(HttpSession session) throws Exception {
        // 이 부분을 통해 강제적으로 Seq를 정해줄 수있음 postman 테스트용임
        Integer memberSeq = 5;
        // Integer memberSeq = (Integer) session.getAttribute("memberSeq");

        if (memberSeq == null) {
            throw new Exception("로그인시 마이페이지에 접근이 가능합니다.");
        }
        return myPageService.getMemberBySeq(memberSeq);
    }

    // 검색 기록을 가져오는 엔드포인트
    @GetMapping("/kwdhistory")
    public ResponseEntity<?> getUserHistory(HttpSession session) {
        try {
            Member member = getLoggedInUser(session);
            List<SearchHistory> history = myPageService.getUserSearchHistory(member.getSeq());
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("조회 실패 사유: " + e.getMessage());
        }
    }

    // 설정 정보를 가져오는 엔드포인트
    @GetMapping("/settings")
    public ResponseEntity<?> getUserSettings(HttpSession session) {
        try {
            Member member = getLoggedInUser(session);
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("조회 실패 사유: " + e.getMessage());
        }
    }

    // 알람 설정을 업데이트하는 엔드포인트
    @PostMapping("/settings/alarm")
    public ResponseEntity<?> updateAlarmCheck(HttpSession session, @RequestBody UpdateAlarmRequest request) {
        try {
            Member member = getLoggedInUser(session);
            myPageService.updateAlarmCheck(member.getSeq(), request.getAlarmCheck());
            return ResponseEntity.ok("알람 설정이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("알람 설정 변경 실패 사유: " + e.getMessage());
        }
    }

    // 비밀번호를 업데이트하는 엔드포인트
    @PostMapping("/settings/password")
    public ResponseEntity<?> updatePassword(HttpSession session, @RequestBody UpdatePasswordRequest request) {
        try {
            Member member = getLoggedInUser(session);
            myPageService.updatePassword(member.getSeq(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("비밀번호 변경 실패 사유: " + e.getMessage());
        }
    }

    // 알람 설정 업데이트 요청을 위한 내부 클래스
    @Setter
    @Getter
    public static class UpdateAlarmRequest {
        private Integer alarmCheck;
    }

    // 비밀번호 업데이트 요청을 위한 내부 클래스
    @Setter
    @Getter
    public static class UpdatePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
