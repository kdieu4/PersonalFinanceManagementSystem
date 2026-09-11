package org.intern.personalfinancemanagementsystem.service;

import com.nimbusds.jose.util.StandardCharset;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.constant.KafkaConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.message.ForgotPasswordMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailService {
    @Value("${spring.mail.username}")
    String emailFrom;

    final JavaMailSender javaMailSender;
    final TemplateEngine templateEngine;
    final ObjectMapper objectMapper;

    public void sendEmail(String recipients, String subject, String content) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(recipients);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);

        javaMailSender.send(simpleMailMessage);
    }

    @KafkaListener(topics = KafkaConstant.FORGOT_PASSWORD_TOPIC, groupId = KafkaConstant.FORGOT_PASSWORD_GROUP)
    public void sendOTPByKafka(String jsonMessage) throws MessagingException, UnsupportedEncodingException {
        log.info("========== KAFKA CONSUMER ==========");
        log.info("Message nhận được: {}", jsonMessage);

        ForgotPasswordMessage message = null;
        try {
            message = objectMapper.readValue(jsonMessage, ForgotPasswordMessage.class);

            log.info("Email: " + message.getEmail());
            log.info("OTP: " + message.getOtp());
            // 1. Đưa vào context
            Context context = new Context();
            context.setVariable("OTP", message.getOtp());
            String htmlContent = templateEngine.process("verify-otp-email.html", context);
            log.info("Da render HTML email");

            // 2. Cau hinh noi dung hien thi
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharset.UTF_8.name());

            helper.setFrom(emailFrom, "DIDI");
            helper.setTo(message.getEmail());
            helper.setSubject("Please confirm your account");
            helper.setText(htmlContent, true);

            // gui email
            log.info("----Dang gui email----");
            javaMailSender.send(mimeMessage);
            log.info("Gui mail thanh cong");
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("Lỗi khi gửi email otp: " + message.getEmail() + e);
        }
    }
}
