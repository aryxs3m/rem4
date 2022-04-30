/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot;

import hu.pvga.rem4.Bot.Features.FeatureInterface;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class BotLoader {
    private static JDA JDA;
    private final Logger logger = LoggerFactory.getLogger(BotLoader.class);
    private final List<FeatureInterface> features = new ArrayList<>();

    public JDA boot() throws LoginException {
        /*List<GatewayIntent> gatewayIntentList = new ArrayList<>();
        gatewayIntentList.add(GatewayIntent.GUILD_MEMBERS);
        gatewayIntentList.add(GatewayIntent.GUILD_MESSAGES);
        gatewayIntentList.add(GatewayIntent.DIRECT_MESSAGES);
        gatewayIntentList.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);*/

        JDABuilder builder = JDABuilder
                .createDefault(Main.systemConfig.getDiscordToken())
                /*.enableIntents(gatewayIntentList)*/;
        builder.setActivity(
                Activity.playing("Rem v4")
        );

        loadFeatures(builder);

        BotLoader.JDA = builder.build();
        initSlashCommands();

        return BotLoader.JDA;
    }

    private void loadFeatures(JDABuilder builder) {
        for (String featureClassName: Main.systemConfig.getEnabledFeatures()) {
            try {
                Object instance = Class.forName("hu.pvga.rem4.Bot.Features.".concat(featureClassName)).newInstance();

                if (instance instanceof FeatureInterface) {
                    builder.addEventListeners(instance);
                    features.add((FeatureInterface) instance);
                    logger.info(featureClassName + " loaded");
                } else {
                    logger.error(featureClassName + " is not a feature class.");
                    System.exit(1);
                }
            } catch (ClassNotFoundException e) {
                logger.error(featureClassName + " not found.", e);
                System.exit(1);
            } catch (InstantiationException e) {
                logger.error(featureClassName + " instantiation failed.", e);
                System.exit(1);
            } catch (IllegalAccessException e) {
                logger.error("Illegal Access Exception", e);
                System.exit(1);
            }
        }
    }

    private void initSlashCommands()
    {
        for (FeatureInterface feature: features) {
            feature.initSlashCommands();
        }
    }
}
