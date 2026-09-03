package org.intern.personalfinancemanagementsystem.constant;

public final class CommonConstant {
    public static final int FULL_NAME_MAX_LENGTH = 120;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int PASSWORD_MAX_LENGTH = 120;
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[!@#$%^&*(){}_+=~`|:;'<>,./?])(?=.*[a-z])(?=.*[A-Z]).{6,120}$";
    public static final int PHONE_NUMBER_MAX_LENGTH = 15;
    public static final String PHONE_REGEX = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$";
    public static final int DATE_OF_BIRTH_LENGTH = 15;

    public static final class User {
        public static final int AVATAR_LENGTH = 500;
    }
}
