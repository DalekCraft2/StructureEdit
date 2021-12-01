package me.dalekcraft.structureedit.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class PropertyUtils {

    public static final Splitter.MapSplitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator("=");
    public static final Joiner.MapJoiner JOINER = Joiner.on(",").withKeyValueSeparator("=");

    @Contract(value = " -> fail", pure = true)
    private PropertyUtils() {
        throw new UnsupportedOperationException();
    }

    @Contract("_ -> param1")
    @NotNull
    public static CompoundTag byteToString(CompoundTag nbt) {
        if (nbt == null || nbt.entrySet().isEmpty()) {
            return new CompoundTag();
        }
        for (Map.Entry<String, Tag<?>> entry : nbt) {
            if (entry.getValue() instanceof ByteTag byteTag) {
                String key = entry.getKey();
                String value = String.valueOf(byteTag.asBoolean());
                StringTag tag = new StringTag(value);
                nbt.put(key, tag);
            } else if (!(entry.getValue() instanceof StringTag)) {
                String key = entry.getKey();
                StringTag tag = new StringTag(entry.getValue().valueToString());
                nbt.put(key, tag);
            }
        }
        return nbt;
    }

    public static BlockState toBlockState(String blockStateString) {
        int nameEndIndex = blockStateString.length();
        if (blockStateString.contains("[")) {
            nameEndIndex = blockStateString.indexOf('[');
        }

        String id = blockStateString.substring(0, nameEndIndex);

        String propertyString = blockStateString.substring(nameEndIndex).replace("[", "").replace("]", "");
        Map<String, String> propertyMap = SPLITTER.split(propertyString);

        return new BlockState(id, propertyMap);
    }

    public static String fromBlockState(BlockState blockState) {
        return null;
    }

    public static Map<String, String> toPropertyMap(String propertyString) {
        return null;
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
}
