package me.dalekcraft.structureedit.schematic;

public interface VersionedSchematic extends Schematic {

    /**
     * Returns the Minecraft data version of this {@link Schematic}.
     *
     * @return the Minecraft data version of this {@link Schematic}
     */
    int getDataVersion();
}
