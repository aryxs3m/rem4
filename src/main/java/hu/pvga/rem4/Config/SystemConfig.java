/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Config;

import java.util.List;

public class SystemConfig extends BaseConfig {
    private String discordToken;
    private String botName;
    private String botIcon;

    private String databaseJDBC;

    private List<String> enabledFeatures;

    private List<String> adminUsers;

    private boolean apiEnabled;
    private int apiPort;
    private boolean apiBasicAuth;
    private String apiUser;
    private String apiPassword;

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

    public boolean isApiEnabled() {
        return apiEnabled;
    }

    public void setApiEnabled(boolean apiEnabled) {
        this.apiEnabled = apiEnabled;
    }

    public int getApiPort() {
        return apiPort;
    }

    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    public boolean isApiBasicAuth() {
        return apiBasicAuth;
    }

    public void setApiBasicAuth(boolean apiBasicAuth) {
        this.apiBasicAuth = apiBasicAuth;
    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }
}
