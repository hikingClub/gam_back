package com.gam.hikingclub;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testDatabaseConnection() {
        // Create a new Member object
        Member member = new Member();
        member.setNickname("testNickname");
        member.setId("testId");
        member.setPassword("password");
        member.setEmail("test@example.com");
        member.setAlarmCheck(1);  // 여기에서 alarmCheck 값을 설정합니다.

        // Save the Member to the database
        memberRepository.save(member);

        // Retrieve the Member by nickname
        Optional<Member> foundByNickname = memberRepository.findById("testNickname");
        Assertions.assertTrue(foundByNickname.isPresent(), "Member should be present");
        Assertions.assertEquals("testId", foundByNickname.get().getId());
        Assertions.assertEquals("password", foundByNickname.get().getPassword());
        Assertions.assertEquals("test@example.com", foundByNickname.get().getEmail());

        // Retrieve the Member by email
        Optional<Member> foundByEmail = memberRepository.findByEmail("test@example.com");
        Assertions.assertTrue(foundByEmail.isPresent(), "Member should be present");
        Assertions.assertEquals("testNickname", foundByEmail.get().getNickname());
        Assertions.assertEquals("testId", foundByEmail.get().getId());
        Assertions.assertEquals("password", foundByEmail.get().getPassword());
    }
}
