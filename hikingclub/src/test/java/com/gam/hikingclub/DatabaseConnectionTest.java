package com.gam.hikingclub;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testDatabaseConnection() {
        // Create a new Member object
        Member member = new Member();
        member.setNickname("testNickname");
        member.setUid("testUid");
        member.setPassword(passwordEncoder.encode("password"));  // 비밀번호 암호화
        member.setEmail("test@example.com");
        member.setAlarmCheck(1);

        try {
            // Save the Member to the database
            memberRepository.save(member);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error while saving member: " + e.getMessage());
        }

        // Retrieve the Member by uid
        Optional<Member> foundByUid = memberRepository.findByUid("testUid");
        Assertions.assertTrue(foundByUid.isPresent(), "Member should be present");
        Assertions.assertEquals("testNickname", foundByUid.get().getNickname());
        Assertions.assertTrue(passwordEncoder.matches("password", foundByUid.get().getPassword()), "Password should match");
        Assertions.assertEquals("test@example.com", foundByUid.get().getEmail());

        // Retrieve the Member by email
        Optional<Member> foundByEmail = memberRepository.findByEmail("test@example.com");
        Assertions.assertTrue(foundByEmail.isPresent(), "Member should be present");
        Assertions.assertEquals("testNickname", foundByEmail.get().getNickname());
        Assertions.assertEquals("testUid", foundByEmail.get().getUid());
        Assertions.assertTrue(passwordEncoder.matches("password", foundByEmail.get().getPassword()), "Password should match");

        // Test login logic
        Member loginAttempt = new Member();
        loginAttempt.setUid("testUid");
        loginAttempt.setPassword("password");

        try {
            Member loggedInMember = memberService.login(loginAttempt);
            Assertions.assertNotNull(loggedInMember, "Login should be successful");
            Assertions.assertEquals("testUid", loggedInMember.getUid(), "Logged in user's uid should match");
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Login failed: " + e.getMessage());
        }
    }
}
