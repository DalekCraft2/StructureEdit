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
    public static CompoundTag byteToString(@NotNull CompoundTag nbt) {
        for (Map.Entry<String, Tag<?>> entry : nbt) {
            if (entry.getValue() instanceof ByteTag) {
                String key = entry.getKey();
                String value = String.valueOf(((ByteTag) entry.getValue()).asBoolean());
                StringTag tag = new StringTag(value);
                nbt.put(key, tag);
            }
        }
        return nbt;
    }
}
