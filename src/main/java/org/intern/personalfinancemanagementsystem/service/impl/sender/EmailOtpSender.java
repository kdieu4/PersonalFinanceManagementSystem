package org.intern.personalfinancemanagementsystem.service.impl.sender;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.constant.KafkaConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.message.ForgotPasswordMessage;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.service.OtpSender;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service("email")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailOtpSender implements OtpSender {
    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;

    @Override
    public void send(String to, String otp) {
        log.info("----send otp message ----");
        try {
            ForgotPasswordMessage forgotPasswordMessage = new ForgotPasswordMessage(to, otp);
            String jsonString = objectMapper.writeValueAsString(forgotPasswordMessage);
            kafkaTemplate.send(KafkaConstant.FORGOT_PASSWORD_TOPIC, jsonString)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Kafka gửi thất bại", exception);
                        } else {
                            log.info(
                                    "Kafka gửi thành công: partition={}, offset={}",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset()
                            );
                        }
                    });
        } catch (JacksonException e) {
            log.error("Lỗi gửi gửi otp");
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }
}
