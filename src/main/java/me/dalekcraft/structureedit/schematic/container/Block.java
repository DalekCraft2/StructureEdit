package me.dalekcraft.structureedit.schematic.container;

import net.querz.nbt.tag.CompoundTag;

import java.util.Map;

public class Block {

    private BlockState blockState;
    private CompoundTag nbt;

    /**
     * Returns the namespaced ID of this {@link Block}.
     *
     * @return the namespaced ID of this {@link Block}
     */
    public String getId() {
        return blockState.getId();
    }

    /**
     * Sets the namespaced ID of this {@link Block}.
     *
     * @param id the new namespaced ID for this {@link Block}
     */
    public void setId(String id) {
        blockState.setId(id);
    }

    /**
     * Returns the properties of this {@link Block}.
     *
     * @return the properties of this {@link Block}
     */
    public Map<String, String> getProperties() {
        return blockState.getProperties();
    }

    /**
     * Sets the properties of this {@link Block}.
     *
     * @param properties the new properties for this {@link Block}
     */
    public void setProperties(Map<String, String> properties) {
        blockState.setProperties(properties);
    }


    /**
     * Returns the {@link BlockState} of this {@link Block}.
     *
     * @return the {@link BlockState} of this {@link Block}
     */
    public BlockState getBlockState() {
        return blockState;
    }

    /**
     * Sets the {@link BlockState} of this {@link Block}.
     *
     * @param blockState the new {@link BlockState} for this {@link Block}
     */
    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    /**
     * Returns the NBT of this {@link Block}.
     *
     * @return the NBT of this {@link Block}
     */
    public CompoundTag getNbt() {
        return nbt;
    }

    /**
     * Sets the NBT of this {@link Block}.
     *
     * @param nbt the new NBT for this {@link Block}
     */
    public void setNbt(CompoundTag nbt) {
        this.nbt = nbt;
    }
}
