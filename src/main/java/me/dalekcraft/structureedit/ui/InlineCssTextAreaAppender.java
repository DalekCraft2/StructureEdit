/*
 * Created by vshiro (https://stackoverflow.com/users/4544015/vshiro)
 * https://stackoverflow.com/a/29736246
 */
package me.dalekcraft.structureedit.ui;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.Collection;

import static org.apache.logging.log4j.core.config.Property.EMPTY_ARRAY;
import static org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout;

@Plugin(name = "InlineCssTextAreaAppender", category = "Core", elementType = "appender", printObject = true)
public class InlineCssTextAreaAppender extends AbstractAppender {
    private static final Collection<InlineCssTextArea> TEXT_AREAS = new ArrayList<>();
    private final int maxLines; // TODO Maybe reimplement this.

    private InlineCssTextAreaAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, EMPTY_ARRAY);
        this.maxLines = maxLines;
    }

    @SuppressWarnings("unused")
    @PluginFactory
    public static InlineCssTextAreaAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("maxLines") int maxLines, @PluginAttribute("ignoreExceptions") boolean ignoreExceptions, @PluginElement("Layout") Layout<?> layout, @PluginElement("Filters") Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for InlineCssTextAreaAppender");
            return null;
        }

        if (layout == null) {
            layout = createDefaultLayout();
        }
        return new InlineCssTextAreaAppender(name, layout, filter, maxLines, ignoreExceptions);
    }

    // Add the target TextArea to be populated and updated by the logging information.
    public static void addLog4j2TextAreaAppender(final InlineCssTextArea textArea) {
        TEXT_AREAS.add(textArea);
    }

    @Override
    public void append(LogEvent event) {
        String message = new String(getLayout().toByteArray(event));
        // Append formatted message to text area.
        for (InlineCssTextArea textArea : TEXT_AREAS) {
            if (textArea != null) {
                Level level = event.getLevel();
                /*String style = "-fx-font-family: monospace; -fx-fill: ";
                if (level.equals(Level.FATAL)) {
                    style = style.concat(LogColors.colorToHex(LogColors.FATAL));
                } else if (level.equals(Level.ERROR)) {
                    style = style.concat(LogColors.colorToHex(LogColors.ERROR));
                } else if (level.equals(Level.WARN)) {
                    style = style.concat(LogColors.colorToHex(LogColors.WARN));
                } else if (level.equals(Level.INFO)) {
                    style = style.concat(LogColors.colorToHex(LogColors.INFO));
                } else if (level.equals(Level.DEBUG)) {
                    style = style.concat(LogColors.colorToHex(LogColors.DEBUG));
                } else if (level.equals(Level.TRACE)) {
                    style = style.concat(LogColors.colorToHex(LogColors.TRACE));
                } else {
                    style = style.concat(LogColors.colorToHex(Color.WHITE));
                }
                style = style.concat(";");

                String finalStyle = style;*/
                StringBuilder style = new StringBuilder("-fx-font-family: monospace; -fx-fill: ");
                if (level.equals(Level.FATAL)) {
                    style.append(LogColors.colorToHex(LogColors.FATAL));
                } else if (level.equals(Level.ERROR)) {
                    style.append(LogColors.colorToHex(LogColors.ERROR));
                } else if (level.equals(Level.WARN)) {
                    style.append(LogColors.colorToHex(LogColors.WARN));
                } else if (level.equals(Level.INFO)) {
                    style.append(LogColors.colorToHex(LogColors.INFO));
                } else if (level.equals(Level.DEBUG)) {
                    style.append(LogColors.colorToHex(LogColors.DEBUG));
                } else if (level.equals(Level.TRACE)) {
                    style.append(LogColors.colorToHex(LogColors.TRACE));
                } else {
                    style.append(LogColors.colorToHex(Color.WHITE));
                }
                style.append(";");
                Platform.runLater(() -> {
                    int index1 = textArea.getLength();
                    textArea.appendText(message);
                    int index2 = textArea.getLength();
                    textArea.setStyle(index1, index2 - 1, style.toString());
                });
            }
        }
    }
}
