package com.viki.home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { 
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class 
})
public class AWSDemoApplication{

    public static void main(String[] args) throws Exception{
        SpringApplication.run(AWSDemoApplication.class, args);
    }
}

