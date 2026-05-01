package com.example.livreurservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients   
public class LivreurServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivreurServiceApplication.class, args);
    }
}
