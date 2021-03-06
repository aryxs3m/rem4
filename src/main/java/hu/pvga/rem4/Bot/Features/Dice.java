/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Random;

/**
 * Dice
 *
 * Throws with a basic 6 sided dice and returns the result.
 */
public class Dice extends BaseFeature {

    public Dice() {
        addSlashCommand("dice", "Throws dice");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "dice")) {
            BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
            embedBuilder.setTitle(Main.localization.get("dice_dice"));
            embedBuilder.setDescription(
                    MessageFormat.format(
                            Main.localization.get("dice_reply"),
                            ((new Random()).nextInt(5) + 1))
            );

            event.getChannel().sendMessageEmbeds(
                    embedBuilder.build()
            ).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (isSlashCommand(event, "dice")) {
            event.reply("Got " + ((new Random()).nextInt(5) + 1)).setEphemeral(true).queue();
        }
    }
}
