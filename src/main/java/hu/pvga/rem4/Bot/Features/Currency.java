/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Exceptions.RequiredParameterException;
import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.CurrencyConfig;
import hu.pvga.rem4.Config.UptimeRobotConfig;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class Currency extends BaseFeature {
    private final CurrencyConfig currencyConfig;

    public Currency() throws IOException {
        currencyConfig = ConfigManager.load(CurrencyConfig.class);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "currency")) {
            try {
                String[] args = getCommandParameters(event, 2);
                Response response;

                String argValue = args[0];
                String argBaseCurrency = args[1];
                String argCurrency = currencyConfig.getDefaultCurrency();

                // value + basecurrency (e.g. 1 EUR)
                if (args.length == 4) {
                    argCurrency = args[3];
                }

                response = convert(argBaseCurrency, argCurrency);

                if (response.code() == 429) {
                    event.getChannel().sendMessage("CurrencyAPI rate limit exceeded.").queue();
                    logger.warn("CurrencyAPI rate limit exceeded.");
                    return;
                }

                if (response.code() != 200) {
                    event.getChannel().sendMessage("CurrencyAPI responded with " + response.code()).queue();
                    logger.warn("CurrencyAPI responded with " + response.code());
                    return;
                }

                JSONObject responseJSON = new JSONObject(Objects.requireNonNull(response.body()).string());

                float change = responseJSON
                        .getJSONObject("data")
                        .getJSONObject(argCurrency)
                        .getFloat("value");

                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle(argValue.concat(" ").concat(argBaseCurrency));
                embedBuilder.setDescription((Float.parseFloat(argValue) * change) + " " + argCurrency);
                embedBuilder.setAuthor(
                        "Currencyapi.com",
                        "https://currencyapi.com",
                        "https://currencyapi.com/img/favicon/favicon-16x16.png"
                );

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            } catch (IOException e) {
                event.getChannel().sendMessage("Cannot reach CurrencyAPI.").queue();
                logger.error("Cannot reach CurrencyAPI", e);
            } catch (RequiredParameterException e) {
                event.getChannel().sendMessage("Missing parameters. Examples: `1 EUR` or `1 EUR in USD`.").queue();
            }
        }
    }

    private Response convert(String baseCurrency, String currency) throws IOException {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder =
                Objects.requireNonNull(HttpUrl.parse("https://api.currencyapi.com/v3/latest")).newBuilder();
        urlBuilder.addQueryParameter("apikey", currencyConfig.getApiKey());
        urlBuilder.addQueryParameter("base_currency", baseCurrency);
        urlBuilder.addQueryParameter("currencies", currency);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        return call.execute();
    }
}
