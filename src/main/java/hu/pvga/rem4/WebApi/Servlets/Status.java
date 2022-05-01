/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.WebApi.Servlets;

import org.json.JSONObject;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Status implements Take {
    @Override
    public Response act(final Request req) {
        JSONObject response = new JSONObject();
        response.put("version", getVersion());

        JSONObject memoryObject = new JSONObject();
        memoryObject.put("total", Runtime.getRuntime().totalMemory());
        memoryObject.put("free", Runtime.getRuntime().freeMemory());
        response.put("memory", memoryObject);

        return new RsText(response.toString());
    }

    private String getVersion()
    {
        try (InputStream input = Status.class.getClassLoader().getResourceAsStream("version.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            return prop.getProperty("version");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "UNKNOWN";
    }
}
