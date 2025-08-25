package com.hackathon2_BE.pium.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    /**
     * 로컬 개발:  ./uploads
     * 서버(컨테이너): /app/uploads  (docker-compose에서 마운트)
     * application.properties / .env 로 덮어쓸 수 있음.
     */
    @Value("${app.upload.dir:/app/uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 절대경로로 변환
        Path absolute = Paths.get(uploadDir).toAbsolutePath();

        // file: 스킴 보장 + 끝에 / 보장
        String location = absolute.toUri().toString();   // 예) file:/app/uploads/
        if (!location.startsWith("file:")) {
            location = "file:" + absolute.toString();
        }
        if (!location.endsWith("/")) {
            location = location + "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)   // 예) file:/app/uploads/
                .setCachePeriod(3600)             // 선택: 캐시 1시간
                .resourceChain(true);
    }
}
