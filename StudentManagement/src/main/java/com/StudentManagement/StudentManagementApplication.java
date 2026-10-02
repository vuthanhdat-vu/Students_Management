package com.StudentManagement;

import com.StudentManagement.config.AdminBean;
import com.StudentManagement.config.StudentBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class StudentManagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(StudentManagementApplication.class, args);
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StudentManagementApplication.class);
		System.out.println("ApplicationContext được khởi tạo");
		AdminBean adminBean = context.getBean(AdminBean.class);
		StudentBean studentBean = context.getBean(StudentBean.class);
	}

}
