/*
 * Created by vshiro (https://stackoverflow.com/users/4544015/vshiro)
 * https://stackoverflow.com/a/29736246
 */
package me.dalekcraft.structureedit.ui;

import javafx.scene.control.TextArea;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.ArrayList;
import java.util.Collection;

import static org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout;

@Plugin(name = "TextAreaAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class TextAreaAppender extends AbstractAppender {
    private static final Collection<TextArea> TEXT_AREAS = new ArrayList<>();
    private final int maxLines;

    private TextAreaAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
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

    @Override
    public void append(LogEvent event) {
        String message = new String(getLayout().toByteArray(event));

        // Append formatted message to text area.
        for (TextArea textArea : TEXT_AREAS) {
            if (textArea != null) {
                textArea.appendText(message);

                // Limit number of lines
                if (maxLines >= 0) {
                    String text = textArea.getText();
                    int count = StringUtils.countMatches(text, '\n');
                    if (count > maxLines) {
                        for (int numExtras = count - maxLines; numExtras > 0; numExtras--) {
                            text = text.substring(text.indexOf('\n') + 1);
                        }
                        textArea.setText(text);
                    }
                }
            }
        }
    }
}
