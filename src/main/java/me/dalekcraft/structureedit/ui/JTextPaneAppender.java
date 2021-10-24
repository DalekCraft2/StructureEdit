/*
 * Created by vshiro (https://stackoverflow.com/users/4544015/vshiro)
 * https://stackoverflow.com/questions/24005748/how-to-output-logs-to-a-jtextarea-using-log4j2
 */
package me.dalekcraft.structureedit.ui;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.logging.log4j.core.config.Property.EMPTY_ARRAY;
import static org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout;

@Plugin(name = "JTextPaneAppender", category = "Core", elementType = "appender", printObject = true)
public class JTextPaneAppender extends AbstractAppender {
    private static final Collection<JTextPane> TEXT_PANES = new ArrayList<>();
    private final int maxLines;

    private JTextPaneAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, EMPTY_ARRAY);
        this.maxLines = maxLines;
    }

    @SuppressWarnings("unused")
    @PluginFactory
    public static JTextPaneAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("maxLines") int maxLines, @PluginAttribute("ignoreExceptions") boolean ignoreExceptions, @PluginElement("Layout") Layout<?> layout, @PluginElement("Filters") Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for JTextPaneAppender");
            return null;
        }

        if (layout == null) {
            layout = createDefaultLayout();
        }
        return new JTextPaneAppender(name, layout, filter, maxLines, ignoreExceptions);
    }

    // Add the target JTextPane to be populated and updated by the logging information.
    public static void addLog4j2TextPaneAppender(final JTextPane textPane) {
        TEXT_PANES.add(textPane);
    }

    public static int getLineCount(JTextPane textPane) {
        int totalCharacters = textPane.getText().length();
        int lineCount = (totalCharacters == 0) ? 1 : 0;

        try {
            int offset = totalCharacters;
            while (offset > 0) {
                offset = Utilities.getRowStart(textPane, offset) - 1;
                lineCount++;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return lineCount;
    }

    @Override
    public void append(LogEvent event) {
        String message = new String(getLayout().toByteArray(event));

        // Append formatted message to text pane using the Thread.
        try {
            SwingUtilities.invokeLater(() -> {
                for (JTextPane textPane : TEXT_PANES) {
                    try {
                        if (textPane != null) {
                            if (textPane.getText().isEmpty()) {
                                textPane.setText(message);
                            } else {
                                textPane.setText(textPane.getText() + message);
                                // if (maxLines > 0 & getLineCount(textPane) > maxLines + 1) {
                                //     int endIdx = textPane.getText(0, textPane.getDocument().getLength()).indexOf("\n");
                                //     textPane.getDocument().remove(0, endIdx + 1);
                                // }
                            }
                            String content = textPane.getText();
                            textPane.setText(content.substring(0, content.length() - 1));
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
