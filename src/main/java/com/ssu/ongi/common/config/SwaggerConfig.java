package com.ssu.ongi.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 기반 bearer 인증")
                        )
                )
                .info(new Info()
                        .title("Ongi Backend API")
                        .description("두드림 온기 프로젝트 백엔드 API 문서")
                        .version("1.0.0")
                        .contact(
                                new Contact()
                                        .name("Ongi Dev Team")
                                        .email("ongi.smartcare@gmail.com")
                        )
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    @Bean
    public OpenApiCustomizer localTimeSchemaCustomizer() {
        return openApi -> openApi.getComponents()
                .addSchemas("LocalTime", new StringSchema()
                        .example("08:30:00")
                        .description("HH:mm:ss 형식의 시간"));
    }
}
