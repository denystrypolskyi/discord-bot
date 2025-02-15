package com.example.discordbot.bot;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.stereotype.Component;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.EnumSet;

@Component
public class DiscordBot {
    private final SlashCommandListener slashCommandListener;
    private Dotenv dotenv;
    private String token;
    private JDA jda;

    public DiscordBot(SlashCommandListener slashCommandListener) {
        dotenv = Dotenv.load();
        token = dotenv.get("DISCORD_TOKEN");
        this.slashCommandListener = slashCommandListener;
    }

    @PostConstruct
    public void startBot() throws Exception {
        jda = JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(slashCommandListener)
                .build()
                .awaitReady();

        jda.updateCommands().addCommands(
            Commands.slash("workhours", "Log your work hours")
                .addOption(OptionType.STRING, "day", "Day (DD.MM)", true)
                .addOption(OptionType.STRING, "starthour", "Start hour (HH:mm)", true)
                .addOption(OptionType.STRING, "endhour", "End hour (HH:mm)", true),
            Commands.slash("totalhours", "View your total logged work hours"),
            Commands.slash("deletehours", "Delete logged work hours for a specific day")
                .addOption(OptionType.STRING, "day", "Day (DD.MM)", true)
        ).queue();
    }

}
