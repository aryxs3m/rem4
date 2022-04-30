/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Exceptions.RequiredParameterException;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseFeature extends ListenerAdapter implements FeatureInterface {
    private final String commandPrefix = "?";
    private final HashMap<String, String> slashCommands = new HashMap<>();
    protected Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Override
    public void initSlashCommands() {
        JDA JDA = this.getJDA();
        for (Map.Entry<String, String> slashCommand: slashCommands.entrySet()) {
            JDA.upsertCommand(slashCommand.getKey(), slashCommand.getValue()).queue();
        }
    }

    /**
     * Is the received message equals the comman
     * @param event
     * @param command
     * @return
     */
    protected boolean isCommand(MessageReceivedEvent event, String command)
    {
        return event.getMessage().getContentDisplay().startsWith(this.commandPrefix.concat(command));
    }

    protected String[] getCommandParameters(MessageReceivedEvent event, int required) throws RequiredParameterException {
        String originalMessage = event.getMessage().getContentDisplay();
        String[] params = originalMessage.split(" ");

        if (params.length < required + 1) {
            throw new RequiredParameterException();
        }

        return Arrays.copyOfRange(params, 1, params.length);
    }

    protected boolean isSlashCommand(SlashCommandInteractionEvent event, String command)
    {
        return event.getName().equals(command);
    }

    protected JDA getJDA()
    {
        return Main.JDA;
    }

    protected void addSlashCommand(String name, String description)
    {
        this.slashCommands.put(name, description);
    }
}
