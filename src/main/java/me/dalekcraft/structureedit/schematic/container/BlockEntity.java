package me.dalekcraft.structureedit.schematic.container;

import me.dalekcraft.structureedit.assets.ResourceLocation;
import net.querz.nbt.tag.CompoundTag;

import java.util.Objects;

public class BlockEntity {

    private ResourceLocation id;
    private CompoundTag nbt;

    public BlockEntity(ResourceLocation id) {
        this(id, null);
    }

    public BlockEntity(ResourceLocation id, CompoundTag nbt) {
        this.id = Objects.requireNonNull(id);
        this.nbt = Objects.requireNonNullElse(nbt, new CompoundTag());
    }

    /**
     * Returns the namespaced ID of this {@link BlockEntity}.
     *
     * @return the namespaced ID of this {@link BlockEntity}
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Sets the namespaced ID of this {@link BlockEntity}.
     *
     * @param id the new namespaced ID for this {@link BlockEntity}
     */
    public void setId(ResourceLocation id) {
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

    public boolean isEmpty() {
        return id.toString().isEmpty() && nbt.size() == 0;
    }
}
