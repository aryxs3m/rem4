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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BaseFeature
 *
 * A Feature is a function for the bot. A Feature is a JDA event listener and handles one function at a time.
 * Every Feature should extend this class.
 */
public class BaseFeature extends ListenerAdapter implements FeatureInterface {
    private final String commandPrefix = "?";
    private final HashMap<String, String> slashCommands = new HashMap<>();
    protected Logger logger =  LoggerFactory.getLogger(this.getClass());

    /**
     * This is called after JDA is initialized successfully. If the Feature depends on something that can only be
     * accessible after the bot is ready, use this.
     */
    @Override
    public void onJDAInit()
    {
        // You can execute things here that needed the JDA from Main
    }

    /**
     * Initializes slash commands.
     * @todo currently not in use, slash commands are not implemented
     */
    @Override
    public void initSlashCommands() {
        JDA JDA = this.getJDA();
        for (Map.Entry<String, String> slashCommand: slashCommands.entrySet()) {
            JDA.upsertCommand(slashCommand.getKey(), slashCommand.getValue()).queue();
        }
    }

    /**
     * Is the received message a "command"?
     * @param event
     * @param command command name string, without prefix
     * @return
     */
    protected boolean isCommand(MessageReceivedEvent event, String command)
    {
        return event.getMessage().getContentDisplay().startsWith(this.commandPrefix.concat(command));
    }

    /**
     * Returns command parameters. Parameters are everything that typed after the command. You can pass multiple
     * parameters separated by spaces. If you want to pass a parameter with space in it, then you must escape it
     * using " characters.
     * @param event
     * @param required
     * @return
     * @throws RequiredParameterException
     */
    protected String[] getCommandParameters(MessageReceivedEvent event, int required) throws RequiredParameterException {
        String originalMessage = event.getMessage().getContentDisplay();
        String[] params = originalMessage.split(" ");
        String withoutCommand = String.join(" ", Arrays.copyOfRange(params, 1, params.length));

        ArrayList<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(withoutCommand);
        while (m.find()) {
            list.add(m.group(1).replace("\"", ""));
        }

        if (params.length < required + 1) {
            throw new RequiredParameterException();
        }

        return list.toArray(new String[0]);
    }

    /**
     * @tood slash commands are not implemented yet
     */
    protected boolean isSlashCommand(SlashCommandInteractionEvent event, String command)
    {
        return event.getName().equals(command);
    }

    /**
     * Is the message sent by an admin?
     * @param event
     * @return true, when the message sent by an admin
     */
    protected boolean isFromAdmin(MessageReceivedEvent event)
    {
        String userId = event.getAuthor().getId();
        for (String adminUserId: Main.systemConfig.getAdminUsers()) {
            if (adminUserId.equals(userId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the JDA instance.
     * @return
     */
    protected JDA getJDA()
    {
        return Main.JDA;
    }

    /**
     * @todo slash commands are not implemented yet
     */
    protected void addSlashCommand(String name, String description)
    {
        this.slashCommands.put(name, description);
    }
}
