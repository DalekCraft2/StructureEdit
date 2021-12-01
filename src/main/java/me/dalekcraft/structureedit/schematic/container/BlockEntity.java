package me.dalekcraft.structureedit.schematic.container;

import net.querz.nbt.tag.CompoundTag;

import java.util.Objects;

// TODO Replace the "nbt" field in the Block class with this.
public class BlockEntity {

    private String id;
    private CompoundTag nbt;

    public BlockEntity(String id) {
        this(id, new CompoundTag());
    }

    public BlockEntity(String id, CompoundTag nbt) {
        this.id = id;
        this.nbt = Objects.requireNonNullElse(nbt, new CompoundTag());
    }

    /**
     * Returns the namespaced ID of this {@link BlockEntity}.
     *
     * @return the namespaced ID of this {@link BlockEntity}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the namespaced ID of this {@link BlockEntity}.
     *
     * @param id the new namespaced ID for this {@link BlockEntity}
     */
    public void setId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Returns the NBT of this {@link BlockEntity}.
     *
     * @return the NBT of this {@link BlockEntity}
     */
    public CompoundTag getNbt() {
        return nbt;
    }

    /**
     * Sets the NBT of this {@link BlockEntity}.
     *
     * @param nbt the new NBT for this {@link BlockEntity}
     */
    public void setNbt(CompoundTag nbt) {
        this.nbt = Objects.requireNonNullElse(nbt, new CompoundTag());
    }
}
