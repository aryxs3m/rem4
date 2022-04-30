/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4;

import hu.pvga.rem4.Bot.BotLoader;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.SystemConfig;
import net.dv8tion.jda.api.JDA;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {
    public static SystemConfig systemConfig;
    public static net.dv8tion.jda.api.JDA JDA;

    public static void main(String[] args) throws IOException, LoginException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Main.systemConfig = ConfigManager.load(SystemConfig.class);

        BotLoader botLoader = new BotLoader();
        Main.JDA = botLoader.boot();
    }
}