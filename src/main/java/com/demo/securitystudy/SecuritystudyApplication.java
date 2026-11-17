package com.demo.securitystudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SecuritystudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecuritystudyApplication.class, args);
    }

}
