/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Gold Feature
 *
 * Gold model - holds one "gold" message
 */
@DatabaseTable(tableName = "golds")
public class Gold {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(index = true)
    private String messageId;

    @DatabaseField
    private String userId;

    @DatabaseField
    private String userName;

    @DatabaseField
    private String message;

    @DatabaseField
    private Date sentAt;

    @DatabaseField
    private String goldByUserId;

    @DatabaseField
    private String goldByUserName;

    public Gold() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getGoldByUserId() {
        return goldByUserId;
    }

    public void setGoldByUserId(String goldByUserId) {
        this.goldByUserId = goldByUserId;
    }

    public String getGoldByUserName() {
        return goldByUserName;
    }

    public void setGoldByUserName(String goldByUserName) {
        this.goldByUserName = goldByUserName;
    }
}
