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
import hu.pvga.rem4.Main;
import hu.pvga.rem4.Models.WordCount;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Simple word counter feature
 *
 * Admins can manage which words to count. Users can check the statistics.
 */
public class WordCounter extends BaseFeature {
    HashMap<Integer, String> wordCounts = new HashMap<>();

    @Override
    public void onJDAInit() {
        try {
            TableUtils.createTableIfNotExists(Main.database.connectionSource, WordCount.class);
            loadWordCountsFromDatabase();
        } catch (SQLException e) {
            logger.error("Could not create table for WordCount", e);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "wc-add")) {
            if (isFromAdmin(event)) {
                try {
                    String[] args = getCommandParameters(event, 1);

                    Dao<WordCount, String> wordCountDao = DaoManager.createDao(Main.database.connectionSource, WordCount.class);
                    WordCount wordCount = new WordCount();
                    wordCount.setWord(args[0]);
                    wordCount.setSince(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                    wordCountDao.create(wordCount);

                    wordCounts.put(wordCount.getId(), args[0]);
                } catch (RequiredParameterException e) {
                    event.getChannel().sendMessage("Required one parameter: word").queue();
                } catch (SQLException e) {
                    event.getChannel().sendMessage("SQL exception happened.").queue();
                    this.logger.error("SQL exception while WordCount create", e);
                }
            } else {
                event.getChannel().sendMessage("You are not an administrator.").queue();
            }
            return;
        }

        if (isCommand(event, "wc-remove")) {
            if (isFromAdmin(event)) {
                try {
                    String[] args = getCommandParameters(event, 1);

                    Dao<WordCount, String> wordCountDao = DaoManager.createDao(Main.database.connectionSource, WordCount.class);
                    WordCount wordCount = wordCountDao.queryForId(args[0]);
                    wordCountDao.delete(wordCount);

                    wordCounts.remove(Integer.parseInt(args[0]));
                } catch (RequiredParameterException e) {
                    event.getChannel().sendMessage("Required one parameter: id").queue();
                } catch (SQLException e) {
                    event.getChannel().sendMessage("SQL exception happened.").queue();
                    this.logger.error("SQL exception while WordCount delete", e);
                }
            } else {
                event.getChannel().sendMessage("You are not an administrator.").queue();
            }
            return;
        }

        if (isCommand(event, "wc-list")) {
            if (isFromAdmin(event)) {
                try {
                    StringBuilder wcList = new StringBuilder();
                    wcList.append("ID\tWORD\tCOUNT\n");
                    Dao<WordCount, String> wordCountDao = DaoManager.createDao(Main.database.connectionSource, WordCount.class);
                    for (WordCount wordCount: wordCountDao.queryForAll()) {
                        wcList.append(wordCount.getId());
                        wcList.append("\t");
                        wcList.append(wordCount.getWord());
                        wcList.append("\t");
                        wcList.append(wordCount.getCount());
                        wcList.append("\n");
                    }
                    event.getChannel().sendMessage("```" + wcList.toString() + "```").queue();
                } catch (SQLException e) {
                    event.getChannel().sendMessage("SQL exception happened.").queue();
                    this.logger.error("SQL exception while WordCount delete", e);
                }
            } else {
                event.getChannel().sendMessage("You are not an administrator.").queue();
            }
            return;
        }

        if (isCommand(event, "wc")) {
            showWordCounts(event);
            return;
        }

        if (event.getAuthor().isBot()) {
            // Don't count in messages sent by bots
            return;
        }

        for (Map.Entry<Integer, String> wordCountEntry: wordCounts.entrySet()) {
            if (event.getMessage().getContentDisplay().toLowerCase(Locale.ROOT)
                    .contains(wordCountEntry.getValue().toLowerCase(Locale.ROOT))) {
                try {
                    incrementWordCountById(wordCountEntry.getKey());
                } catch (SQLException e) {
                    logger.warn("Word count update failed", e);
                }
            }
        }
    }

    /**
     * Loads all wordcount entries from the database to a local hashmap
     * @throws SQLException
     */
    private void loadWordCountsFromDatabase() throws SQLException {
        Dao<WordCount, String> wordCountDao = DaoManager.createDao(Main.database.connectionSource, WordCount.class);
        for (WordCount wordCount: wordCountDao.queryForAll()) {
            this.wordCounts.put(
                    wordCount.getId(),
                    wordCount.getWord()
            );
        }
        this.logger.info("Loaded " + this.wordCounts.size() + " word counts");
    }

    /**
     * Increments a wordcount database entry by its id
     * @param id
     * @throws SQLException
     */
    private void incrementWordCountById(int id) throws SQLException {
        Dao<WordCount, String> wordCountDao = DaoManager.createDao(Main.database.connectionSource, WordCount.class);
        WordCount wordCount = wordCountDao.queryForId(String.valueOf(id));
        wordCount.setCount(wordCount.getCount() + 1);
        wordCountDao.update(wordCount);
    }

    /**
     * Retrieves all wordcounts and shows them in messageembed
     * @param event
     */
    private void showWordCounts(MessageReceivedEvent event)
    {
        try {
            Dao<WordCount, String> wordCountDao = DaoManager.createDao(Main.database.connectionSource, WordCount.class);

            BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
            embedBuilder.setTitle("Word Counter");
            embedBuilder.setDescription("Statistics");

            for (WordCount wordCount: wordCountDao.queryForAll()) {
                embedBuilder.addField(
                        wordCount.getWord(),
                        String.valueOf(wordCount.getCount()),
                        true
                );
            }

            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
