package com.StudentManagement.enums;

public enum ApplicationExceptionCode {

    ACCESS_DENIED("Bạn không có quyền truy cập"),
    USER_NOT_FOUND("Không tìm thấy người dùng"),
    STUDENT_NOT_FOUND("Không tìm thấy sinh viên"),
    COURSE_NOT_FOUND("Không tìm thấy môn học"),
    ENROLLMENT_NOT_FOUND("Không tìm thấy đăng ký môn học");

    private final String message;

    ApplicationExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}