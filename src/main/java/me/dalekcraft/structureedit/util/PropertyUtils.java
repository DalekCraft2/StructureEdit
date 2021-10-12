package me.dalekcraft.structureedit.util;

import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class PropertyUtils {

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
}
