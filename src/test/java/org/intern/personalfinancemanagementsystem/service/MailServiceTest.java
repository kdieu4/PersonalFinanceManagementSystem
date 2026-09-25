package org.intern.personalfinancemanagementsystem.service;

import jakarta.mail.internet.MimeMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.message.ForgotPasswordMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private ForgotPasswordMessage forgotPasswordMessage;

    private MailService mailService;

    private String emailFrom;

    @BeforeEach
    void setUp() {
        mailService = new MailService(javaMailSender, templateEngine, objectMapper);

        emailFrom = "test@example.com";

        ReflectionTestUtils.setField(mailService, "emailFrom", emailFrom);
    }

    @Test
    void sendEmail_whenValidRequest_shouldSendEmailSuccessfully() {
        String recipient = "user@example.com";
        String subject = "Test subject";
        String content = "Test content";

        mailService.sendEmail(recipient, subject, content);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(javaMailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertArrayEquals(new String[]{recipient}, sentMessage.getTo());

        assertEquals(subject, sentMessage.getSubject());

        assertEquals(content, sentMessage.getText());
    }

    @Test
    void sendOTPByKafka_whenValidMessage_shouldSendEmailSuccessfully() throws Exception {

        String jsonMessage = """
                {
                  "email": "user@example.com",
                    "otp": "123456"
                }
                """;

        String recipient = "user@example.com";
        String otp = "123456";
        String htmlContent = "<html>OTP: 123456</html>";

        when(objectMapper.readValue(jsonMessage, ForgotPasswordMessage.class)).thenReturn(forgotPasswordMessage);

        when(forgotPasswordMessage.getEmail()).thenReturn(recipient);

        when(forgotPasswordMessage.getOtp()).thenReturn(otp);

        when(templateEngine.process(eq("verify-otp-email.html"), any())).thenReturn(htmlContent);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendOTPByKafka(jsonMessage);

        verify(objectMapper).readValue(jsonMessage, ForgotPasswordMessage.class);

        verify(templateEngine).process(eq("verify-otp-email.html"), any());

        verify(javaMailSender).createMimeMessage();

        verify(javaMailSender).send(mimeMessage);

        verify(forgotPasswordMessage, atLeastOnce()).getEmail();

        verify(forgotPasswordMessage, atLeastOnce()).getOtp();
    }

    @Test
    void sendOTPByKafka_whenMailSendingFails_shouldNotThrowException() throws Exception {

        String jsonMessage = """
                {
                    "email": "user@example.com",
                    "otp": "123456"
                }
                """;

        when(objectMapper.readValue(jsonMessage, ForgotPasswordMessage.class)).thenReturn(forgotPasswordMessage);

        when(forgotPasswordMessage.getEmail()).thenReturn("user@example.com");

        when(forgotPasswordMessage.getOtp()).thenReturn("123456");

        when(templateEngine.process(eq("verify-otp-email.html"), any())).thenReturn("<html>OTP</html>");

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailSendException("Cannot send email")).when(javaMailSender).send(mimeMessage);

        assertDoesNotThrow(() -> mailService.sendOTPByKafka(jsonMessage));

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void sendOTPByKafka_whenTemplateRenderingFails_shouldNotSendEmail() throws Exception {

        String jsonMessage = """
                {
                    "email": "user@example.com",
                    "otp": "123456"
                }
                """;

        when(objectMapper.readValue(jsonMessage, ForgotPasswordMessage.class)).thenReturn(forgotPasswordMessage);

        when(forgotPasswordMessage.getEmail()).thenReturn("user@example.com");

        when(forgotPasswordMessage.getOtp()).thenReturn("123456");

        when(templateEngine.process(eq("verify-otp-email.html"), any())).thenThrow(new RuntimeException("Template error"));

        assertThrows(RuntimeException.class, () -> mailService.sendOTPByKafka(jsonMessage));

        verify(javaMailSender, never()).createMimeMessage();
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }
}
