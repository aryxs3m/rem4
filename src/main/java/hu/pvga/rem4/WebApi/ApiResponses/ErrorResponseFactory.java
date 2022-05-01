/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.WebApi.ApiResponses;

import org.json.JSONObject;
import org.takes.rs.RsText;

public abstract class ErrorResponseFactory {
    public static RsText makeFromString(String message)
    {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("status", "error");
        errorResponse.put("type", "message");
        errorResponse.put("message", message);
        return new RsText(errorResponse.toString());
    }

    public static RsText makeFromException(Exception exception)
    {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("status", "error");
        errorResponse.put("type", "exception");

        JSONObject exceptionObject = new JSONObject();
        exceptionObject.put("message", exception.getMessage());
        exceptionObject.put("localizedMessage", exception.getLocalizedMessage());
        exceptionObject.put("class", exception.getClass().getName());

        errorResponse.put("exception", exceptionObject);
        return new RsText(errorResponse.toString());
    }
}
