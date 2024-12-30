package me.dalekcraft.structureedit.ui;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class LogColors {

    public static final Color FATAL = Color.RED;
    public static final Color ERROR = Color.RED;
    public static final Color WARN = Color.YELLOW;
    public static final Color INFO = Color.GREEN;
    public static final Color DEBUG = Color.CYAN;
    public static final Color TRACE = Color.BLACK;

    private LogColors() {
        throw new UnsupportedOperationException();
    }

    public static String colorToHex(@NotNull Color color) {
        String hex = Integer.toHexString(color.hashCode()).toUpperCase(Locale.ROOT);
        return switch (hex.length()) {
            case 2 -> "#000000";
            case 3 -> String.format("#00000%s", hex.charAt(0));
            case 4 -> String.format("#0000%s", hex.substring(0, 2));
            case 5 -> String.format("#000%s", hex.substring(0, 3));
            case 6 -> String.format("#00%s", hex.substring(0, 4));
            case 7 -> String.format("#0%s", hex.substring(0, 5));
            default -> "#" + hex.substring(0, 6);
        };
    }
}
