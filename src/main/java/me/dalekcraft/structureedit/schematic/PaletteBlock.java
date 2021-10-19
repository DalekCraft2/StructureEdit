package me.dalekcraft.structureedit.schematic;

public interface PaletteBlock extends Block{

    /**
     * Returns the palette index of this {@link Block}.
     *
     * @return the palette index of this {@link Block}
     */
    int getStateIndex();

    /**
     * Sets the palette index of this {@link Block}.
     *
     * @param state the new palette index for this {@link Block}
     */
    void setStateIndex(int state);
}
