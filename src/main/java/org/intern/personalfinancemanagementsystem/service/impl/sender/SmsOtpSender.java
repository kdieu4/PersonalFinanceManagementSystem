package org.intern.personalfinancemanagementsystem.service.impl.sender;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.CommonConstant;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.service.OtpSender;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service("sms")
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SmsOtpSender implements OtpSender {
    SnsClient snsClient;

    @Override
    public void send(String to, String otp) {
        log.info("---Sending SMS to {} with {}", to, otp);
        try {
            // 1. Soan tin nhan
            String message = otp + CommonConstant.MESSAGE_OTP_SMS;
            String phoneNumber = "+84" + to.substring(1);
            PublishRequest request = PublishRequest
                    .builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();
            // 2. Chuyen cho Sns
            snsClient.publish(request);
        } catch (AwsServiceException | SdkClientException e) {
            log.error("----{}: {}----", ErrorMessage.Auth.ERR_SEND_OTP_SMS, e.getMessage());
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.Auth.ERR_SEND_OTP_SMS, ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }
}
