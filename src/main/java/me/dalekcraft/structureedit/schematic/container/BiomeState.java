package me.dalekcraft.structureedit.schematic.container;

import com.google.common.base.Splitter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
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
        this.id = id;
    }
}
