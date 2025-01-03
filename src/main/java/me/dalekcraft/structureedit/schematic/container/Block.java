package me.dalekcraft.structureedit.schematic.container;

import me.dalekcraft.structureedit.assets.ResourceLocation;

import java.util.Objects;

public class Block {

    private int blockStateIndex;
    private BlockEntity blockEntity;

    public Block(int blockStateIndex) {
        this(blockStateIndex, null);
    }

    public Block(int blockStateIndex, BlockEntity blockEntity) {
        this.blockStateIndex = blockStateIndex;
        this.blockEntity = Objects.requireNonNullElse(blockEntity, new BlockEntity(new ResourceLocation("")));
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
        return blockEntity;
    }

    /**
     * Sets the NBT of this {@link Block}.
     *
     * @param blockEntity the new NBT for this {@link Block}
     */
    public void setBlockEntity(BlockEntity blockEntity) {
        this.blockEntity = Objects.requireNonNullElse(blockEntity, new BlockEntity(new ResourceLocation("")));
    }
}
