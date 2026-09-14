package org.intern.personalfinancemanagementsystem.service.impl.sender;

import org.intern.personalfinancemanagementsystem.service.OtpSender;
import org.springframework.stereotype.Service;

@Service("sms")
public class SmsOtpSender implements OtpSender {

    @Override
    public void send(String to, String otpCode) {

    }
}
