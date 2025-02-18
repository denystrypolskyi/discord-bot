package com.example.discordbot.bot;

import java.util.EnumSet;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Component
public class DiscordBot {
    private final SlashCommandListener slashCommandListener;
    private final String token;
    private JDA jda;

    public DiscordBot(SlashCommandListener slashCommandListener) {
        this.slashCommandListener = slashCommandListener;

        // Загружаем токен из переменной окружения или из .env
        Dotenv dotenv = Dotenv.load();
        this.token = System.getenv("DISCORD_TOKEN") != null ? System.getenv("DISCORD_TOKEN")
                : dotenv.get("DISCORD_TOKEN");
    }

    @PostConstruct
    public void startBot() throws Exception {
        jda = JDABuilder.createLight(token, EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS))
                .addEventListeners(slashCommandListener)
                .build()
                .awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("addhours", "Log your work hours")
                        .addOption(OptionType.STRING, "day", "Day (DD.MM)", true)
                        .addOption(OptionType.STRING, "shiftstart", "Start hour (HH:mm)", true)
                        .addOption(OptionType.STRING, "shiftend", "End hour (HH:mm)", true),

                Commands.slash("gethours", "View your total logged work hours")
                        .addOption(OptionType.INTEGER, "month", "Specify the month (1-12)", false),
                Commands.slash("deletehours", "Delete logged work hours for a specific day")
                        .addOption(OptionType.STRING, "day", "Day (DD.MM)", true),

                Commands.slash("clearhours", "Delete all your logged work hours"),

                Commands.slash("salary", "Calculate your estimated salary in PLN")
                        .addOption(OptionType.STRING, "rate", "Your hourly rate (PLN)", false)
                        .addOption(OptionType.INTEGER, "month", "Month number (1-12)", false))
                .queue();
    }
}
