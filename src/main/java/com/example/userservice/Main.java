package com.example.userservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.example.userservice")
public class Main implements CommandLineRunner {
    public static void main(String[] args) { SpringApplication.run(Main.class, args); }

    @Override
    public void run(String...args) throws Exception {}
}
