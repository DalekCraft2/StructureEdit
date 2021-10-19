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

    interface PaletteBlock extends Block {

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

    interface Palette {

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
}
