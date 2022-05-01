/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * FeatureSet
 *
 * Small utilities to speed up Feature creation
 */
public abstract class FeatureSet {

    /**
     * Executes the given shell command (with parameters), returns with the stdout
     * @param commands command to run with parameters
     * @return execution stdout
     * @throws IOException
     */
    public static String getCLI(String commands) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        String errorMessage;
        if ((errorMessage = stdError.readLine()) != null)
        {
            throw new Exception(errorMessage);
        }

        return stdInput.readLine();
    }

    /**
     * Quick HTTP GET feature. Returns the response body as string.
     * @param urlString URL to fetch
     * @return response body
     * @throws IOException
     */
    public static String getHTTP(String urlString) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux x86_64) Rem Discord v4");
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }

        return result.toString();
    }
}
