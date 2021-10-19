package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface Block {

    /**
     * Returns the position of this {@link Block}.
     *
     * @return the position of this {@link Block}
     */
    int[] getPosition();

    /**
     * Sets the position of this {@link Block}.
     *
     * @param position the new x position for this {@link Block}
     */
    default void setPosition(int @NotNull [] position) {
        setPosition(position[0], position[1], position[2]);
    }

    /**
     * Sets the position of this {@link Block}.
     *
     * @param x the new x position for this {@link Block}
     * @param y the new y position for this {@link Block}
     * @param z the new z position for this {@link Block}
     */
    void setPosition(int x, int y, int z);

    /**
     * Returns the namespaced ID of this {@link Block}.
     *
     * @return the namespaced ID of this {@link Block}
     */
    String getId();

    /**
     * Sets the namespaced ID of this {@link Block}.
     *
     * @param id the new namespaced ID for this {@link Block}
     */
    void setId(String id);

    /**
     * Returns the properties of this {@link Block}, as a {@link CompoundTag}.
     *
     * @return the properties of this {@link Block}, as a {@link CompoundTag}
     */
    CompoundTag getProperties();

    /**
     * Sets the properties of this {@link Block}.
     *
     * @param properties the new properties for this {@link Block}, as a {@link CompoundTag}
     */
    void setProperties(CompoundTag properties);

    /**
     * Returns the properties of this {@link Block}, as a {@link String}.
     *
     * @return the properties of this {@link Block}, as a {@link String}
     */
    String getPropertiesAsString();

    /**
     * Sets the properties of this {@link Block}.
     *
     * @param propertiesString the new properties for this {@link Block}, as a {@link String}
     */
    void setPropertiesAsString(String propertiesString) throws IOException;

    /**
     * Returns the NBT of this {@link Block}.
     *
     * @return the NBT of this {@link Block}
     */
    CompoundTag getNbt();

    /**
     * Sets the NBT of this {@link Block}.
     *
     * @param nbt the new NBT for this {@link Block}
     */
    void setNbt(CompoundTag nbt);

    /**
     * Returns the NBT of this {@link Block}, translated into SNBT.
     *
     * @return the NBT of this {@link Block}, translated into SNBT
     */
    String getSnbt();

    /**
     * Sets the NBT of this {@link Block}.
     *
     * @param snbt the new NBT for this {@link Block}, as SNBT
     */
    void setSnbt(String snbt) throws IOException;
}
