package me.dalekcraft.structureedit.schematic.container;

import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class Entity {

    private final double[] position = new double[3];
    private String id;
    private CompoundTag nbt;

    public Entity(String id) {
        this(id, null);
    }

    public Entity(String id, CompoundTag nbt) {
        this.id = Objects.requireNonNull(id);
        this.nbt = Objects.requireNonNullElse(nbt, new CompoundTag());
    }

    /**
     * Returns the position of this {@link Entity}.
     *
     * @return the position of this {@link Entity}
     */
    public double[] getPosition() {
        return position;
    }

    /**
     * Sets the position of this {@link Entity}.
     *
     * @param position the new x position for this {@link Entity}
     */
    public void setPosition(double @NotNull [] position) {
        setPosition(position[0], position[1], position[2]);
    }

    /**
     * Sets the position of this {@link Entity}.
     *
     * @param x the new x position for this {@link Entity}
     * @param y the new y position for this {@link Entity}
     * @param z the new z position for this {@link Entity}
     */
    public void setPosition(double x, double y, double z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    /**
     * Returns the namespaced ID of this {@link Entity}.
     *
     * @return the namespaced ID of this {@link Entity}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the namespaced ID of this {@link Entity}.
     *
     * @param id the new namespaced ID for this {@link Entity}
     */
    public void setId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Returns the NBT of this {@link Entity}.
     *
     * @return the NBT of this {@link Entity}
     */
    public CompoundTag getNbt() {
        return nbt;
    }

    /**
     * Sets the NBT of this {@link Entity}.
     *
     * @param nbt the new NBT for this {@link Entity}
     */
    public void setNbt(CompoundTag nbt) {
        this.nbt = Objects.requireNonNullElse(nbt, new CompoundTag());
    }

    @Override
    public String toString() {
        return "Entity{" + "position=" + Arrays.toString(position) + ", id='" + id + '\'' + '}';
    }
}
