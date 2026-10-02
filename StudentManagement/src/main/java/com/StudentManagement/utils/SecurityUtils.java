package com.StudentManagement.utils;

import com.StudentManagement.enums.ApplicationExceptionCode;
import com.StudentManagement.entity.User;
import com.StudentManagement.exception.ApplicationException;
import com.StudentManagement.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUsername() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new ApplicationException(
                    ApplicationExceptionCode.ACCESS_DENIED
            );
        }

        return authentication.getName();
    }

    public static boolean hasRole(String roleName) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(roleName));
    }

    public static boolean isAdminOrTeacher() {
        return hasRole("ROLE_ADMIN")
                || hasRole("ROLE_TEACH");
    }

    public static boolean isStudent() {
        return hasRole("ROLE_STUDENT");
    }
}