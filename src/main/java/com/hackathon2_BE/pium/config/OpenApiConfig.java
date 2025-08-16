package com.hackathon2_BE.pium.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Swagger 상단에 보이는 제목/버전/설명 + JWT Authorize 버튼 설정
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // JWT 보안 스키마 정의 (Bearer 토큰)
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Pium API")
                        .version("v1.0")
                        .description("해커톤 프로젝트용 API 문서"))
                .components(new Components().addSecuritySchemes(
                        securitySchemeName,
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                // 기본으로 모든 API에 인증 스키마를 적용(개별 @Operation에서 덮어쓸 수 있음)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
