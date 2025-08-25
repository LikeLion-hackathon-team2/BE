package com.hackathon2_BE.pium.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon2_BE.pium.dto.ApiErrorResponse;
import com.hackathon2_BE.pium.security.CustomUserDetailsService;
import com.hackathon2_BE.pium.security.JwtAuthenticationFilter;
import com.hackathon2_BE.pium.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 예: "https://hack2-pium.vercel.app,http://localhost:5173"
    // 환경변수 APP_CORS_ALLOWED_ORIGINS 로 매핑됩니다(스프링 relaxed binding).
    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String corsAllowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtTokenProvider tokenProvider,
            CustomUserDetailsService userDetailsService,
            // 자동 구성된 ObjectMapper 주입 (JavaTimeModule 포함)
            ObjectMapper om
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 컨트롤러 진입 전 401/403도 JSON으로
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(401);
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            res.getWriter().write(om.writeValueAsString(
                                    ApiErrorResponse.of("UNAUTHORIZED", "인증이 필요합니다.")
                            ));
                        })
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(403);
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            res.getWriter().write(om.writeValueAsString(
                                    ApiErrorResponse.of("FORBIDDEN", "권한이 없습니다.")
                            ));
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Actuator / Swagger
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/swagger-resources/**", "/webjars/**"
                        ).permitAll()

                        // 정적
                        .requestMatchers("/uploads/**").permitAll()

                        // 공개 엔드포인트
                        .requestMatchers("/api/user/signup", "/api/auth/login").permitAll()

                        // 공개 조회(READ): GET만 허용
                        .requestMatchers(HttpMethod.GET,
                                "/api/product", "/api/product/**",
                                "/api/events/**"
                        ).permitAll()

                        // 나머지는 인증
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        // JWT 필터
        http.addFilterBefore(
                new JwtAuthenticationFilter(tokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        CorsConfiguration cfg = new CorsConfiguration();
        // 와일드카드/정확 매칭 모두 허용
        cfg.setAllowedOriginPatterns(origins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization", "Location", "Content-Disposition"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
