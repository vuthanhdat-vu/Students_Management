package com.StudentManagement.config;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class StudentBean {
    public StudentBean(){
        System.out.println("khoi tao Bean Student");
    }
}
