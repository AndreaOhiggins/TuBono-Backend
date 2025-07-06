package com.upc.finance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openApiConfiguration(){
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Platform")
                        .description("Finance Platform application REST API documentation")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                        .servers(List.of(new Server().url("https://accurate-cat-production.up.railway.app"))

                );

    }
}
