package org.intern.personalfinancemanagementsystem.constant;

public final class ErrorMessage {
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String VALIDATION_FAILED_CODE = "VALIDATION_FAILED";
    public static final String FORBIDDEN_CODE = "FORBIDDEN";
    public static final String NOT_FOUND_CODE = "NOT_FOUND";
    public static final String BAD_REQUEST_CODE = "BAD_REQUEST";
    public static final String TOO_MANY_REQUEST_CODE = "TOO_MANY_REQUEST";
    public static final String CONFLICT_CODE = "CONFLICT";

    public static final String INVALID_FIELD = "The field is not valid.";
    public static final String NOT_BLANK_FIELD = "The field cannot be blank.";
    public static final String NOT_NULL_FIELD = "The field cannot be null.";
    public static final String INVALID_FORMAT_EMAIL = "Please enter a valid email address.";
    public static final String INVALID_FORMAT_PASSWORD = "Password must be 6-120 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.";
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number format.";
    public static final String ERROR_EMAIL_OR_PHONE_NUMBER_FORMAT = "Invalid email or phone number format.";
    public static final String INVALID_FORMAT_FULL_NAME = "Full name must be less than 120 characters long.";
    public static final String FORBIDDEN_MESSAGE = "You do not have permission to access this resource.";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "An internal system error occurred.";
    public static final String PASSWORD_MISMATCH = "Password do not match.";
    public static final String INVALID_DATE_OF_BIRTH = "Date of birth must be in the past or present";

    public static final class User {
        public static final String EMAIL_EXISTED = "Email is already existed. Please use another email or log in";
        public static final String USER_NOT_EXISTED = "User does not exist.";
        public static final String NULL_POINTER = "User does not exist.";
        public static final String ERR_SEND_EMAIL = "Error while sending otp.";
    }

    public static final class Auth {
        public static final String INVALID_CREDENTIALS = "Invalid email or password";
        public static final String INVALID_OTP = "Invalid otp";
        public static final String INVALID_LOGOUT_TOKEN = "Invalid token provided for logout";
        public static final String INVALID_REFRESH_TOKEN = "Invalid token provided for refresh token";
        public static final String TOKEN_ALREADY_INVALIDATED = "This session is already logged out";
        public static final String GENERATE_JWT_ERROR = "Error while signing JWT";
        public static final String ERR_GET_TOKEN_CLAIM_SET_FAIL = "Error while claiming set";
        public static final String INVALID_PASSWORD = "Invalid password";
        public static final String PASSWORD_SAME_AS_OLD = "New password must be different from the current password";
        public static final String OTP_ALREADY_SENT = "OTP is already sent";
        public static final String ERR_SEND_OTP_SMS = "Send OTP via SMS is fail";
        public static final String INVALID_FORMAT_OTP = "OTP must be exactly 6 digits";
        public static final String RESET_SESSION_EXPIRED = "Your password reset session has expired. Please start the process again.";
    }

    public static class Category {
        public static final String CATEGORY_NOT_EXISTED = "Category does not existed";
        public static final String CATEGORY_EXISTED = "Category is already existed";
        public static final String ERR_CYCLIC_CATEGORY = "Category cannot be its own parent";
    }

    public static class Wallet {
        public static final String WALLET_NOT_EXISTED = "Wallet does not existed";
        public static final String WALLET_EXISTED = "Wallet is already existed";
        public static final String MIN_BALANCE_VALUE = "The wallet balance must be greater than or equals zero";
    }

    public static class Transaction {
        public static final String MIN_VALUE = "The transaction amount must be greater than zero";
        public static final String NULL_VALUE = "The field must not be null";
        public static final String AMOUNT_INVALID = "Transaction amount must be less than wallet balance";
        public static final String TRANSACTION_NOT_EXISTED = "Transaction does not existed";
    }

    public static class Budget {
        public static final String BUDGET_NOT_EXISTED = "Budget does not existed";
        public static final String BUDGET_EXISTED = "Budget existed";
    }

    public static class Goal {
        public static final String GOAL_NOT_EXISTED = "Goal does not existed";
        public static final String GOAL_EXISTED = "Goal existed";
    }

    public static class Report {
        public static final String NOT_VALID_DATE = "End date must not be before start date";
    }
}