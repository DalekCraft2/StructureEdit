package me.dalekcraft.structureedit.util;

import me.dalekcraft.structureedit.assets.ResourceLocation;

import java.util.List;

public final class Constants {

    public static final ResourceLocation DEFAULT_BLOCK = new ResourceLocation("air");
    public static final ResourceLocation DEFAULT_BIOME = new ResourceLocation("ocean");
    public static final ResourceLocation WATERLOGGED_BLOCK = new ResourceLocation("block/water");

    public static final List<String> NO_COPY_ENTITY_NBT_FIELDS;

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

    /**
     * The DataVersion for Minecraft 1.19
     */
    public static final int DATA_VERSION_MC_1_19 = 3105;

    /**
     * The DataVersion for Minecraft 1.20
     */
    public static final int DATA_VERSION_MC_1_20 = 3463;

    /**
     * The DataVersion for Minecraft 1.21
     */
    public static final int DATA_VERSION_MC_1_21 = 3953;

    /**
     * The DataVersion for Minecraft 1.21.3
     */
    public static final int DATA_VERSION_MC_1_21_3 = 4082;

    /**
     * The DataVersion for Minecraft 1.21.4
     */
    public static final int DATA_VERSION_MC_1_21_4 = 4189;

    public static final double EDITOR_TILE_SIZE = 30.0;

    static {
        NO_COPY_ENTITY_NBT_FIELDS = List.of("UUIDLeast", "UUIDMost", "UUID", // Bukkit and Vanilla
                "WorldUUIDLeast", "WorldUUIDMost", // Bukkit and Vanilla
                "PersistentIDMSB", "PersistentIDLSB" // Forge
        );
    }

    private Constants() {
        throw new UnsupportedOperationException();
    }
}
