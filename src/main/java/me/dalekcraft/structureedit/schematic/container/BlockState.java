package me.dalekcraft.structureedit.schematic.container;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlockState {

    private static final Splitter.MapSplitter SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings().trimResults().withKeyValueSeparator("=");
    private static final Joiner.MapJoiner JOINER = Joiner.on(",").withKeyValueSeparator("=");
    @NotNull
    private String id;
    @NotNull
    private Map<String, String> properties;

    public BlockState(@NotNull String id) {
        this(id, new HashMap<>());
    }

    public BlockState(@NotNull String id, Map<String, String> properties) {
        this.id = Objects.requireNonNull(id);
        this.properties = Objects.requireNonNullElse(properties, new HashMap<>());
    }

    public static BlockState toBlockState(String blockStateString) {
        int nameEndIndex = blockStateString.length();
        if (blockStateString.contains("[")) {
            nameEndIndex = blockStateString.indexOf('[');
        }

        String id = blockStateString.substring(0, nameEndIndex);

        String propertyString = blockStateString.substring(nameEndIndex).replace("[", "").replace("]", "");
        Map<String, String> propertyMap = toPropertyMap(propertyString);

        return new BlockState(id, propertyMap);
    }

    public static String toBlockStateString(BlockState blockState) {
        return blockState.getId() + toPropertyString(blockState.getProperties());
    }

    public static Map<String, String> toPropertyMap(String propertyString) {
        return toPropertyMap(propertyString, true);
    }

    public static Map<String, String> toPropertyMap(String propertyString, boolean containsBrackets) {
        if (containsBrackets) {
            propertyString = propertyString.replace("[", "").replace("]", "");
        }
        return SPLITTER.split(propertyString);
    }

    public static String toPropertyString(Map<String, String> propertyMap) {
        return toPropertyString(propertyMap, false);
    }

    public static String toPropertyString(Map<String, String> propertyMap, boolean omitBrackets) {
        if (omitBrackets) {
            return propertyMap.isEmpty() ? "" : JOINER.join(propertyMap);
        } else {
            return propertyMap.isEmpty() ? "" : "[" + JOINER.join(propertyMap) + "]";
        }
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
        this.id = Objects.requireNonNull(id);
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
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BlockState casted = (BlockState) obj;
        return id.equals(casted.id) && properties.equals(casted.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, properties);
    }

    @Override
    public String toString() {
        return toBlockStateString(this);
    }
}
