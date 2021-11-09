/*
 * Created by vshiro (https://stackoverflow.com/users/4544015/vshiro)
 * https://stackoverflow.com/questions/24005748/how-to-output-logs-to-a-jtextarea-using-log4j2
 */
package me.dalekcraft.structureedit.ui;

import javafx.scene.control.TextArea;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.logging.log4j.core.config.Property.EMPTY_ARRAY;
import static org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout;

@Plugin(name = "TextAreaAppender", category = "Core", elementType = "appender", printObject = true)
public class TextAreaAppender extends AbstractAppender {
    private static final Collection<TextArea> TEXT_AREAS = new ArrayList<>();
    private final int maxLines;

    private TextAreaAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, EMPTY_ARRAY);
        this.maxLines = maxLines;
    }

    @SuppressWarnings("unused")
    @PluginFactory
    public static TextAreaAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("maxLines") int maxLines, @PluginAttribute("ignoreExceptions") boolean ignoreExceptions, @PluginElement("Layout") Layout<?> layout, @PluginElement("Filters") Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for TextAreaAppender");
            return null;
        }

        if (layout == null) {
            layout = createDefaultLayout();
        }
        return new TextAreaAppender(name, layout, filter, maxLines, ignoreExceptions);
    }

    // Add the target TextArea to be populated and updated by the logging information.
    public static void addLog4j2TextAreaAppender(final TextArea textArea) {
        TEXT_AREAS.add(textArea);
    }

    /*public static int getLineCount(TextArea textArea) {
        int totalCharacters = textArea.getText().length();
        int lineCount = (totalCharacters == 0) ? 1 : 0;

        try {
            int offset = totalCharacters;
            while (offset > 0) {
                offset = Utilities.getRowStart(textArea, offset) - 1;
                lineCount++;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return lineCount;
    }*/

    @Override
    public void append(LogEvent event) {
        String message = new String(getLayout().toByteArray(event));

        // Append formatted message to text area using the Thread.
        try {
            SwingUtilities.invokeLater(() -> {
                for (TextArea textArea : TEXT_AREAS) {
                    try {
                        if (textArea != null) {
                            if (textArea.getText().isEmpty()) {
                                textArea.appendText(message + "\n");
                            } else {
                                textArea.appendText(message + "\n");
                                // if (maxLines > 0 & getLineCount(textArea) > maxLines + 1) {
                                //     int endIdx = textArea.getText(0, textArea.getDocument().getLength()).indexOf("\n");
                                //     textArea.getDocument().remove(0, endIdx + 1);
                                // }
                            }
                            String content = textArea.getText();
                            textArea.setText(content.substring(0, content.length() - 1));
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
        } catch (IllegalStateException exception) {
            exception.printStackTrace();
        }
    }
}
