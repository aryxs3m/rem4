/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Exceptions.RequiredParameterException;
import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.WeatherConfig;
import hu.pvga.rem4.FeatureSet;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Weather Command
 *
 * Returns the weather from OpenWeatherMap.
 *
 * Configuration: OpenWeatherMap API key, units, lang
 */
public class Weather extends BaseFeature {
    private final WeatherConfig weatherConfig;

    public Weather() throws IOException {
        weatherConfig = ConfigManager.load(WeatherConfig.class);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "weather")) {
            try {
                String[] params = getCommandParameters(event, 1);

                JSONObject response = new JSONObject(
                        FeatureSet.getHTTP(
                                "http://api.openweathermap.org/data/2.5/weather?q="+params[0]
                                        +"&APPID="+this.weatherConfig.getOpenWeatherAppId()
                                        +"&units="+weatherConfig.getUnits()
                                        +"&lang="+weatherConfig.getLang()
                        )
                );

                JSONObject weatherObject = response.getJSONArray("weather").getJSONObject(0);
                JSONObject mainObject = response.getJSONObject("main");
                JSONObject windObject = response.getJSONObject("wind");

                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle(
                        MessageFormat.format(
                                Main.localization.get("weather_in"),
                                response.getString("name")
                        )
                );
                embedBuilder.setDescription(weatherObject.getString("description"));
                embedBuilder.setAuthor(
                        "OpenWeatherMap",
                        "https://openweathermap.org/",
                        "https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/icons/logo_32x32.png"
                );
                embedBuilder.setImage("http://openweathermap.org/img/wn/"+weatherObject.getString("icon")+"@2x.png");
                embedBuilder.addField(new MessageEmbed.Field(
                        Main.localization.get("weather_temp"),
                        mainObject.getDouble("temp") + " °C",
                        true
                ));
                embedBuilder.addField(new MessageEmbed.Field(
                        Main.localization.get("weather_feels_like"),
                        mainObject.getDouble("feels_like") + " °C",
                        true
                ));
                embedBuilder.addField(new MessageEmbed.Field(
                        Main.localization.get("weather_humidity"),
                        mainObject.getInt("humidity") + "%",
                        true
                ));
                embedBuilder.addField(new MessageEmbed.Field(
                        Main.localization.get("weather_visibility"),
                        response.getInt("visibility") + " m",
                        true
                ));
                embedBuilder.addField(new MessageEmbed.Field(
                        Main.localization.get("weather_wind_speed"),
                        windObject.getDouble("speed") + " m/s",
                        true
                ));

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            } catch (RequiredParameterException e) {
                event.getChannel().sendMessage(Main.localization.get("weather_parameters")).queue();
            } catch (IOException e) {
                event.getChannel().sendMessage(Main.localization.get("error_cannot_reach_api")).queue();
                logger.error("Cannot reach OpenWeatherMap API.");
            }
        }
    }
}
