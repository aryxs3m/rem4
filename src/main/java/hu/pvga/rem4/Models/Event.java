/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Events Feature
 *
 * Event Model - holds one event
 */
@DatabaseTable(tableName = "events")
public class Event {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField()
    private Integer year;

    @DatabaseField()
    private Integer month;

    @DatabaseField()
    private Integer day;

    @DatabaseField()
    private String activityMessage;

    @DatabaseField()
    private String announceMessage;

    public Event() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getActivityMessage() {
        return activityMessage;
    }

    public void setActivityMessage(String activityMessage) {
        this.activityMessage = activityMessage;
    }

    public String getAnnounceMessage() {
        return announceMessage;
    }

    public void setAnnounceMessage(String announceMessage) {
        this.announceMessage = announceMessage;
    }
}
