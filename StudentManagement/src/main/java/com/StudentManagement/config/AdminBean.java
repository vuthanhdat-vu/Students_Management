package com.StudentManagement.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Getter
@Setter
public class AdminBean {
    @Lazy
    @Autowired
    private StudentBean studentBean;
    public AdminBean(){
        System.out.println("tao Bean Admin");
    }
    public StudentBean getStudentBean(){
        return studentBean;
    }
}
