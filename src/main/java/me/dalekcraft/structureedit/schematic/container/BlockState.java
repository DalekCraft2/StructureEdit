package me.dalekcraft.structureedit.schematic.container;

import com.google.common.base.Splitter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlockState {

    public static final Splitter.MapSplitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator("=");

    @NotNull
    private String id;
    @NotNull
    private Map<String, String> properties;

    public BlockState(@NotNull String id) {
        this(id, new HashMap<>());
    }

    public BlockState(@NotNull String id, Map<String, String> properties) {
        this.id = id;
        this.properties = Objects.requireNonNullElse(properties, new HashMap<>());
    }

    /**
     * Returns the namespaced ID of this {@link BlockState}.
     *
     * @return the namespaced ID of this {@link BlockState}
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Sets the namespaced ID of this {@link BlockState}.
     *
     * @param id the new namespaced ID for this {@link BlockState}
     */
    public void setId(@NotNull String id) {
        this.id = id;
    }

    /**
     * Returns the properties of this {@link BlockState}.
     *
     * @return the properties of this {@link BlockState}
     */
    @NotNull
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Sets the properties of this {@link BlockState}.
     *
     * @param properties the new properties for this {@link BlockState}
     */
    public void setProperties(@NotNull Map<String, String> properties) {
        this.properties = properties;
    }
}
