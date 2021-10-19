package me.dalekcraft.structureedit.schematic;

public interface Palette {

    /**
     * Returns the raw data of this {@link Palette}.
     *
     * @return the raw data of this {@link Palette}
     */
    Object getData();

    /**
     * Returns the size of this {@link Palette}.
     *
     * @return the size of this {@link Palette}
     */
    int size();

    /**
     * Returns the block state at the specified index.
     *
     * @param index the index of a block state
     * @return the block state at the specified index
     */
    Object getState(int index);

    /**
     * Sets the block state at the specified index.
     *
     * @param index the index of a block state
     * @param state the new block state
     */
    void setState(int index, Object state);
}
