package com.example.discordbot;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.EnumSet;

@SpringBootApplication
public class DiscordBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscordBotApplication.class, args);
    }
}

@Component
class DiscordBot {
    private final MessageReceiveListener messageReceiveListener;
    
    private Dotenv dotenv;
    private String token;

    private JDA jda;

    public DiscordBot(MessageReceiveListener messageReceiveListener) {
        dotenv = Dotenv.load();
        token = dotenv.get("DISCORD_TOKEN");
        this.messageReceiveListener = messageReceiveListener;
    }

    @PostConstruct
    public void startBot() throws Exception {
        jda = JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(messageReceiveListener)
                .build();
    }
}
