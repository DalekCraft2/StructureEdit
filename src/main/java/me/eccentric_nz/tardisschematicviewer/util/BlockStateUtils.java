package me.eccentric_nz.tardisschematicviewer.util;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import java.io.IOException;
import java.util.Map;

public class BlockStateUtils {

    private BlockStateUtils() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag toTag(String properties) {
        String snbt = properties.replace('[', '{').replace(']', '}').replace('=', ':');
        CompoundTag nbt = null;
        try {
            nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return nbt;
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
}
