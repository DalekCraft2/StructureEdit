package me.dalekcraft.structureedit.util;

import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

import java.util.Map;

public final class PropertyUtils {

    private PropertyUtils() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag byteToString(CompoundTag nbt) {
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
