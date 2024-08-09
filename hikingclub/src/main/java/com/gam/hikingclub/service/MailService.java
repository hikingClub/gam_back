package com.gam.hikingclub.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "vlfodn00@gmail.com"; // 발신자 이메일 주소

    // 랜덤 인증 코드를 생성하는 메소드
    public static String createToken() {
        return String.valueOf((int) (Math.random() * (90000)) + 100000); // 100000 ~ 999999 사이의 랜덤 숫자 생성
    }

    // 인증 이메일을 생성하는 메소드
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

    // 이메일을 전송하는 메소드
    public void sendMail(String mail, String token) {
        MimeMessage message = createMail(mail, token);
        javaMailSender.send(message);
    }

    // 알림 이메일을 생성 및 전송하는 메소드
    public void sendNotificationMail(String mail, List<String> messageContents) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(mail);
            helper.setSubject("디지털 규장각 구독 설정 알림입니다.");

            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append("<h3>디지털 규장각 구독 키워드 알림입니다.</h3>");
            bodyBuilder.append("<h3>추가된 데이터 제목:</h3>");

            for (String content : messageContents) {
                bodyBuilder.append("<p>").append(content).append("</p>");
            }

            bodyBuilder.append("<h3>자세한 내용을 디지털 규장각 마이페이지 내 구독설정에서 확인하세요.</h3>");

            helper.setText(bodyBuilder.toString(), true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        javaMailSender.send(message);
    }
}
