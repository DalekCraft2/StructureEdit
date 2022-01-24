package me.dalekcraft.structureedit.util;

import me.dalekcraft.structureedit.assets.ResourceLocation;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Constants {

    public static final ResourceLocation DEFAULT_BLOCK = new ResourceLocation("air");
    public static final ResourceLocation DEFAULT_BIOME = new ResourceLocation("ocean");
    public static final ResourceLocation WATERLOGGED_BLOCK = new ResourceLocation("water");

    public static final List<String> NO_COPY_ENTITY_NBT_FIELDS;

    static {
        NO_COPY_ENTITY_NBT_FIELDS = Collections.unmodifiableList(Arrays.asList(
                "UUIDLeast", "UUIDMost", "UUID", // Bukkit and Vanilla
                "WorldUUIDLeast", "WorldUUIDMost", // Bukkit and Vanilla
                "PersistentIDMSB", "PersistentIDLSB" // Forge
        ));
    }

    /**
     * The DataVersion for Minecraft 1.13
     */
    public static final int DATA_VERSION_MC_1_13 = 1519;

    /**
     * The DataVersion for Minecraft 1.13.2
     */
    public static final int DATA_VERSION_MC_1_13_2 = 1631;

    /**
     * The DataVersion for Minecraft 1.14
     */
    public static final int DATA_VERSION_MC_1_14 = 1952;

    /**
     * The DataVersion for Minecraft 1.15
     */
    public static final int DATA_VERSION_MC_1_15 = 2225;

    /**
     * The DataVersion for Minecraft 1.16
     */
    public static final int DATA_VERSION_MC_1_16 = 2566;

    /**
     * The DataVersion for Minecraft 1.17
     */
    public static final int DATA_VERSION_MC_1_17 = 2724;

    /**
     * The DataVersion for Minecraft 1.18
     */
    public static final int DATA_VERSION_MC_1_18 = 2860;

    private Constants() {
    }
}
