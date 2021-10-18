package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.ListTag;

public interface MultiPaletteSchematic extends PaletteSchematic {

    ListTag<ListTag<?>> getPaletteList();

    void setPaletteList(ListTag<ListTag<?>> palettes);

    Palette getPaletteListEntry(int index);

    void setPaletteListEntry(int index, Palette palette);

    boolean hasPaletteList();

    void setActivePalette(int index);
}
