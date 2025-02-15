package com.example.discordbot.bot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                String endHourInput = event.getOption("endhour").getAsString();     // e.g., "02:00"
        
                // Use the current year automatically
                int currentYear = LocalDate.now().getYear();
                String fullStart = dayInput + "." + currentYear + " " + startHourInput;
                String fullEnd = dayInput + "." + currentYear + " " + endHourInput;
        
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        
                LocalDateTime startHour = LocalDateTime.parse(fullStart, formatter);
                LocalDateTime endHour = LocalDateTime.parse(fullEnd, formatter);
        
                LocalDate startDay = startHour.toLocalDate();
                LocalDate endDay = startDay; // Default to startDay
        
                if (endHour.isBefore(startHour)) {
                    // If endHour is before startHour, assume it’s on the next day
                    endDay = startDay.plusDays(1);
                    endHour = endHour.plusDays(1);
                }
        
                String userId = event.getUser().getId();
                String username = event.getUser().getName();
        
                WorkHours workHours = new WorkHours(userId, username, startDay, startHour, endHour, LocalDateTime.now());
                workHours.setEndDay(endDay); // Set endDay after object creation
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

        else if (event.getName().equals("deletehours")) {
            if (event.getOption("day") == null) {
                event.reply("⚠️ Please provide the day in the format `DD.MM` (e.g., `26.02`).").queue();
                return;
            }
        
            try {
                String dayInput = event.getOption("day").getAsString();  // e.g., "26.02"
                int currentYear = LocalDate.now().getYear();
                
                // Ensure correct parsing of user input
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate dateToDelete = LocalDate.parse(dayInput + "." + currentYear, formatter);
                
                String userId = event.getUser().getId();
                
                System.out.println("Attempting to delete hours for date: " + dateToDelete);
        
                int deletedCount = workHoursRepository.deleteByUserIdAndDate(userId, dateToDelete);
        
                if (deletedCount > 0) {
                    event.reply("🗑️ Deleted " + deletedCount + " work hour entries for " + dayInput + ".").queue();
                } else {
                    event.reply("⚠️ No work hours found for " + dayInput + ".").queue();
                }
            } catch (Exception e) {
                event.reply("⚠️ Error: " + e.getMessage()).queue();
                System.err.println("Error parsing date: " + e.getMessage());  
            }
        }

        else if (event.getName().equals("deleteallhours")) {
            String userId = event.getUser().getId();
            int deletedCount = workHoursRepository.deleteAllByUserId(userId);
        
            if (deletedCount > 0) {
                event.reply("🗑️ Deleted all **" + deletedCount + "** work hour entries.").queue();
            } else {
                event.reply("⚠️ No work hours found to delete.").queue();
            }
        }
        
    }
}
