package org.intern.personalfinancemanagementsystem.service;

public interface OtpSender {
    void send(String to, String otpCode);
}
