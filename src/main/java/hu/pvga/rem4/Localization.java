/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    private final ResourceBundle messages;

    public Localization(String language, String country)
    {
        messages = ResourceBundle.getBundle("localization/Localization", new Locale(language, country));
    }

    public String get(String key)
    {
        return messages.getString(key);
    }
}
