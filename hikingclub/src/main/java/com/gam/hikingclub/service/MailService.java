package com.gam.hikingclub.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "your-email@gmail.com"; // 발신자 이메일 주소

    // 랜덤으로 숫자 생성
    public static String createToken() {
        return String.valueOf((int) (Math.random() * (90000)) + 100000); //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    // 이메일 생성
    public MimeMessage createMail(String mail, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(mail);
            helper.setSubject("디지털 규장각 이메일 인증번호 입니다.");
            String body = "<h3>안녕하세요,</h3>" +
                    "<h3>요청하신 인증 번호를 안내 드립니다.</h3>" +
                    "<h3>아래 번호를 입력하여 디지털 규장각 회원 인증 절차를 완료해 주세요.</h3>" +
                    "<h1>" + token + "</h1>" +
                    "<h3>감사합니다 디지털 규장각 드림.</h3>";
            helper.setText(body, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    // 이메일 전송
    public void sendMail(String mail, String token) {
        MimeMessage message = createMail(mail, token);
        javaMailSender.send(message);
    }
}
