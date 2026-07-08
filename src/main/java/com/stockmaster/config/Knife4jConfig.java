package com.stockmaster.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / Swagger 配置类
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI stockMasterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StockMaster 仓库管理系统 API")
                        .description("WMS 仓库管理系统接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("StockMaster Team"))
                        .license(new License()
                                .name("Apache 2.0")));
    }
}
