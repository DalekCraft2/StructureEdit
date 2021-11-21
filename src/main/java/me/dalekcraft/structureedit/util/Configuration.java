package me.dalekcraft.structureedit.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

// TODO Add more configuration.
public final class Configuration {

    public static final ResourceBundle LANGUAGE;
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Properties CONFIG = new Properties() {
        @Override
        public synchronized Object setProperty(String key, String value) {
            Object o = super.setProperty(key, value);
            String protocol = Configuration.class.getResource("").getProtocol();
            if (protocol.equals("jar")) {
                try (FileOutputStream fileOutputStream = new FileOutputStream("config.properties")) {
                    store(fileOutputStream, null);
                } catch (IOException e) {
                    LOGGER.log(Level.ERROR, e.getMessage());
                }
            }
            return o;
        }
    };

    static {
        try {
            String protocol = Configuration.class.getResource("").getProtocol();
            if (protocol.equals("jar")) {
                Path configPath = Path.of("config.properties");
                if (!Files.exists(configPath)) {
                    InputStream configStream = Configuration.class.getResourceAsStream("/config.properties");
                    try {
                        assert configStream != null;
                        Files.copy(configStream, configPath);
                    } catch (IOException e) {
                        LOGGER.log(Level.ERROR, e.getMessage());
                    }
                }
                CONFIG.load(Files.newInputStream(configPath));
            } else {
                CONFIG.load(Configuration.class.getResourceAsStream("/config.properties"));
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        ResourceBundle languageBundle;
        try {
            languageBundle = ResourceBundle.getBundle("language.language", Locale.getDefault());
        } catch (MissingResourceException e) {
            languageBundle = ResourceBundle.getBundle("language.language", Locale.US);
        }
        LANGUAGE = languageBundle;
    }

    @Contract(value = " -> fail", pure = true)
    private Configuration() {
        throw new UnsupportedOperationException();
    }
}
