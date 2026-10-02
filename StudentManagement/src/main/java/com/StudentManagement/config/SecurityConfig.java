package com.StudentManagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@ConditionalOnResource(resources = "/application.properties")
//Spring Boot chỉ áp dụng cấu hình khi file application.properties tồn tại.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    @ConditionalOnJava(JavaVersion.TWENTY_ONE)
    //Spring kiểm tra phiên bản Java, nếu đúng là phiên bản 21 thì khởi tạo Bean
    public SecurityInfoService securityInfoService() {
        return new SecurityInfoService();
    }

    @Bean
    @ConditionalOnProperty(
            name = "app.login-log.enabled",
            havingValue = "true"
    )
    // nếu giá trị app.login-log.enabled = true thì Bean này được khởi tạo
    public LoginLogService loginLogService() {
        return new LoginLogService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/students/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACH", "ROLE_STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/students/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/students/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasAuthority("ROLE_ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/courses/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACH", "ROLE_STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAuthority("ROLE_ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/enrollments")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACH")
                .requestMatchers(HttpMethod.GET, "/api/enrollments/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACH", "ROLE_STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/enrollments/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACH", "ROLE_STUDENT")
                .requestMatchers(HttpMethod.DELETE, "/api/enrollments/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACH", "ROLE_STUDENT")
                .requestMatchers(HttpMethod.PUT, "/api/enrollments/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACH")

                .requestMatchers("/api/teachers/**").hasAuthority("ROLE_ADMIN")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:4200")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
