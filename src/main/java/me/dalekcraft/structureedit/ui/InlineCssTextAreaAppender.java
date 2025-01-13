/*
 * Created by vshiro (https://stackoverflow.com/users/4544015/vshiro)
 * https://stackoverflow.com/a/29736246
 */
package me.dalekcraft.structureedit.ui;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.Collection;

import static org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout;

@Plugin(name = "InlineCssTextAreaAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class InlineCssTextAreaAppender extends AbstractAppender {
    private static final Collection<InlineCssTextArea> TEXT_AREAS = new ArrayList<>();
    private final int maxLines;

    private InlineCssTextAreaAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
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

                String fill;
                if (level.equals(Level.FATAL)) {
                    fill = LogColors.colorToHex(LogColors.FATAL);
                } else if (level.equals(Level.ERROR)) {
                    fill = LogColors.colorToHex(LogColors.ERROR);
                } else if (level.equals(Level.WARN)) {
                    fill = LogColors.colorToHex(LogColors.WARN);
                } else if (level.equals(Level.INFO)) {
                    fill = LogColors.colorToHex(LogColors.INFO);
                } else if (level.equals(Level.DEBUG)) {
                    fill = LogColors.colorToHex(LogColors.DEBUG);
                } else if (level.equals(Level.TRACE)) {
                    fill = LogColors.colorToHex(LogColors.TRACE);
                } else {
                    fill = LogColors.colorToHex(Color.WHITE);
                }

                /*String highlightTextFill; // TODO Make this work.
                if (level.equals(Level.FATAL)) {
                    highlightTextFill = LogColors.colorToHex(LogColors.FATAL);
                } else if (level.equals(Level.ERROR)) {
                    highlightTextFill = LogColors.colorToHex(LogColors.ERROR);
                } else if (level.equals(Level.WARN)) {
                    highlightTextFill = LogColors.colorToHex(LogColors.WARN);
                } else if (level.equals(Level.INFO)) {
                    highlightTextFill = LogColors.colorToHex(LogColors.INFO);
                } else if (level.equals(Level.DEBUG)) {
                    highlightTextFill = LogColors.colorToHex(LogColors.DEBUG);
                } else if (level.equals(Level.TRACE)) {
                    highlightTextFill = LogColors.colorToHex(LogColors.TRACE);
                } else {
                    highlightTextFill = LogColors.colorToHex(Color.WHITE);
                }*/

                /*String style = """
                        -fx-font-family: monospace;
                        -fx-fill: %s;
                        -fx-highlight-text-fill: %s;""".formatted(fill, highlightTextFill);*/

                String style = """
                        -fx-font-family: monospace;
                        -fx-fill: %s;""".formatted(fill);

                Platform.runLater(() -> {
                    int index1 = textArea.getLength();
                    textArea.appendText(message);
                    int index2 = textArea.getLength();
                    textArea.setStyle(index1, index2 - 1, style);

                    // Limit number of lines
                    if (maxLines > 0) {
                        String text = textArea.getText();
                        int count = StringUtils.countMatches(text, '\n');
                        if (count > maxLines) {
                            for (int numExtras = count - maxLines; numExtras > 0; numExtras--) {
                                textArea.replaceText(0, textArea.getText().indexOf('\n') + 1, "");
                            }
                        }
                    }
                });
            }
        }
    }
}
