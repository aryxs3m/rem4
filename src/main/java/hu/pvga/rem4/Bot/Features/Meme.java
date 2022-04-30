/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.MemeConfig;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class Meme extends BaseFeature {
    private final MemeConfig memeConfig;
    private long lastSent = 0;

    public Meme() throws IOException {
        memeConfig = ConfigManager.load(MemeConfig.class);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if ((System.nanoTime() - lastSent) / 1000000000 <= memeConfig.getTimeoutSeconds()) {
            return;
        }

        try {
            Response response = null;

            if (event.getMessage().getReferencedMessage() != null &&
                    event.getMessage().getContentDisplay().toLowerCase(Locale.ROOT).equals("always has been")) {
                response = makeAlwaysHasBeen(event);
            }

            if (response != null) {
                JSONObject responseJSON = new JSONObject(Objects.requireNonNull(response.body()).string());
                if (responseJSON.getBoolean("success")) {
                    BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                    embedBuilder.setAuthor(
                            "imgflip",
                            "https://imgflip.com/",
                            "https://imgflip.com/apple-touch-icon.png?c"
                    );
                    embedBuilder.setImage(responseJSON.getJSONObject("data").getString("url"));

                    event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lastSent = System.nanoTime();
    }

    public Response makeAlwaysHasBeen(MessageReceivedEvent event) throws IOException {
        return makeMeme(
                "252600902",
                Objects.requireNonNull(event.getMessage().getReferencedMessage()).getContentDisplay(),
                "Always has been."
        );
    }

    private Response makeMeme(String templateId, String text0, String text1) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("username", memeConfig.getUsername())
                .add("password", memeConfig.getPassword())
                .add("template_id", templateId)
                .add("text0", text0)
                .add("text1", text1)
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgflip.com/caption_image")
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        return call.execute();
    }
}
