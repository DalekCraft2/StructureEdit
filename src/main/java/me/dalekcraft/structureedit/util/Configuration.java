package me.dalekcraft.structureedit.util;

import me.dalekcraft.structureedit.ui.UserInterface;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.Properties;

// TODO Add more configuration.
public final class Configuration {

    public static final Properties LANGUAGE = new Properties();
    private static final Logger LOGGER = LogManager.getLogger(UserInterface.class);

    static {
        try {
            LANGUAGE.load(Configuration.class.getClassLoader().getResourceAsStream("language.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }

    @Contract(value = " -> fail", pure = true)
    private Configuration() {
        throw new UnsupportedOperationException();
    }
}
