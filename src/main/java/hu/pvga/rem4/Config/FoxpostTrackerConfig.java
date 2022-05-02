/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Config;

public class FoxpostTrackerConfig extends BaseConfig {
    private int refreshIntervalMinutes;

    public int getRefreshIntervalMinutes() {
        return refreshIntervalMinutes;
    }

    public void setRefreshIntervalMinutes(int refreshIntervalMinutes) {
        this.refreshIntervalMinutes = refreshIntervalMinutes;
    }
}
