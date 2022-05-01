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
import hu.pvga.rem4.Models.WordCount;
import hu.pvga.rem4.WebApi.ApiResponses.ErrorResponseFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;

import java.sql.SQLException;
import java.util.List;

public class WordCounts implements Take {
    @Override
    public Response act(final Request req) {
        try {
            Dao<WordCount, String> wordCountDao = DaoManager.createDao(Main.database.connectionSource, WordCount.class);
            List<WordCount> wordCounts = wordCountDao.queryForAll();

            JSONObject responseObject = new JSONObject();
            JSONArray wordCountsArray = new JSONArray();

            for (WordCount wordCount: wordCounts) {
                JSONObject eventObject = new JSONObject(wordCount);
                wordCountsArray.put(eventObject);
            }

            responseObject.put("wordcounts", wordCountsArray);

            return new RsText(responseObject.toString());
        } catch (SQLException e) {
            return ErrorResponseFactory.makeFromException(e);
        }
    }
}
