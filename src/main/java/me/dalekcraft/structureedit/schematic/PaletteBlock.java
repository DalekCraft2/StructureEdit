package me.dalekcraft.structureedit.schematic;

public interface PaletteBlock extends Block{

    /**
     * Returns the palette index of this block.
     *
     * @return the palette index of this block
     */
    int getStateIndex();

    /**
     * Sets the palette index of this block.
     *
     * @param state the new palette index for this block
     */
    void setStateIndex(int state);
}
