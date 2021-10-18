package me.dalekcraft.structureedit.schematic;

public interface PaletteSchematic extends Schematic {

    /**
     * Returns the palette of this {@link Schematic}.
     *
     * @return the palette of this {@link Schematic}
     */
    Palette getPalette();

    /**
     * Sets the palette of this {@link Schematic}.
     *
     * @param palette the new palette
     */
    void setPalette(Palette palette);
}
