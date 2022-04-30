/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Config;

public class EventsConfig extends BaseConfig {
    private String announceChannelId;
    private int announceAtHour;

    public String getAnnounceChannelId() {
        return announceChannelId;
    }

    public void setAnnounceChannelId(String announceChannelId) {
        this.announceChannelId = announceChannelId;
    }

    public int getAnnounceAtHour() {
        return announceAtHour;
    }

    public void setAnnounceAtHour(int announceAtHour) {
        this.announceAtHour = announceAtHour;
    }
}
