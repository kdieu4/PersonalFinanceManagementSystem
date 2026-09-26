package org.intern.personalfinancemanagementsystem.mock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.service.OtpSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("sms")
@Profile("dev")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockSmsOtpSender implements OtpSender {

    @Override
    public void send(String to, String otp) {
        log.info("---Sending SMS to {} with {}", to, otp);
    }
}
