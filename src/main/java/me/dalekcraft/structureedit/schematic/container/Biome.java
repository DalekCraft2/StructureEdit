package me.dalekcraft.structureedit.schematic.container;

public class Biome {

    private BiomeState biomeState;

    /**
     * Returns the namespaced ID of this {@link Biome}.
     *
     * @return the namespaced ID of this {@link Biome}
     */
    public String getId() {
        return biomeState.getId();
    }

    /**
     * Sets the namespaced ID of this {@link Biome}.
     *
     * @param id the new namespaced ID for this {@link Biome}
     */
    public void setId(String id) {
        biomeState.setId(id);
    }


    /**
     * Returns the {@link BiomeState} of this {@link Biome}.
     *
     * @return the {@link BiomeState} of this {@link Biome}
     */
    public BiomeState getBiomeState() {
        return biomeState;
    }

    /**
     * Sets the {@link BiomeState} of this {@link Biome}.
     *
     * @param biomeState the new {@link BiomeState} for this {@link Biome}
     */
    public void setBiomeState(BiomeState biomeState) {
        this.biomeState = biomeState;
    }
}
