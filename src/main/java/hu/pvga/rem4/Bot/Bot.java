/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot;

import hu.pvga.rem4.Bot.Features.FeatureInterface;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Bot extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(Bot.class);
    HashMap<String, FeatureInterface> commands = new HashMap<>();

    private static final String COMMAND_PREFIX = "/";

    public Bot()
    {
        // TODO
    }
}
