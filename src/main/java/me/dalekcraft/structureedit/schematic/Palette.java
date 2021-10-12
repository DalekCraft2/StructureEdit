package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.CompoundTag;

public interface Palette {

    Object getData();

    int size();

    CompoundTag getState(int index);

    void setState(int index, CompoundTag state);
}
