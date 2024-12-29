package me.dalekcraft.structureedit.drawing;

import javafx.scene.paint.Color;

/**
 * Colors used for tinting water in different biomes.
 *
 * @see <a href="https://minecraft.wiki/w/Block_colors#Water_colors">Water Colors</a>
 */
@SuppressWarnings("unused")
public enum WaterColor {

    DEFAULT("#3576E4"),
    COLD_OCEAN("#3D57D6"),
    DEEP_COLD_OCEAN("#3D57D6"),
    SNOWY_TAIGA("#3D57D6"),
    SNOWY_BEACH("#3D57D6"),
    FROZEN_OCEAN("#3938C9"),
    DEEP_FROZEN_OCEAN("#3938C9"),
    FROZEN_RIVER("#3938C9"),
    LUKEWARM_OCEAN("#45ADF2"),
    DEEP_LUKEWARM_OCEAN("#45ADF2"),
    SWAMP("#617B64"),
    WARM_OCEAN("#43D5EE"),
    MEADOW("#0E4ECF");

    private final Color color;

    WaterColor(String hex) {
        color = Color.valueOf(hex);
    }

    public Color getColor() {
        return color;
    }
}
