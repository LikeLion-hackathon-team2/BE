package com.hackathon2_BE.pium.config;

import com.hackathon2_BE.pium.security.CustomUserDetailsService;
import com.hackathon2_BE.pium.security.JwtAuthenticationFilter;
import com.hackathon2_BE.pium.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 배포 시 여기에 콤마(,)로 오리진 나열: ex) https://app.com,https://staging.app.com,http://localhost:5173
    // 비워두면(또는 미설정이면) 개발 편의상 모든 오리진 허용(*)
    @Value("${app.cors.allowed-origins:}")
    private String allowedOriginsProp;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtTokenProvider tokenProvider,
            CustomUserDetailsService userDetailsService
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ⬅️ CORS 적용
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/swagger-resources/**", "/webjars/**"
                        ).permitAll()
                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 공개 엔드포인트
                        .requestMatchers("/api/user/signup", "/api/auth/login").permitAll()
                        // TODO: 개발 편의로 열어둔 API, 배포 전 정리 권장
                        .requestMatchers("/api/product/**").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable());

        // JWT 인증 필터
        http.addFilterBefore(
                new JwtAuthenticationFilter(tokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // ⚙️ allowed-origins 프로퍼티가 비어있다면: 개발용으로 모든 오리진 허용
        if (allowedOriginsProp == null || allowedOriginsProp.isBlank()) {
            // with credentials=false일 때만 * 허용 가능 (우리는 JWT를 Authorization 헤더로 쓰므로 false)
            cfg.setAllowedOriginPatterns(List.of("*"));
        } else {
            // 배포/스테이징/로컬 도메인을 콤마로 분리해 정확히 허용
            List<String> origins = Arrays.stream(allowedOriginsProp.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
            cfg.setAllowedOrigins(origins); // 정확한 Origin만 허용
        }

        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
        cfg.setExposedHeaders(List.of());      // 필요 시 추가
        cfg.setAllowCredentials(false);        // ✅ JWT는 헤더로 전달 → 보통 false
        cfg.setMaxAge(3600L);                  // preflight 캐시 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
