/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.FeatureSet;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Uptime Feature
 *
 * Shows the server uptime.
 */
public class Uptime extends BaseFeature {

    public Uptime() {
        addSlashCommand("uptime", "Check server uptime");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "uptime")) {
            try {
                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle(Main.localization.get("uptime_server_uptime"));
                embedBuilder.setDescription(FeatureSet.getCLI("uptime"));

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            } catch (Exception e) {
                event.getChannel().sendMessage(Main.localization.get("error_unknown")).queue();
                logger.error("Uptime check error.", e);
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (isSlashCommand(event, "uptime")) {
            try {
                event.reply(FeatureSet.getCLI("uptime")).setEphemeral(true).queue();
            } catch (Exception e) {
                event.reply("Cannot check uptime right now.").setEphemeral(true).queue();
            }
        }
    }
}
