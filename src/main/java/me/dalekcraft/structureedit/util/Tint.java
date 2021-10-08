package me.dalekcraft.structureedit.util;

import me.dalekcraft.structureedit.drawing.Block;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.StringTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final class Tint {

    @Contract(value = " -> fail", pure = true)
    private Tint() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Color getTint(String namespacedId, CompoundTag properties) {
        Block block = Block.fromId(namespacedId);
        switch (block) {
            case REDSTONE_WIRE -> {
                int power = 0;
                if (properties.containsKey("power") && properties.get("power") instanceof IntTag intTag) {
                    power = intTag.asInt();
                } else if (properties.containsKey("power") && properties.get("power") instanceof StringTag stringTag) {
                    power = Integer.parseInt(stringTag.getValue());
                }
                switch (power) {
                    case 0 -> {
                        return Color.decode("#4B0000");
                    }
                    case 1 -> {
                        return Color.decode("#6F0000");
                    }
                    case 2 -> {
                        return Color.decode("#790000");
                    }
                    case 3 -> {
                        return Color.decode("#820000");
                    }
                    case 4 -> {
                        return Color.decode("#8C0000");
                    }
                    case 5 -> {
                        return Color.decode("#970000");
                    }
                    case 6 -> {
                        return Color.decode("#A10000");
                    }
                    case 7 -> {
                        return Color.decode("#AB0000");
                    }
                    case 8 -> {
                        return Color.decode("#B50000");
                    }
                    case 9 -> {
                        return Color.decode("#BF0000");
                    }
                    case 10 -> {
                        return Color.decode("#CA0000");
                    }
                    case 11 -> {
                        return Color.decode("#D30000");
                    }
                    case 12 -> {
                        return Color.decode("#DD0000");
                    }
                    case 13 -> {
                        return Color.decode("#E70600");
                    }
                    case 14 -> {
                        return Color.decode("#F11B00");
                    }
                    case 15 -> {
                        return Color.decode("#FC3100");
                    }
                }
            }
            case GRASS_BLOCK, GRASS, TALL_GRASS, FERN, LARGE_FERN, POTTED_FERN, SUGAR_CANE -> {
                return Color.decode("#91BD59");
            }
            case OAK_LEAVES, DARK_OAK_LEAVES, JUNGLE_LEAVES, ACACIA_LEAVES, VINE -> {
                return Color.decode("#77AB2F");
            }
            case WATER, WATER_CAULDRON -> {
                return Color.decode("#3F76E4");
            }
            case BIRCH_LEAVES -> {
                return Color.decode("#80A755");
            }
            case SPRUCE_LEAVES -> {
                return Color.decode("#619961");
            }
            case LILY_PAD -> {
                return Color.decode("#208030");
            }
        }
        return new Color(255, 255, 255, 255);
    }
}
