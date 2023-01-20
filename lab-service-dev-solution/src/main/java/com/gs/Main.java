package com.gs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.Collections;

@RestController
@Configuration
@PropertySource("classpath:application.properties")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Main {

    static private int port=8080;



    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Main.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", port));
        app.run(args);

    }



}