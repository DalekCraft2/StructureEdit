package me.dalekcraft.structureedit.drawing;

import javafx.scene.paint.Color;

/**
 * Colors used for tinting grass in different biomes.
 *
 * @see <a href="https://minecraft.wiki/w/Block_colors#Grass_colors">Grass Colors</a>
 */
@SuppressWarnings("unused")
public enum GrassColor {

    BADLANDS("#90814D"),
    ERODED_BADLANDS("#90814D"),
    WOODED_BADLANDS("#90814D"),
    DESERT("#BFB755"),
    SAVANNA("#BFB755"),
    SAVANNA_PLATEAU("#BFB755"),
    WINDSWEPT_SAVANNA("#BFB755"),
    NETHER_WASTES("#BFB755"),
    SOUL_SAND_VALLEY("#BFB755"),
    CRIMSON_FOREST("#BFB755"),
    WARPED_FOREST("#BFB755"),
    BASALT_DELTAS("#BFB755"),
    STONY_PEAKS("9ABE4B"),
    JUNGLE("#59C93C"),
    BAMBOO_JUNGLE("#59C93C"),
    SPARSE_JUNGLE("#64C73F"),
    MUSHROOM_FIELDS("#55C93F"),
    SWAMP("#6A7039"),
    PLAINS("#91BD59"),
    SUNFLOWER_PLAINS("#91BD59"),
    BEACH("#91BD59"),
    DRIPSTONE_CAVES("#91BD59"),
    FOREST("#79C05A"),
    FLOWER_FOREST("#79C05A"),
    DARK_FOREST("#507A32"),
    BIRCH_FOREST("#88BB67"),
    OLD_GROWTH_BIRCH_FOREST("#88BB67"),
    OCEAN("#8EB971"),
    DEEP_OCEAN("#8EB971"),
    WARM_OCEAN("#8EB971"),
    LUKEWARM_OCEAN("#8EB971"),
    DEEP_LUKEWARM_OCEAN("#8EB971"),
    COLD_OCEAN("#8EB971"),
    DEEP_COLD_OCEAN("#8EB971"),
    DEEP_FROZEN_OCEAN("#8EB971"),
    RIVER("#8EB971"),
    LUSH_CAVES("#8EB971"),
    THE_END("#8EB971"),
    SMALL_END_ISLANDS("#8EB971"),
    END_BARRENS("#8EB971"),
    END_MIDLANDS("#8EB971"),
    END_HIGHLANDS("#8EB971"),
    THE_VOID("#8EB971"),
    MEADOW("#83BB6D"),
    OLD_GROWTH_PINE_TAIGA("#86B87F"),
    TAIGA("#86B783"),
    OLD_GROWTH_SPRUCE_TAIGA("#86B783"),
    WINDSWEPT_HILLS("#8AB689"),
    WINDSWEPT_GRAVELLY_HILLS("#8AB689"),
    WINDSWEPT_FOREST("#8AB689"),
    STONY_SHORE("#8AB689"),
    SNOWY_BEACH("#83B593"),
    SNOWY_PLAINS("#80B497"),
    ICE_SPIKES("#80B497"),
    SNOWY_TAIGA("#80B497"),
    FROZEN_OCEAN("#80B497"),
    FROZEN_RIVER("#80B497"),
    GROVE("#80B497"),
    SNOWY_SLOPES("#80B497"),
    FROZEN_PEAKS("#80B497"),
    JAGGED_PEAKS("#80B497");

    private final Color color;

    GrassColor(String hex) {
        color = Color.valueOf(hex);
    }

    public Color getColor() {
        return color;
    }
}
