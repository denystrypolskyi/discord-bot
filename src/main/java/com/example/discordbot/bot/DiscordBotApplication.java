package com.example.discordbot.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.discordbot.model")  // <-- dodaj to
@EnableJpaRepositories(basePackages = "com.example.discordbot.repository")
public class DiscordBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscordBotApplication.class, args);
    }
}
