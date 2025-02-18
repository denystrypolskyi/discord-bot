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
        switch (event.getName()) {
            case "addhours" -> handleAddHoursCommand(event);
            case "gethours" -> handleGetHoursCommand(event);
            case "deletehours" -> handleDeleteHoursCommand(event);
            case "clearhours" -> handleClearHoursCommand(event);
            case "salary" -> handleSalaryCommand(event);
            default -> event.reply("⚠️ Unknown command.").queue();
        }
    }

    private void handleAddHoursCommand(SlashCommandInteractionEvent event) {
        if (event.getOption("day") == null || event.getOption("shiftstart") == null
                || event.getOption("shiftend") == null) {
            event.reply("⚠️ Please provide `day`, `shiftstart`, and `shiftend` in the format `DD.MM HH:mm HH:mm`.")
                    .queue();
            return;
        }

        try {
            String dayInput = event.getOption("day").getAsString();
            String shiftStartInput = event.getOption("shiftstart").getAsString();
            String shiftEndInput = event.getOption("shiftend").getAsString();

            int currentYear = LocalDate.now().getYear();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            String formattedStart = dayInput + "." + currentYear + " " + shiftStartInput;
            String formattedEnd = dayInput + "." + currentYear + " " + shiftEndInput;

            LocalDateTime shiftStart = LocalDateTime.parse(formattedStart, formatter);
            LocalDateTime shiftEnd = LocalDateTime.parse(formattedEnd, formatter);

            if (shiftEnd.isBefore(shiftStart)) {
                shiftEnd = shiftEnd.plusDays(1);
            }

            String userId = event.getUser().getId();
            String username = event.getUser().getName();

            WorkHours workHours = new WorkHours(userId, username, shiftStart, shiftEnd, LocalDateTime.now());
            workHoursRepository.save(workHours);

            event.reply("✅ Logged work hours for " + username + " on " + dayInput + " from " + shiftStartInput + " to "
                    + shiftEndInput).queue();
        } catch (Exception e) {
            event.reply("⚠️ Invalid date format! Use `DD.MM HH:mm HH:mm`. Error: " + e.getMessage()).queue();
        }
    }

    private void handleGetHoursCommand(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        int month = LocalDate.now().getMonthValue(); 
        int year = LocalDate.now().getYear();
    
        if (event.getOption("month") != null) {
            try {
                month = Integer.parseInt(event.getOption("month").getAsString());
                if (month < 1 || month > 12) {
                    event.reply("⚠️ Invalid month! Please provide a number between 1 and 12.").queue();
                    return;
                }
            } catch (NumberFormatException e) {
                event.reply("⚠️ Invalid month format! Use a number (e.g., `2` for February).").queue();
                return;
            }
        }
    
        Long totalMinutes = workHoursRepository.getTotalMinutesWorkedByMonth(userId, month, year);
    
        if (totalMinutes == null || totalMinutes == 0) {
            event.reply("📅 " + event.getUser().getName() + ", you have no recorded work hours for **" + month + "/" + year + "**.").queue();
            return;
        }
    
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
    
        event.reply("⏳ " + event.getUser().getName() + ", you have logged **" + hours + "h " + minutes +
                "m** of work in **" + month + "/" + year + "**.").queue();
    }
    

    private void handleDeleteHoursCommand(SlashCommandInteractionEvent event) {
        if (event.getOption("day") == null) {
            event.reply("⚠️ Please provide the day in the format `DD.MM`.").queue();
            return;
        }

        try {
            String dayInput = event.getOption("day").getAsString();
            int currentYear = LocalDate.now().getYear();

            LocalDate dateToDelete = LocalDate.parse(dayInput + "." + currentYear,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            String userId = event.getUser().getId();
            int deletedCount = workHoursRepository.deleteByUserIdAndDate(userId, dateToDelete);

            if (deletedCount > 0) {
                event.reply("🗑️ Deleted " + deletedCount + " work hour entries for " + dayInput + ".").queue();
            } else {
                event.reply("⚠️ No work hours found for " + dayInput + ".").queue();
            }
        } catch (Exception e) {
            event.reply("⚠️ Error parsing date. Please use `DD.MM`.").queue();
        }
    }

    private void handleClearHoursCommand(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        int deletedCount = workHoursRepository.deleteAllByUserId(userId);

        if (deletedCount > 0) {
            event.reply("🗑️ Deleted all **" + deletedCount + "** work hour entries.").queue();
        } else {
            event.reply("⚠️ No work hours found to delete.").queue();
        }
    }

    private void handleSalaryCommand(SlashCommandInteractionEvent event) {
        double hourlyRate = 30.50;
        int month = LocalDate.now().getMonthValue(); 
        int year = LocalDate.now().getYear();
    
        if (event.getOption("rate") != null) {
            try {
                hourlyRate = Double.parseDouble(event.getOption("rate").getAsString());
            } catch (NumberFormatException e) {
                event.reply("⚠️ Invalid hourly rate! Please provide a valid number.").queue();
                return;
            }
        }
    
        if (event.getOption("month") != null) {
            try {
                month = Integer.parseInt(event.getOption("month").getAsString());
                if (month < 1 || month > 12) {
                    event.reply("⚠️ Invalid month! Please provide a number between 1 and 12.").queue();
                    return;
                }
            } catch (NumberFormatException e) {
                event.reply("⚠️ Invalid month format! Use a number (e.g., `2` for February).").queue();
                return;
            }
        }
    
        String userId = event.getUser().getId();
        Long totalMinutes = workHoursRepository.getTotalMinutesWorkedByMonth(userId, month, year);
        
        if (totalMinutes == null || totalMinutes == 0) {
            event.reply("📅 " + event.getUser().getName() + ", you have no recorded work hours for **" + month + "/" + year + "**.").queue();
            return;
        }
    
        double totalHours = totalMinutes / 60.0;
        long hours = totalMinutes / 60;   
        long minutes = totalMinutes % 60;

        double salary = totalHours * hourlyRate;

    
        event.reply("💰 " + event.getUser().getName() + ", your estimated salary for **" + month + "/" + year +
        "** is **" + String.format("%.2f", salary) + " PLN** based on **" +
        hours + "h " + minutes + "m** worked.").queue();
    }
}
