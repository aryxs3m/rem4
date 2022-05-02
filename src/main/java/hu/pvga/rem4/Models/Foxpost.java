/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * FoxpostTracker Feature
 *
 * Foxpost Model - holds one foxpost item
 */
@DatabaseTable(tableName = "foxpost_trackings")
public class Foxpost {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String channelId;

    @DatabaseField
    private String userId;

    @DatabaseField
    private String trackingNumber;

    @DatabaseField
    private String status;

    @DatabaseField
    private String statusDescription;

    public Foxpost() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
