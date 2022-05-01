/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.WebApi;

import hu.pvga.rem4.Config.WebApiConfig;
import hu.pvga.rem4.WebApi.Servlets.Events;
import hu.pvga.rem4.WebApi.Servlets.Status;
import hu.pvga.rem4.WebApi.Servlets.WordCounts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.tk.TkWithType;

import java.io.IOException;

public class WebApiServer extends Thread {
    Logger logger = LoggerFactory.getLogger(WebApiServer.class);

    private final WebApiConfig webApiConfig;

    public WebApiServer(WebApiConfig webApiConfig)
    {
        this.webApiConfig = webApiConfig;
    }

    public void run() {
        try {
            new FtBasic(
                    new TkFork(
                            new FkRegex("/status", new TkWithType(new Status(), "application/json")),
                            new FkRegex("/events", new TkWithType(new Events(), "application/json")),
                            new FkRegex("/wordcounts", new TkWithType(new WordCounts(), "application/json"))
                    ), this.webApiConfig.getApiPort()
            ).start(Exit.NEVER);
        } catch (IOException e) {
            logger.error("Could not start web api server.", e);
        }
    }
}
