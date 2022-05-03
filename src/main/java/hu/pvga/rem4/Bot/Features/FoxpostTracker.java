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
import hu.pvga.rem4.Config.FoxpostTrackerConfig;
import hu.pvga.rem4.Main;
import hu.pvga.rem4.Models.Foxpost;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

/**
 * Foxpost Tracker Feature
 *
 * Simple Foxpost tracker that updates frequently and messages you when status changes.
 *
 * Configuration: refresh interval
 */
public class FoxpostTracker extends BaseFeature {
    private final FoxpostTrackerConfig foxpostTrackerConfig;

    public FoxpostTracker() throws SQLException, IOException {
        foxpostTrackerConfig = ConfigManager.load(FoxpostTrackerConfig.class);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "foxpost-remove")) {
            try {
                String[] args = getCommandParameters(event, 1);

                Dao<Foxpost, String> foxpostDao = DaoManager.createDao(Main.database.connectionSource, Foxpost.class);

                List<Foxpost> oldItems = foxpostDao.queryForEq("trackingNumber", args[0]);
                if (oldItems.size() > 0) {
                    Foxpost oldItem = oldItems.get(0);
                    foxpostDao.delete(oldItem);

                    BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                    embedBuilder.setTitle(Main.localization.get("foxpost_deleted"));
                    embedBuilder.setDescription(
                            MessageFormat.format(
                                    Main.localization.get("foxpost_deleted_item"),
                                    args[0]
                            )
                    );
                    embedBuilder.setAuthor(
                            "Foxpost",
                            "https://www.foxpost.hu/",
                            "https://www.foxpost.hu/favicon-32x32.png"
                    );

                    event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }
            } catch (SQLException e) {
                event.getChannel().sendMessage(
                        MessageFormat.format(
                                Main.localization.get("error_sql"),
                                e.getMessage()
                        )
                ).queue();
                logger.error("Could not save event.", e);
            } catch (RequiredParameterException e) {
                event.getChannel().sendMessage(Main.localization.get("foxpost_delete_parameters")).queue();
            }

            return;
        }

        if (isCommand(event, "foxpost")) {
            try {
                String[] args = getCommandParameters(event, 0);

                if (args.length == 0) {
                    showAllStatus(event);
                } else {
                    addNewOrShowExistingItem(event, args[0]);
                }
            } catch (RequiredParameterException e) {
                // no required parameters
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onJDAInit() {
        try {
            TableUtils.createTableIfNotExists(Main.database.connectionSource, Foxpost.class);
            new Timer().scheduleAtFixedRate(new UpdateFoxpostTask(), 0,
                    1000L * 60 * foxpostTrackerConfig.getRefreshIntervalMinutes());
        } catch (SQLException e) {
            logger.error("Error while initializing Foxpost feature", e);
        }
    }

    /**
     * Show all tracked items
     * @throws SQLException
     */
    private void showAllStatus(MessageReceivedEvent event) throws SQLException {
        Dao<Foxpost, String> foxpostDao = DaoManager.createDao(Main.database.connectionSource, Foxpost.class);

        BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
        embedBuilder.setTitle(Main.localization.get("foxpost_items"));
        embedBuilder.setDescription(Main.localization.get("foxpost_tracked_items"));
        embedBuilder.setAuthor(
                "Foxpost",
                "https://www.foxpost.hu/",
                "https://www.foxpost.hu/favicon-32x32.png"
        );

        for (Foxpost foxpost: foxpostDao.queryForAll()) {
            embedBuilder.addField(
                    foxpost.getTrackingNumber(),
                    foxpost.getStatus(),
                    false
            );
        }

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Adds a new item by its tracking number. If it exists, just shows its current status.
     * @throws SQLException
     */
    private void addNewOrShowExistingItem(MessageReceivedEvent event, String trackingNumber) throws SQLException {
        Dao<Foxpost, String> foxpostDao = DaoManager.createDao(Main.database.connectionSource, Foxpost.class);

        List<Foxpost> oldItems = foxpostDao.queryForEq("trackingNumber", trackingNumber);
        if (oldItems.size() > 0) {
            notifyCurrentStatus(oldItems.get(0));
            return;
        }

        Foxpost foxpostItem = new Foxpost();
        foxpostItem.setChannelId(event.getChannel().getId());
        foxpostItem.setTrackingNumber(trackingNumber);
        foxpostItem.setUserId(event.getAuthor().getId());
        trackItem(foxpostItem);

        foxpostDao.create(foxpostItem);

        BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
        embedBuilder.setTitle(Main.localization.get("foxpost_new_created"));
        embedBuilder.setDescription(Main.localization.get("foxpost_will_notify"));
        embedBuilder.setAuthor(
                "Foxpost",
                "https://www.foxpost.hu/csomagkovetes/?code=" + foxpostItem.getTrackingNumber(),
                "https://www.foxpost.hu/favicon-32x32.png"
        );
        embedBuilder.addField(
                Main.localization.get("foxpost_status"),
                foxpostItem.getStatus(),
                true
        );
        embedBuilder.addField(
                Main.localization.get("foxpost_tracking_number"),
                foxpostItem.getTrackingNumber(),
                true
        );
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Tracks an item and updates it status and status descrption.
     * @param foxpost Item to track
     */
    private void trackItem(Foxpost foxpost)
    {
        try {
            Document doc = Jsoup.connect("https://www.foxpost.hu/csomagkovetes/?code=" + foxpost.getTrackingNumber())
                    .userAgent("Mozilla/5.0 (Linux x86_64) Rem Discord v4")
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip")
                    .header("Connection", "Keep-Alive")
                    .header("Accept-Language", "hu-HU,hu;q=0.9")
                    .header("Sec-Fetch-Dest", "document")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Cache-Control", "max-age=0")
                    .header("Content-Type", "")
                    .timeout(5000)
                    .get();
            doc.charset(StandardCharsets.UTF_8);

            Element lastStatus = doc.getElementsByClass("parcel-status-items__list-item-title").first();
            Element lastStatusDesc = doc.getElementsByClass("parcel-status-items__list-item-description").first();

            if (lastStatus != null && lastStatusDesc != null) {
                foxpost.setStatus(lastStatus.text());
                foxpost.setStatusDescription(lastStatusDesc.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a notification about the tracking status change.
     * @param foxpost
     */
    private void notifyChangedStatus(Foxpost foxpost)
    {
        TextChannel channel = getJDA().getTextChannelById(foxpost.getChannelId());

        if (channel != null) {
            BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
            embedBuilder.setTitle(Main.localization.get("foxpost_status_change"));
            embedBuilder.setDescription("**" + foxpost.getStatus() + "**\n" + foxpost.getStatusDescription());
            embedBuilder.setAuthor(
                    "Foxpost",
                    "https://www.foxpost.hu/csomagkovetes/?code=" + foxpost.getTrackingNumber(),
                    "https://www.foxpost.hu/favicon-32x32.png"
            );
            embedBuilder.addField(
                    Main.localization.get("foxpost_tracking_number"),
                    foxpost.getTrackingNumber(),
                    true
            );
            channel.sendMessageEmbeds(embedBuilder.build()).mentionUsers(foxpost.getUserId()).queue();
        }
    }

    /**
     * Sends a notification about the current status
     * @param foxpost
     */
    private void notifyCurrentStatus(Foxpost foxpost)
    {
        TextChannel channel = getJDA().getTextChannelById(foxpost.getChannelId());

        if (channel != null) {
            BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
            embedBuilder.setTitle(Main.localization.get("foxpost_status"));
            embedBuilder.setDescription("**" + foxpost.getStatus() + "**\n" + foxpost.getStatusDescription());
            embedBuilder.setAuthor(
                    "Foxpost",
                    "https://www.foxpost.hu/csomagkovetes/?code=" + foxpost.getTrackingNumber(),
                    "https://www.foxpost.hu/favicon-32x32.png"
            );
            embedBuilder.addField(
                    Main.localization.get("foxpost_tracking_number"),
                    foxpost.getTrackingNumber(),
                    true
            );
            channel.sendMessageEmbeds(embedBuilder.build()).mentionUsers(foxpost.getUserId()).queue();
        }
    }

    /**
     * TimerTask to update periodically.
     */
    public class UpdateFoxpostTask extends TimerTask {
        @Override
        public void run() {
            try {
                Dao<Foxpost, String> foxpostDao = DaoManager.createDao(Main.database.connectionSource, Foxpost.class);

                for (Foxpost foxpost: foxpostDao.queryForAll()) {
                    String lastStatus = foxpost.getStatus();
                    trackItem(foxpost);

                    if (!lastStatus.equals(foxpost.getStatus())) {
                        foxpostDao.update(foxpost);
                        notifyChangedStatus(foxpost);
                    }

                    // If the item was picked up, we no longer need to track it
                    if (foxpost.getStatus().equals("Átvéve")) {
                        foxpostDao.delete(foxpost);
                    }
                }

                logger.debug("Foxpost updated.");
            } catch (SQLException e) {
                logger.error("Foxpost update failed", e);
            }
        }
    }
}
