package me.dalekcraft.structureedit.schematic.container;

import java.util.Objects;

public class Block {

    private int blockStateIndex;
    private BlockEntity nbt;

    public Block(int blockStateIndex) {
        this(blockStateIndex, null);
    }

    public Block(int blockStateIndex, BlockEntity nbt) {
        this.blockStateIndex = blockStateIndex;
        this.nbt = Objects.requireNonNullElse(nbt, new BlockEntity(""));
    }

    /**
     * Returns the {@link BlockState} of this {@link Block}.
     *
     * @return the {@link BlockState} of this {@link Block}
     */
    public int getBlockStateIndex() {
        return blockStateIndex;
    }

    /**
     * Sets the {@link BlockState} of this {@link Block}.
     *
     * @param blockStateIndex the new {@link BlockState} for this {@link Block}
     */
    public void setBlockStateIndex(int blockStateIndex) {
        this.blockStateIndex = blockStateIndex;
    }

    /**
     * Returns the NBT of this {@link Block}.
     *
     * @return the NBT of this {@link Block}
     */
    public BlockEntity getBlockEntity() {
        return nbt;
    }

    /**
     * Sets the NBT of this {@link Block}.
     *
     * @param nbt the new NBT for this {@link Block}
     */
    public void setBlockEntity(BlockEntity nbt) {
        this.nbt = Objects.requireNonNullElse(nbt, new BlockEntity(""));
    }
}
