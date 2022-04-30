/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Extends;

import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.EmbedBuilder;

public class BotEmbedBuilder extends EmbedBuilder {
    public BotEmbedBuilder() {
        setFooter(Main.systemConfig.getBotName(), Main.systemConfig.getBotIcon());
    }
}
