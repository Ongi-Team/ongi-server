package com.ssu.ongi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan
public class OngiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OngiApplication.class, args);
    }

}
