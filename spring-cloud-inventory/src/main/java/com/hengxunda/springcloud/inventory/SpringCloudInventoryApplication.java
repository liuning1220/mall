package com.hengxunda.springcloud.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.hengxunda.springcloud.inventory.mapper")
public class SpringCloudInventoryApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudInventoryApplication.class, args);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(true).defaultContentType(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
    }
}
