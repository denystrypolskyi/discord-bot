package com.example.discordbot.bot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.springframework.stereotype.Component;

import com.example.discordbot.model.WorkHours;
import com.example.discordbot.repository.WorkHoursRepository;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class SlashCommandListener extends ListenerAdapter {

    private final WorkHoursRepository workHoursRepository;

    public SlashCommandListener(WorkHoursRepository workHoursRepository) {
        this.workHoursRepository = workHoursRepository;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("workhours")) {
            if (event.getOption("day") == null || event.getOption("starthour") == null || event.getOption("endhour") == null) {
                event.reply("⚠️ Please provide `day`, `starthour`, and `endhour` in the format `DD.MM HH:mm HH:mm` (e.g., `26.02 16:00 18:00`).").queue();
                return;
            }

            try {
                String dayInput = event.getOption("day").getAsString();           // e.g., "26.02"
                String startHourInput = event.getOption("starthour").getAsString(); // e.g., "16:00"
                String endHourInput = event.getOption("endhour").getAsString();     // e.g., "18:00"

                // Use the current year automatically
                int currentYear = LocalDate.now().getYear();
                String fullStart = dayInput + "." + currentYear + " " + startHourInput;
                String fullEnd = dayInput + "." + currentYear + " " + endHourInput;

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

                LocalDateTime startHour = LocalDateTime.parse(fullStart, formatter);
                LocalDateTime endHour = LocalDateTime.parse(fullEnd, formatter);

                if (endHour.isBefore(startHour)) {
                    event.reply("⚠️ End time cannot be before start time!").queue();
                    return;
                }

                String userId = event.getUser().getId();
                String username = event.getUser().getName();

                WorkHours workHours = new WorkHours(userId, username, startHour, endHour, LocalDateTime.now());
                workHoursRepository.save(workHours);

                event.reply("✅ Logged work hours for " + username + " on " + dayInput + " from " + startHourInput + " to " + endHourInput).queue();
            } catch (Exception e) {
                event.reply("⚠️ Invalid date format! Use `DD.MM HH:mm HH:mm` (e.g., `26.02 16:00 18:00`).").queue();
            }
        }
        
        else if (event.getName().equals("totalhours")) {
            String userId = event.getUser().getId();
            long totalMinutes = workHoursRepository.getTotalMinutesWorked(userId);
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
    
            event.reply("⏳ " + event.getUser().getName() + ", you have logged **" + hours + "h " + minutes + "m** of work.").queue();
        }
    }
}
