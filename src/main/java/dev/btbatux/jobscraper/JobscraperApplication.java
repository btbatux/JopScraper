package dev.btbatux.jobscraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobscraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobscraperApplication.class, args);
    }

}
