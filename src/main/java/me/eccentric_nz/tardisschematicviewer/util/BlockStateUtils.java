package me.eccentric_nz.tardisschematicviewer.util;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

import java.io.IOException;
import java.util.Map;

public class BlockStateUtils {

    private BlockStateUtils() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag toTag(String properties) {
        return toTag(properties, true);
    }

    public static CompoundTag toTag(String properties, boolean blockStateFormat) {
        String snbt;
        if (blockStateFormat) {
            snbt = properties.replace('[', '{').replace(']', '}').replace('=', ':');
        } else {
            snbt = properties;
        }
        CompoundTag nbt = new CompoundTag();
        try {
            nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return byteToString(nbt);
    }

    public static String fromTag(CompoundTag nbt) {
        String snbt = null;
        try {
            snbt = SNBTUtil.toSNBT(nbt);
            snbt = snbt.replace('{', '[').replace('}', ']').replace(':', '=');
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return snbt;
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
