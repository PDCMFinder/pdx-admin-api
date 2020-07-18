package org.pdxfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
//@EnableOAuth2Client
//@EnableAuthorizationServer
public class AdminCurator {

    public static void main(String[] args) {

        SpringApplication.run(AdminCurator.class, args);

    }
}