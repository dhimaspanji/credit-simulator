package com.project.creditsimulator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    private ConfigLoader() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Config file not found in resources!");
            }
            properties.load(input);
        } catch (IOException e) {
            logger.error("Error loading config file: {} ", e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
