package com.example.discordbot;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class MessageReceiveListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
      System.out.printf("[%s] %#s: %s\n",
        event.getChannel(),
        event.getAuthor(),
        event.getMessage().getContentDisplay());
    }
  }