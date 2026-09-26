package org.intern.personalfinancemanagementsystem.constant;

public final class RedisConstant {
    public static String OTP_FORGOT_PASSWORD_KEY = "PFMS:auth:forgot_pwd:";
    public static String RESET_PASSWORD_VERIFIED_KEY = "PFMS:auth:reset_pwd:";
    public static int OTP_FORGOT_PASSWORD_TTL = 5;
}
