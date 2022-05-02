/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.GoldConfig;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

/**
 * Simple quote manager feature.
 *
 * If you add a reaction to a message, this feature will save it to the database, then you can later get these. This is
 * an old Rem feature (back from IRC times). It was used as a simple bash.org alternative for us.
 *
 * Configuration: emoji/emote and which emoji/emote to trigger
 */
public class Gold extends BaseFeature {
    GoldConfig goldConfig;

    public Gold() throws IOException {
        goldConfig = ConfigManager.load(GoldConfig.class);
    }

    @Override
    public void onJDAInit() {
        try {
            TableUtils.createTableIfNotExists(Main.database.connectionSource, hu.pvga.rem4.Models.Gold.class);
        } catch (SQLException e) {
            logger.error("Could not create table for Gold");
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "gold")) {
            showRandomGold(event);
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getReactionEmote().isEmote() && goldConfig.getEmojiOrEmote().equals("emote")) {
            if (event.getReactionEmote().getEmote().getName().equals(goldConfig.getEmote())) {
                addGold(event);
                logger.info("Adding gold by emote to " + event.getMessageId());
            }
        } else if (event.getReactionEmote().isEmoji() && goldConfig.getEmojiOrEmote().equals("emoji")) {
            if (event.getReactionEmote().getEmoji().equals(goldConfig.getEmoji())) {
                addGold(event);
                logger.info("Adding gold by emoji to " + event.getMessageId());
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (event.getReactionEmote().isEmote() && goldConfig.getEmojiOrEmote().equals("emote")) {
            if (event.getReactionEmote().getEmote().getName().equals(goldConfig.getEmote())) {
                removeGold(event);
                logger.info("Removing gold by emote to " + event.getMessageId());
            }
        } else if (event.getReactionEmote().isEmoji() && goldConfig.getEmojiOrEmote().equals("emoji")) {
            if (event.getReactionEmote().getEmoji().equals(goldConfig.getEmoji())) {
                removeGold(event);
                logger.info("Removing gold by emoji to " + event.getMessageId());
            }
        }
    }

    /**
     * Retrieves all Golds, chooses one randomly and then sends it back.
     * @param event
     */
    private void showRandomGold(MessageReceivedEvent event)
    {
        try {
            Dao<hu.pvga.rem4.Models.Gold, String> goldDao = DaoManager.createDao(Main.database.connectionSource, hu.pvga.rem4.Models.Gold.class);
            List<hu.pvga.rem4.Models.Gold> golds = goldDao.queryForAll();

            int randomGoldIndex = (new Random()).nextInt(golds.size());
            hu.pvga.rem4.Models.Gold gold = golds.get(randomGoldIndex);

            BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
            embedBuilder.setTitle(Main.localization.get("gold_random_gold"));
            embedBuilder.setDescription(gold.getMessage());
            embedBuilder.addField(
                    Main.localization.get("gold_author"),
                    gold.getUserName(),
                    true
            );
            embedBuilder.addField(
                    Main.localization.get("gold_created_at"),
                    gold.getSentAt()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime().toString(),
                    true
            );
            embedBuilder.addField(
                    Main.localization.get("gold_saved_by"),
                    gold.getGoldByUserName(),
                    true
            );
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the message to the database.
     * @param event
     */
    private void addGold(MessageReactionAddEvent event)
    {
        try {
            Message message = event.retrieveMessage().complete();
            Dao<hu.pvga.rem4.Models.Gold, String> goldDao = DaoManager.createDao(Main.database.connectionSource, hu.pvga.rem4.Models.Gold.class);

            Optional<hu.pvga.rem4.Models.Gold> goldElement = goldDao.queryForFieldValues(new HashMap<String, Object>() {{
                put("messageId", message.getId());
            }}).stream().findFirst();

            if (goldElement.isPresent()) {
                // already saved
                return;
            }

            hu.pvga.rem4.Models.Gold gold = new hu.pvga.rem4.Models.Gold();
            gold.setMessageId(message.getId());
            gold.setMessage(message.getContentDisplay());
            gold.setUserId(message.getAuthor().getId());
            gold.setUserName(message.getAuthor().getName());
            gold.setSentAt(Date.from(message.getTimeCreated().toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
            gold.setGoldByUserId(Objects.requireNonNull(event.getUser()).getId());
            gold.setGoldByUserName(event.getUser().getName());

            goldDao.create(gold);

            event.getChannel().sendMessage(Main.localization.get("gold_message_saved")).queue();
        } catch (SQLException e) {
            logger.error("Could not save gold.", e);
        }
    }

    /**
     * Removes a saved message from the database.
     * @param event
     */
    private void removeGold(MessageReactionRemoveEvent event)
    {
        try {
            Message message = event.retrieveMessage().complete();
            Dao<hu.pvga.rem4.Models.Gold, String> goldDao = DaoManager.createDao(Main.database.connectionSource, hu.pvga.rem4.Models.Gold.class);

            Optional<hu.pvga.rem4.Models.Gold> goldElement = goldDao.queryForFieldValues(new HashMap<String, Object>() {{
                put("messageId", message.getId());
            }}).stream().findFirst();

            if (goldElement.isPresent())
            {
                goldDao.delete(goldElement.get());
            }

            event.getChannel().sendMessage(Main.localization.get("gold_message_removed")).queue();
        } catch (SQLException e) {
            logger.error("Could not remove gold.", e);
        }
    }
}
