package com.swatching.swatching_be.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String CURRENT_USER_HEADER = "X-USER-ID";

    @Bean
    public OpenAPI swatchingOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(CURRENT_USER_HEADER, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(CURRENT_USER_HEADER)))
                .addSecurityItem(new SecurityRequirement().addList(CURRENT_USER_HEADER))
                .info(new Info()
                        .title("Swatching API")
                        .description("Swatching backend API documentation. 임시 인증은 X-USER-ID 헤더에 사용자 ID를 넣어 테스트합니다.")
                        .version("v1"));
    }
}
