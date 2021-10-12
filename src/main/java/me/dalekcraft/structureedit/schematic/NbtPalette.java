package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class NbtPalette implements Palette {

    private final ListTag<CompoundTag> palette;

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
    public void setState(int index, CompoundTag state) {
        palette.set(index, state);
    }
}
