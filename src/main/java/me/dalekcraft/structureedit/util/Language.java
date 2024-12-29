package me.dalekcraft.structureedit.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Language {
    public static final ResourceBundle LANGUAGE;
    private static final String LANGUAGE_BUNDLE_NAME = "language.language";

    static {
        ResourceBundle languageBundle;
        try {
            languageBundle = ResourceBundle.getBundle(LANGUAGE_BUNDLE_NAME, Locale.getDefault());
        } catch (MissingResourceException e) {
            languageBundle = ResourceBundle.getBundle(LANGUAGE_BUNDLE_NAME, Locale.US);
        }
        LANGUAGE = languageBundle;
    }

    private Language() {
        throw new UnsupportedOperationException();
    }
}
