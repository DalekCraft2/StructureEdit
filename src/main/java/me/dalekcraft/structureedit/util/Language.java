package me.dalekcraft.structureedit.util;

public final class Language {

    public static final String TITLE = "StructureEdit";
    public static final String TITLE_WITH_FILE = "%s - " + TITLE;
    public static final String NULL_SCHEMATIC = "Schematic was null!";
    public static final String NOT_SCHEMATIC = "Not a schematic file!";
    public static final String LOADING = "Loading %s ...";
    public static final String LOADED = "Loaded %s successfully.";
    public static final String ERROR_READING_SCHEMATIC = "Error reading schematic: %s";
    public static final String SAVING = "Saving %s ...";
    public static final String SAVED = "Schematic saved to %s successfully.";
    public static final String ERROR_SAVING_SCHEMATIC = "Error saving schematic: %s";

    private Language() {
        throw new UnsupportedOperationException();
    }
}
