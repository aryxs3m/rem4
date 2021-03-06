/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.UptimeRobotConfig;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

/**
 * UptimeRobot Feature
 *
 * Shows your UptimeRobot monitors all time availability.
 *
 * Configuration: UptimeRobot api key
 */
public class UptimeRobot extends BaseFeature {
    private final UptimeRobotConfig uptimeRobotConfig;

    public UptimeRobot() throws IOException {
        uptimeRobotConfig = ConfigManager.load(UptimeRobotConfig.class);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "upbot")) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("api_key", uptimeRobotConfig.getApiKey())
                        .add("format", "json")
                        .add("all_time_uptime_ratio", "1")
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.uptimerobot.com/v2/getMonitors")
                        .post(formBody)
                        .build();

                Call call = client.newCall(request);
                Response response = call.execute();

                if (response.code() == 429) {
                    event.getChannel().sendMessage(Main.localization.get("error_api_rate_limit_exceeded")).queue();
                    logger.warn("UptimeRobot API rate limit exceeded.");
                    return;
                }

                if (response.code() != 200) {
                    event.getChannel().sendMessage(Main.localization.get("error_api_responded_with") + " " + response.code()).queue();
                    logger.warn("UptimeRobot API responded with " + response.code());
                    return;
                }

                JSONObject responseJSON = new JSONObject(response.body().string());

                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle(Main.localization.get("monitor_monitor_uptime"));
                embedBuilder.setDescription(Main.localization.get("monitor_all_time"));
                embedBuilder.setAuthor(
                        "UptimeRobot",
                        "https://uptimerobot.com/",
                        "https://play-lh.googleusercontent.com/cUrv0t00FYQ1GKLuOTvv8qjo1lSDjqZC16IOp3Fb6ijew6Br5m4o16HhDp0GBu_Bw8Y"
                );

                for (Object monitor: responseJSON.getJSONArray("monitors")) {
                    if (monitor instanceof JSONObject) {
                        JSONObject monitorJSON = (JSONObject) monitor;
                        String status = "\uD83D\uDFE2";
                        if (monitorJSON.getInt("status") >= 8) {
                            status = "\uD83D\uDC80";
                        }

                        embedBuilder.addField(new MessageEmbed.Field(
                                status + " " + monitorJSON.getString("friendly_name"),
                                monitorJSON.getString("all_time_uptime_ratio") + "%",
                                true
                        ));
                    }
                }

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            } catch (IOException e) {
                event.getChannel().sendMessage(Main.localization.get("error_cannot_reach_api")).queue();
                logger.error("Cannot reach UptimeRobot API.", e);
            }
        }
    }
}
