package me.dalekcraft.structureedit.util;

import javafx.scene.paint.Color;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.drawing.FoliageColor;
import me.dalekcraft.structureedit.drawing.GrassColor;
import me.dalekcraft.structureedit.drawing.WaterColor;
import me.dalekcraft.structureedit.schematic.container.BiomeState;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Utility to help find the tint of a given {@link BlockState} in a given {@link BiomeState}.
 */
public final class TintHelper {

    public static final Color DEFAULT_TINT = Color.WHITE;

    private TintHelper() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Color getTint(@NotNull BlockState blockState, BiomeState biomeState) {
        ResourceLocation namespacedId = blockState.getId();
        Map<String, String> properties = blockState.getProperties();
        switch (namespacedId.toString()) {
            case "minecraft:redstone_wire" -> {
                int power = 0;
                if (properties.containsKey("power")) {
                    try {
                        power = Integer.parseInt(properties.get("power"));
                    } catch (NumberFormatException ignored) {
                    }
                }
                switch (power) {
                    case 1 -> {
                        return Color.valueOf("#6F0000");
                    }
                    case 2 -> {
                        return Color.valueOf("#790000");
                    }
                    case 3 -> {
                        return Color.valueOf("#820000");
                    }
                    case 4 -> {
                        return Color.valueOf("#8C0000");
                    }
                    case 5 -> {
                        return Color.valueOf("#970000");
                    }
                    case 6 -> {
                        return Color.valueOf("#A10000");
                    }
                    case 7 -> {
                        return Color.valueOf("#AB0000");
                    }
                    case 8 -> {
                        return Color.valueOf("#B50000");
                    }
                    case 9 -> {
                        return Color.valueOf("#BF0000");
                    }
                    case 10 -> {
                        return Color.valueOf("#CA0000");
                    }
                    case 11 -> {
                        return Color.valueOf("#D30000");
                    }
                    case 12 -> {
                        return Color.valueOf("#DD0000");
                    }
                    case 13 -> {
                        return Color.valueOf("#E70600");
                    }
                    case 14 -> {
                        return Color.valueOf("#F11B00");
                    }
                    case 15 -> {
                        return Color.valueOf("#FC3100");
                    }
                    default -> { // 0
                        return Color.valueOf("#4B0000");
                    }
                }
            }
            case "minecraft:grass_block", "minecraft:grass", "minecraft:tall_grass", "minecraft:fern", "minecraft:large_fern", "minecraft:potted_fern", "minecraft:sugar_cane" -> {
                ResourceLocation biomeId = biomeState.getId();
                String biomeName = biomeId.getPath();
                try {
                    return GrassColor.valueOf(biomeName).getColor();
                } catch (IllegalArgumentException e) {
                    return GrassColor.OCEAN.getColor();
                }
            }
            case "minecraft:oak_leaves", "minecraft:dark_oak_leaves", "minecraft:jungle_leaves", "minecraft:acacia_leaves", "minecraft:mangrove_leaves", "minecraft:vine" -> {
                ResourceLocation biomeId = biomeState.getId();
                String biomeName = biomeId.getPath();
                try {
                    return FoliageColor.valueOf(biomeName).getColor();
                } catch (IllegalArgumentException e) {
                    return FoliageColor.OCEAN.getColor();
                }
            }
            case "minecraft:water", "minecraft:water_cauldron" -> {
                ResourceLocation biomeId = biomeState.getId();
                String biomeName = biomeId.getPath();
                try {
                    return WaterColor.valueOf(biomeName).getColor();
                } catch (IllegalArgumentException e) {
                    return WaterColor.DEFAULT.getColor();
                }
            }
            case "minecraft:birch_leaves" -> {
                return Color.valueOf("#80A755");
            }
            case "minecraft:spruce_leaves" -> {
                return Color.valueOf("#619961");
            }
            case "minecraft:lily_pad" -> {
                return Color.valueOf("#208030");
            }
            default -> {
                return DEFAULT_TINT;
            }
        }
    }
}
