package com.example.subjectservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class SubjectServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubjectServiceApplication.class, args);
    }

}
