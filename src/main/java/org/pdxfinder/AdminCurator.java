package org.pdxfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@SpringBootApplication
@EnableCaching
@EnableOAuth2Client
@EnableAuthorizationServer
public class AdminCurator {

    public static void main(String[] args) {

        SpringApplication.run(AdminCurator.class, args);

    }
}