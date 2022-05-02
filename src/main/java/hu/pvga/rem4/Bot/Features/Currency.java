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
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

/**
 * Currency Converter Feature
 *
 * This one uses the currencyapi.com API. It has a short and a long command:
 * <blockquote>currency 1 USD</blockquote> will convert 1 USD to the configured currency,
 * <blockquote>currency 1 USD in EUR</blockquote> will convert 1 USD to EUR.
 *
 * Configuration: API key, base currency
 */
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

                response = getExchangeRate(argBaseCurrency, argCurrency);

                if (response.code() == 429) {
                    event.getChannel().sendMessage(Main.localization.get("error_api_rate_limit_exceeded")).queue();
                    logger.warn("CurrencyAPI rate limit exceeded.");
                    return;
                }

                if (response.code() != 200) {
                    event.getChannel().sendMessage(Main.localization.get("error_api_responded_with") + " " + response.code()).queue();
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
                event.getChannel().sendMessage(Main.localization.get("error_cannot_reach_api")).queue();
                logger.error("Cannot reach CurrencyAPI", e);
            } catch (RequiredParameterException e) {
                event.getChannel().sendMessage(Main.localization.get("currency_parameters")).queue();
            }
        }
    }

    /**
     * Returns exchange rate between two currencies.
     * @param baseCurrency
     * @param currency
     * @return
     * @throws IOException
     */
    private Response getExchangeRate(String baseCurrency, String currency) throws IOException {
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
