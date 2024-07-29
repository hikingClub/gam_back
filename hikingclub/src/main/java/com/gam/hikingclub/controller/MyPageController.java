package com.gam.hikingclub.controller;

import com.gam.hikingclub.dto.MemberRecommendDTO;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.service.MemberService;
import com.gam.hikingclub.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MyPageService myPageService;
    @Autowired
    private MemberRepository memberRepository;

    // 현재 로그인한 유저를 세션에서 가져오는 메서드
    private Member getLoggedInUser(HttpSession session) throws Exception {
        // 이 부분을 통해 강제적으로 Seq를 정해줄 수 있음 postman 테스트용임
        // Integer memberSeq = 4;
        Integer memberSeq = (Integer) session.getAttribute("memberSeq");

        if (memberSeq == null) {
            throw new Exception("로그인시 마이페이지에 접근이 가능합니다.");
        }
        return myPageService.getMemberBySeq(memberSeq);
    }


    @PostMapping("/setRecommendSetting")
    public ResponseEntity<String> setRecommendSetting(HttpSession session, @RequestBody Member member) {
        try {
            member.setSeq(getLoggedInUser(session).getSeq());
            myPageService.setRecommendSetting(member);
            return ResponseEntity.ok("성공!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("에러 이유" + e.getMessage());
        }
    }

    @PostMapping("/getRecommendSetting")
    public ResponseEntity<Map<String, Object>> getRecommendSetting(HttpSession session) {
        try {
            Member member = getLoggedInUser(session);
            List<String> recommendFieldList = myPageService.getRecommendFieldName(member.getSeq());
            MemberRecommendDTO memberRecommendDTO = myPageService.getRecommendedSetting(member.getSeq());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("recommendFieldList", recommendFieldList);
            response.put("interest", memberRecommendDTO.getInterest());
            response.put("ageRange", memberRecommendDTO.getAgeRange());
            response.put("jobRange", memberRecommendDTO.getJobRange());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "오류 메세지: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
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

    // 회원을 삭제하는 엔드포인트
    @PostMapping("/settings/quit")
    public ResponseEntity<?> quitUser(HttpSession session) {
        try {
            Member member = getLoggedInUser(session);
            myPageService.deleteUser(member.getSeq());
            return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("회원 탈퇴 실패 사유: " + e.getMessage());
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
