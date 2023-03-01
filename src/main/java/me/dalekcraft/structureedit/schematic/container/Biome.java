package me.dalekcraft.structureedit.schematic.container;

public class Biome {

    private int biomeStateIndex;

    public Biome(int biomeStateIndex) {
        this.biomeStateIndex = biomeStateIndex;
    }

    /**
     * Returns the {@link BiomeState} of this {@link Biome}.
     *
     * @return the {@link BiomeState} of this {@link Biome}
     */
    public int getBiomeStateIndex() {
        return biomeStateIndex;
    }

    /**
     * Sets the {@link BiomeState} of this {@link Biome}.
     *
     * @param biomeStateIndex the new {@link BiomeState} for this {@link Biome}
     */
    public void setBiomeStateIndex(int biomeStateIndex) {
        this.biomeStateIndex = biomeStateIndex;
    }
}
