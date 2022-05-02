/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Stopwatch Feature
 *
 * Old Rem command. Calling the command once starts the stopwatch, calling it again stops it and shows the elapsed time.
 */
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
                embedBuilder.setTitle(Main.localization.get("stopwatch_stopwatch"));
                embedBuilder.setDescription(Main.localization.get("stopwatch_started"));

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            } else {
                isStarted = false;

                long diff = (System.nanoTime() - startedAt) / 1000000000;

                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle(Main.localization.get("stopwatch_stopwatch"));
                embedBuilder.setDescription(Main.localization.get("stopwatch_stopped"));
                embedBuilder.addField(
                        Main.localization.get("stopwatch_elapsed"),
                         diff/60 + "m " + diff%60 + "s",
                        true
                );
                embedBuilder.addField(
                        Main.localization.get("stopwatch_started_by"),
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
