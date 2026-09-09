package org.intern.personalfinancemanagementsystem.constant;

public final class SuccessMessage {
    public static final String SUCCESS_CODE = "SUCCESS";

    public static final class Auth {
        public static final String REGISTER_SUCCESS = "Register successfully.";
        public static final String LOGIN_SUCCESS = "Login successfully";
        public static final String LOGOUT_SUCCESS = "Logout successfully";
        public static final String REFRESH_TOKEN_SUCCESS = "Refresh token successfully";
    }

    public static final class User {
        public static final String GET_PROFILE_SUCCESS = "Profile retrieved successfully";
        public static final String UPDATE_PROFILE_SUCCESS = "Update profile successfully";
        public static final String CHANGE_PASSWORD_SUCCESS = "Password changed successfully";
    }
}
