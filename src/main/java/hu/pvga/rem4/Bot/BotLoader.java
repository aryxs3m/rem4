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
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

/**
 * BotLoader
 *
 * This class initializes JDA and the enabled Features.
 */
public class BotLoader {
    private static JDA JDA;
    private final Logger logger = LoggerFactory.getLogger(BotLoader.class);
    private final List<FeatureInterface> features = new ArrayList<>();

    public JDA boot() throws LoginException {


        List<GatewayIntent> gatewayIntentList = new ArrayList<>();
        for (String intentValue: Main.systemConfig.getIntents()) {
            gatewayIntentList.add(GatewayIntent.valueOf(intentValue));
            logger.info("Enabling " + intentValue + " intent.");
        }

        JDABuilder builder = JDABuilder
                .createDefault(Main.systemConfig.getDiscordToken())
                .enableIntents(gatewayIntentList);
        builder.setActivity(
                Activity.playing("Rem v4")
        );

        builder.addEventListeners(new BotSystemHandler());
        loadFeatures(builder);

        BotLoader.JDA = builder.build();

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

    public void initFeatures()
    {
        for (FeatureInterface feature: features) {
            feature.onJDAInit();
        }
    }
}
