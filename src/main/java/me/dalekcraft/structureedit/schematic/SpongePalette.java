package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.Contract;

import java.util.Map;

public class SpongePalette implements Palette {

    private final CompoundTag palette;

    @Contract(pure = true)
    public SpongePalette(CompoundTag palette) {
        this.palette = palette;
    }

    @Override
    public CompoundTag getData() {
        return palette;
    }

    @Override
    public int size() {
        return palette.size();
    }

    @Override
    public String getState(int index) {
        String state = null;
        for (String tagName : palette.keySet()) {
            if (palette.getInt(tagName) == index) {
                state = tagName;
            }
        }
        return state;
    }

    @Override
    public void setState(int index, Object state) {
        for (Map.Entry<String, Tag<?>> tagEntry : palette.entrySet()) {
            if (((IntTag) tagEntry.getValue()).asInt() == index) {
                String tagName = tagEntry.getKey();
                palette.put((String) state, palette.remove(tagName));
                break;
            }
        }
    }
}
