/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.WebApi;

import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.WebApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WebApiLoader {
    Logger logger = LoggerFactory.getLogger(WebApiLoader.class);
    WebApiConfig webApiConfig;
    WebApiServer serverThread;

    public WebApiLoader() throws IOException {
        this.webApiConfig = ConfigManager.load(WebApiConfig.class);
    }

    public void boot()
    {
        if (this.webApiConfig.isApiEnabled())
        {
            serverThread = new WebApiServer(this.webApiConfig);
            serverThread.start();
            logger.info("Web API server started");
        } else {
            logger.info("Web API server disabled");
        }
    }
}
