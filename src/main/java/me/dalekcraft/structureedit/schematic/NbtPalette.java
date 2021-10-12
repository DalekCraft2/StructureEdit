package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.Contract;

public class NbtPalette implements Palette {

    private final ListTag<CompoundTag> palette;

    @Contract(pure = true)
    public NbtPalette(ListTag<CompoundTag> palette) {
        this.palette = palette;
    }

    @Override
    public ListTag<CompoundTag> getData() {
        return palette;
    }

    @Override
    public int size() {
        return palette.size();
    }

    @Override
    public CompoundTag getState(int index) {
        return palette.get(index);
    }

    @Override
    public void setState(int index, Object state) {
        palette.set(index, (CompoundTag) state);
    }
}
