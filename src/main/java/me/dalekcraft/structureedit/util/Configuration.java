package me.dalekcraft.structureedit.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

// TODO Add more configuration.
public final class Configuration {

    private static final Logger LOGGER;
    private static final Path CONFIG_DIRECTORY = ConfigPaths.getConfigFilePath("StructureEdit");
    private static final Path CONFIG_FILE = CONFIG_DIRECTORY.resolve("config.properties");
    public static final Properties CONFIG = new Properties() {
        @Override
        public synchronized Object setProperty(String key, String value) {
            Object o = super.setProperty(key, value);
            try {
                store(Files.newOutputStream(CONFIG_FILE), null);
            } catch (IOException e) {
                LOGGER.catching(e);
            }
            return o;
        }
    };
    private static final Path LOG_DIRECTORY = CONFIG_DIRECTORY.resolve("logs");
    public static final Path LOG_LATEST_FILE = LOG_DIRECTORY.resolve("StructureEdit-latest.log");
    public static final Path LOG_FILE = LOG_DIRECTORY.resolve("StructureEdit-%d{yyyy-MM-dd}-%i.log.gz");


    static {
        // Set the log output file locations
        System.setProperty("LOG_LATEST_FILE", LOG_LATEST_FILE.toString());
        System.setProperty("LOG_FILE", LOG_FILE.toString());

        LOGGER = LogManager.getLogger();

        try {
            if (!Files.exists(CONFIG_DIRECTORY)) {
                InputStream configStream = Configuration.class.getResourceAsStream("/" + CONFIG_FILE.getFileName());
                try {
                    assert configStream != null;
                    Files.createDirectories(CONFIG_FILE.getParent());
                    Files.copy(configStream, CONFIG_FILE);
                } catch (IOException e) {
                    LOGGER.catching(e);
                }
            }
            CONFIG.load(Files.newInputStream(CONFIG_FILE));
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    private Configuration() {
        throw new UnsupportedOperationException();
    }
}
