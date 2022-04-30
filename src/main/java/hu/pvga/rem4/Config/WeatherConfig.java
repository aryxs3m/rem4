/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Config;

/**
 * Weather Feature configuration
 */
public class WeatherConfig extends BaseConfig {
    private String openWeatherAppId;
    private String units;
    private String lang;

    public String getOpenWeatherAppId() {
        return openWeatherAppId;
    }

    public void setOpenWeatherAppId(String openWeatherAppId) {
        this.openWeatherAppId = openWeatherAppId;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
