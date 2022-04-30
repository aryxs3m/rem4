/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class Database {
    public ConnectionSource connectionSource;

    public Database(String jdbc) throws SQLException {
        connectionSource = new JdbcConnectionSource(jdbc);
    }

    public void close() throws Exception {
        connectionSource.close();
    }
}
