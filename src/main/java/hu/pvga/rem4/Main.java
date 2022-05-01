/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4;

import hu.pvga.rem4.Bot.BotLoader;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.SystemConfig;
import hu.pvga.rem4.WebApi.WebApiLoader;

public class Main {
    public static SystemConfig systemConfig;
    public static net.dv8tion.jda.api.JDA JDA;
    public static Database database;

    public static void main(String[] args) throws Exception {
        Main.systemConfig = ConfigManager.load(SystemConfig.class);

        database = new Database(Main.systemConfig.getDatabaseJDBC());

        BotLoader botLoader = new BotLoader();
        Main.JDA = botLoader.boot();
        botLoader.initFeatures();

        WebApiLoader webApiLoader = new WebApiLoader();
        webApiLoader.boot();
    }
}