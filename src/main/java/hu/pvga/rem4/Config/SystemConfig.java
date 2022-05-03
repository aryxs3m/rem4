/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Config;

import java.util.List;

/**
 * System Configuration
 *
 * This config file is loaded at start to a static variable and can be accessible from anywhere.
 */
public class SystemConfig extends BaseConfig {
    private String discordToken;
    private String botPrefix;
    private String botName;
    private String botIcon;

    private List<String> intents;

    private String language;
    private String country;

    private String databaseJDBC;

    private List<String> enabledFeatures;

    private List<String> adminUsers;

    public String getDiscordToken() {
        return discordToken;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getBotIcon() {
        return botIcon;
    }

    public String getDatabaseJDBC() {
        return databaseJDBC;
    }

    public void setDatabaseJDBC(String databaseJDBC) {
        this.databaseJDBC = databaseJDBC;
    }

    public void setBotIcon(String botIcon) {
        this.botIcon = botIcon;
    }

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
    }

    public List<String> getEnabledFeatures() {
        return enabledFeatures;
    }

    public void setEnabledFeatures(List<String> enabledFeatures) {
        this.enabledFeatures = enabledFeatures;
    }

    public List<String> getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(List<String> adminUsers) {
        this.adminUsers = adminUsers;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getIntents() {
        return intents;
    }

    public void setIntents(List<String> intents) {
        this.intents = intents;
    }

    public String getBotPrefix() {
        return botPrefix;
    }

    public void setBotPrefix(String botPrefix) {
        this.botPrefix = botPrefix;
    }
}
