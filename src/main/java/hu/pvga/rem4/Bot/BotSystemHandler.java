/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot;

import hu.pvga.rem4.Bot.Features.BaseFeature;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * BotSystemHandler
 *
 * Basic bot management.
 */
public class BotSystemHandler extends BaseFeature {
    @Override
    public void onStatusChange(@NotNull StatusChangeEvent event) {
        logger.info("Discord status changed: " + event.getNewStatus());

        if (event.getNewStatus() == JDA.Status.SHUTDOWN) {
            logger.error("Discord connection shutdown. Check error logs.");
            System.exit(1);
        }
    }
}
