/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import hu.pvga.rem4.Bot.Exceptions.RequiredParameterException;
import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.EventsConfig;
import hu.pvga.rem4.Main;
import hu.pvga.rem4.Models.Event;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Events Feature
 *
 * You can define (recurring) events. These events will be announced daily at a given time and will appear in the bots
 * presence/activity. Events are stored in database, management also done by using administrator commands.
 *
 * Configuration: announcement channel id, announcement hour
 */
public class Events extends BaseFeature {
    private final EventsConfig eventsConfig;
    ArrayList<Event> events = new ArrayList<>();

    public Events() throws SQLException, IOException {
        eventsConfig = ConfigManager.load(EventsConfig.class);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "reannounce")) {
            updateActiveEvent(true);
        }

        if (isCommand(event, "event-list")) {
            if (!isFromAdmin(event)) {
                event.getChannel().sendMessage("You are not an administrator.").queue();
                return;
            }

            try {
                Dao<Event, String> eventDao = DaoManager.createDao(Main.database.connectionSource, Event.class);
                StringBuilder eventsTable = new StringBuilder();
                for (Event eventItem: eventDao.queryForAll()) {
                    eventsTable.append("#");
                    eventsTable.append(eventItem.getId());
                    eventsTable.append("\t");

                    if (eventItem.getYear() == null) {
                        eventsTable.append("-");
                    } else {
                        eventsTable.append(eventItem.getYear());
                    }
                    eventsTable.append("\t");

                    if (eventItem.getMonth() == null) {
                        eventsTable.append("-");
                    } else {
                        eventsTable.append(eventItem.getMonth());
                    }
                    eventsTable.append("\t");

                    eventsTable.append(eventItem.getDay());
                    eventsTable.append("\t");

                    eventsTable.append(eventItem.getActivityMessage());
                    eventsTable.append("\t");

                    eventsTable.append(eventItem.getAnnounceMessage());
                    eventsTable.append("\t\n");
                }
                event.getChannel().sendMessage("```" + eventsTable.toString() + "```").queue();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (isCommand(event, "event-remove")) {
            if (!isFromAdmin(event)) {
                event.getChannel().sendMessage("You are not an administrator.").queue();
                return;
            }

            try {
                String[] args = getCommandParameters(event, 1);

                Dao<Event, String> eventDao = DaoManager.createDao(Main.database.connectionSource, Event.class);
                eventDao.deleteById(args[0]);

                event.getChannel().sendMessage("Event #" + args[0] + " deleted.").queue();
            } catch (SQLException e) {
                event.getChannel().sendMessage("Could not save. SQL Error: ```" + e.getMessage() + "```").queue();
                logger.error("Could not save event.", e);
            } catch (RequiredParameterException e) {
                event.getChannel().sendMessage("Missing parameter: you need to pass the ID.").queue();
            }
        }

        if (isCommand(event, "event-reload")) {
            if (!isFromAdmin(event)) {
                event.getChannel().sendMessage("You are not an administrator.").queue();
                return;
            }

            try {
                events.clear();
                loadEventsFromDatabase();
                updateActiveEvent(false);

                event.getChannel().sendMessage("Reloaded events.").queue();
            } catch (SQLException e) {
                event.getChannel().sendMessage("Could not save. SQL Error: ```" + e.getMessage() + "```").queue();
                logger.error("Could not save event.", e);
            }
        }

        if (isCommand(event, "event-add")) {
            if (!isFromAdmin(event)) {
                event.getChannel().sendMessage("You are not an administrator.").queue();
                return;
            }

            try {
                String[] args = getCommandParameters(event, 3);

                Event newEvent = new Event();

                switch (args.length)
                {
                    case 3:
                        newEvent.setDay(Integer.parseInt(args[0]));
                        newEvent.setActivityMessage(args[1]);
                        newEvent.setAnnounceMessage(args[2]);
                        break;

                    case 4:
                        newEvent.setMonth(Integer.parseInt(args[0]));
                        newEvent.setDay(Integer.parseInt(args[1]));
                        newEvent.setActivityMessage(args[2]);
                        newEvent.setAnnounceMessage(args[3]);
                        break;

                    case 5:
                        newEvent.setYear(Integer.parseInt(args[0]));
                        newEvent.setMonth(Integer.parseInt(args[1]));
                        newEvent.setDay(Integer.parseInt(args[2]));
                        newEvent.setActivityMessage(args[3]);
                        newEvent.setAnnounceMessage(args[4]);
                        break;
                }

                Dao<Event, String> eventDao = DaoManager.createDao(Main.database.connectionSource, Event.class);
                eventDao.create(newEvent);

                event.getChannel().sendMessage("Created new event #" + newEvent.getId()).queue();

            } catch (RequiredParameterException e) {
                event.getChannel().sendMessage("Missing parameters. Help: ```event-add [year] [month] day activityMessage announceMessage```").queue();
            } catch (SQLException e) {
                event.getChannel().sendMessage("Could not save. SQL Error: ```" + e.getMessage() + "```").queue();
                logger.error("Could not save event.", e);
            }
        }
    }

    @Override
    public void onJDAInit() {
        try {
            TableUtils.createTableIfNotExists(Main.database.connectionSource, Event.class);
            loadEventsFromDatabase();
            updateActiveEvent(false);
            new Timer().scheduleAtFixedRate(new UpdateEventTask(), 0, 1000 * 60 * 60);
        } catch (SQLException e) {
            logger.error("Error while initializing Events feature", e);
        }
    }

    /**
     * Fetches all events from the database and puts them to a list.
     * @throws SQLException
     */
    private void loadEventsFromDatabase() throws SQLException {
        Dao<Event, String> eventDao = DaoManager.createDao(Main.database.connectionSource, Event.class);

        for (Event event: eventDao.queryForAll()) {
            events.add(event);
            logger.info(event.getActivityMessage() + " event loaded");
        }
    }

    /**
     * Checks if there are any event today and sets them as activity.
     * @param announce if true, the event will be announced to the announce channel
     */
    private void updateActiveEvent(boolean announce)
    {
        LocalDateTime localDateTime = java.time.LocalDateTime.now();

        // Year and month can be null, this way you can make recurring events.
        for (Event event: events) {
            if (event.getYear() == null || event.getYear() == localDateTime.getYear())
            {
                if (event.getMonth() == null || event.getMonth() == localDateTime.getMonthValue())
                {
                    if (event.getDay() == localDateTime.getDayOfMonth())
                    {
                        Main.JDA.getPresence().setActivity(
                                Activity.watching(event.getActivityMessage())
                        );

                        if (announce) {
                            BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                            embedBuilder.setTitle("Today's event");
                            embedBuilder.setDescription(event.getAnnounceMessage());

                            Objects.requireNonNull(Main.JDA
                                    .getTextChannelById(eventsConfig.getAnnounceChannelId()))
                                    .sendMessageEmbeds(embedBuilder.build())
                                    .queue();
                        }
                    }
                }
            }
        }
    }

    /**
     * TimerTask to update periodically.
     */
    public class UpdateEventTask extends TimerTask {
        @Override
        public void run() {
            updateActiveEvent(eventsConfig.getAnnounceAtHour() == LocalDateTime.now().getHour());
            logger.debug("Event updated.");
        }
    }
}
