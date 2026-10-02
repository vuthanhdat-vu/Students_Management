package com.StudentManagement.exception;

import com.StudentManagement.enums.ApplicationExceptionCode;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final String message;
    private final ApplicationExceptionCode applicationExceptionCode;

    public ApplicationException(ApplicationExceptionCode applicationExceptionCode) {
        super(applicationExceptionCode.getMessage());
        this.applicationExceptionCode = applicationExceptionCode;
        this.message = applicationExceptionCode.getMessage();
    }
}
