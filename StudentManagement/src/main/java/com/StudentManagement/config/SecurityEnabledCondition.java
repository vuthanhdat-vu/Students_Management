package com.StudentManagement.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SecurityEnabledCondition implements Condition {

    @Override
    public boolean matches(
            ConditionContext context,
            AnnotatedTypeMetadata metadata) {

        String enabled =
                context.getEnvironment()
                        .getProperty("app.security.enabled");

        return "true".equalsIgnoreCase(enabled);
    }
}