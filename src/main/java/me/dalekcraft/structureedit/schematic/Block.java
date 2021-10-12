package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface Block {

    /**
     * Returns the position of this block.
     *
     * @return the position of this block
     */
    int[] getPosition();

    /**
     * Sets the position of this block.
     *
     * @param position the new x position for this block
     */
    default void setPosition(int @NotNull [] position) {
        setPosition(position[0], position[1], position[2]);
    }

    /**
     * Sets the position of this block.
     *
     * @param x the new x position for this block
     * @param y the new y position for this block
     * @param z the new z position for this block
     */
    void setPosition(int x, int y, int z);

    /**
     * Returns the namespaced ID of this block.
     *
     * @return the namespaced ID of this block
     */
    String getId();

    /**
     * Sets the namespaced ID of this block.
     *
     * @param id the new namespaced ID for this block
     */
    void setId(String id);

    /**
     * Returns the properties of this block, as a {@link CompoundTag}.
     *
     * @return the properties of this block, as a {@link CompoundTag}
     */
    CompoundTag getProperties();

    /**
     * Sets the properties of this block.
     *
     * @param properties the new properties for this block, as a {@link CompoundTag}
     */
    void setProperties(CompoundTag properties);

    /**
     * Returns the properties of this block, as a {@link String}.
     *
     * @return the properties of this block, as a {@link String}
     */
    String getPropertiesAsString();

    /**
     * Sets the properties of this block.
     *
     * @param propertiesString the new properties for this block, as a {@link String}
     */
    void setPropertiesAsString(String propertiesString) throws IOException;

    /**
     * Returns the NBT of this block.
     *
     * @return the NBT of this block
     */
    CompoundTag getNbt();

    /**
     * Sets the NBT of this block.
     *
     * @param nbt the new NBT for this block
     */
    void setNbt(CompoundTag nbt);

    /**
     * Returns the NBT of this block, translated into SNBT.
     *
     * @return the NBT of this block, translated into SNBT
     */
    String getSnbt();

    /**
     * Sets the NBT of this block.
     *
     * @param snbt the new NBT for this block, as SNBT
     */
    void setSnbt(String snbt) throws IOException;

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
