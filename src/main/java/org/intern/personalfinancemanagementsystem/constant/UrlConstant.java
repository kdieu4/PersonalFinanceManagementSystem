package org.intern.personalfinancemanagementsystem.constant;

public final class UrlConstant {
    public static class Auth {
        public static final String PREFIX = "/auth";

        public static final String REGISTER = PREFIX + "/register";
        public static final String LOGIN = PREFIX + "/login";
        public static final String LOGOUT = PREFIX + "/logout";
        public static final String REFRESH_TOKEN = PREFIX + "/refresh-token";
    }

    public static class User {
        public static final String PREFIX = "/user";

        public static final String GET_PROFILE = PREFIX + "/profile";
        public static final String CHANGE_PASSWORD = PREFIX + "/change-password";
    }
}
