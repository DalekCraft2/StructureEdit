package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.CompoundTag;

public class SpongePalette implements Palette {

    private CompoundTag palette;

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
    public CompoundTag getState(int index) {
        return null;
    }

    @Override
    public void setState(int index, CompoundTag state) {

    }
}
