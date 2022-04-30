/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Stopwatch extends BaseFeature {
    private boolean isStarted = false;
    private User startedBy;
    private long startedAt;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "stopwatch") || isCommand(event, "sw")) {

            if (!isStarted) {
                isStarted = true;
                startedAt = System.nanoTime();
                startedBy = event.getAuthor();

                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle("Stopwatch");
                embedBuilder.setDescription("Stopwatch started. Call again to stop.");

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            } else {
                isStarted = false;

                long diff = (System.nanoTime() - startedAt) / 1000000000;

                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle("Stopwatch");
                embedBuilder.setDescription("Stopwatch stopped.");
                embedBuilder.addField(
                        "Elapsed time",
                         diff/60 + "m " + diff%60 + "s",
                        true
                );
                embedBuilder.addField(
                        "Started by",
                        startedBy.getName(),
                        true
                );

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            }
        }
    }
}
