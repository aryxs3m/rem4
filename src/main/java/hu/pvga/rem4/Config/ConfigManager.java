/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

/**
 * ConfigManager
 *
 * Small utility that helps in loading configuration files.
 *
 * Every configuration file stored in the <bold>config</bold> directory. Filenames are matching the corresponding class
 * names.
 */
public class ConfigManager {
    private static final String CONFIG_DIRECTORY = "config";

    public static <T extends BaseConfig> T load(Class<T> config) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(
                new File(CONFIG_DIRECTORY
                    .concat("/")
                    .concat(config.getSimpleName())
                    .concat(".yaml")),
                config
        );
    }
}
