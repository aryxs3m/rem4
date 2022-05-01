/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.WebApi.Servlets;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import hu.pvga.rem4.Main;
import hu.pvga.rem4.Models.Event;
import hu.pvga.rem4.WebApi.ApiResponses.ErrorResponseFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;
import java.util.List;

import java.sql.SQLException;

public class Events implements Take {
    @Override
    public Response act(final Request req) {
        try {
            Dao<Event, String> eventDao = DaoManager.createDao(Main.database.connectionSource, Event.class);
            List<Event> events = eventDao.queryForAll();

            JSONObject responseObject = new JSONObject();
            JSONArray eventsArray = new JSONArray();

            for (Event event: events) {
                JSONObject eventObject = new JSONObject(event);
                eventsArray.put(eventObject);
            }

            responseObject.put("events", eventsArray);

            return new RsText(responseObject.toString());
        } catch (SQLException e) {
            return ErrorResponseFactory.makeFromException(e);
        }
    }
}
