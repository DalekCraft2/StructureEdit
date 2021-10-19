package me.dalekcraft.structureedit.schematic;

public interface PaletteSchematic extends Schematic {

    /**
     * Returns the {@link Palette} of this {@link Schematic}.
     *
     * @return the {@link Palette} of this {@link Schematic}
     */
    Palette getPalette();

    /**
     * Sets the {@link Palette} of this {@link Schematic}.
     *
     * @param palette the new {@link Palette}
     */
    void setPalette(Palette palette);
}
