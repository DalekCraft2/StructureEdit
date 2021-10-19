package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.ListTag;

public interface MultiPaletteSchematic extends PaletteSchematic {

    /**
     * Returns the {@link Palette} list of this {@link Schematic}.
     *
     * @return the {@link Palette} list of this {@link Schematic}
     */
    ListTag<ListTag<?>> getPaletteList();

    /**
     * Sets the {@link Palette} list of this {@link Schematic}.
     *
     * @param palettes the new {@link Palette} list
     */
    void setPaletteList(ListTag<ListTag<?>> palettes);

    /**
     * Returns the {@link Palette} at the specified index of the {@link Schematic}'s {@link Palette} list.
     *
     * @param index the index of a {@link Palette}
     * @return the {@link Palette} at the specified index
     */
    Palette getPaletteListEntry(int index);

    /**
     * Returns the {@link Palette} at the specified index of this {@link Schematic}'s {@link Palette} list.
     *
     * @param index   the index of a {@link Palette}
     * @param palette the new {@link Palette}
     */
    void setPaletteListEntry(int index, Palette palette);

    /**
     * Returns whether this {@link Schematic} has a {@link Palette} list.
     *
     * @return whether this {@link Schematic} has a {@link Palette} list
     */
    boolean hasPaletteList();

    /**
     * Sets the active {@link Palette} for methods which edit the {@link Palette}.
     *
     * @param index the index of the new {@link Palette}
     */
    void setActivePalette(int index);
}
