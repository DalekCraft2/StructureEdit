package me.dalekcraft.structureedit.schematic.container;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BiomeState {

    @NotNull
    private String id;

    public BiomeState(@NotNull String id) {
        this.id = id;
    }

    /**
     * Returns the namespaced ID of this {@link BiomeState}.
     *
     * @return the namespaced ID of this {@link BiomeState}
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Sets the namespaced ID of this {@link BiomeState}.
     *
     * @param id the new namespaced ID for this {@link BiomeState}
     */
    public void setId(@NotNull String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BiomeState casted = (BiomeState) obj;
        return id.equals(casted.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
