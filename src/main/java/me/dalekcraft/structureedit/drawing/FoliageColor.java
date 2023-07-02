package me.dalekcraft.structureedit.drawing;

import javafx.scene.paint.Color;

/**
 * Colors used for tinting foliage in different biomes.
 *
 * @see <a href="https://minecraft.fandom.com/wiki/Block_colors#Foliage_colors">Foliage Colors</a>
 */
@SuppressWarnings("unused")
public enum FoliageColor {

    BADLANDS("#9E814D"),
    ERODED_BADLANDS("#9E814D"),
    WOODED_BADLANDS("#9E814D"),
    DESERT("#AEA42A"),
    SAVANNA("#AEA42A"),
    SAVANNA_PLATEAU("#AEA42A"),
    WINDSWEPT_SAVANNA("#AEA42A"),
    NETHER_WASTES("#AEA42A"),
    SOUL_SAND_VALLEY("#AEA42A"),
    CRIMSON_FOREST("#AEA42A"),
    WARPED_FOREST("#AEA42A"),
    BASALT_DELTAS("#AEA42A"),
    STONY_PEAKS("#82AC1E"),
    JUNGLE("#30BB0B"),
    BAMBOO_JUNGLE("#30BB0B"),
    SPARSE_JUNGLE("#3EB80F"),
    MUSHROOM_FIELDS("#2BBB0F"),
    SWAMP("#6A7039"),
    PLAINS("#77AB2F"),
    SUNFLOWER_PLAINS("#77AB2F"),
    BEACH("#77AB2F"),
    DRIPSTONE_CAVES("#77AB2F"),
    FOREST("#59AE30"),
    FLOWER_FOREST("#59AE30"),
    DARK_FOREST("#59AE30"),
    BIRCH_FOREST("#6BA941"),
    OLD_GROWTH_BIRCH_FOREST("#6BA941"),
    OCEAN("#71A74D"),
    DEEP_OCEAN("#71A74D"),
    WARM_OCEAN("#71A74D"),
    LUKEWARM_OCEAN("#71A74D"),
    DEEP_LUKEWARM_OCEAN("#71A74D"),
    COLD_OCEAN("#71A74D"),
    DEEP_COLD_OCEAN("#71A74D"),
    DEEP_FROZEN_OCEAN("#71A74D"),
    RIVER("#71A74D"),
    LUSH_CAVES("#71A74D"),
    THE_END("#71A74D"),
    SMALL_END_ISLANDS("#71A74D"),
    END_BARRENS("#71A74D"),
    END_MIDLANDS("#71A74D"),
    END_HIGHLANDS("#71A74D"),
    THE_VOID("#71A74D"),
    MEADOW("#63A948"),
    OLD_GROWTH_PINE_TAIGA("#68A55F"),
    TAIGA("#68A464"),
    OLD_GROWTH_SPRUCE_TAIGA("#68A464"),
    WINDSWEPT_HILLS("#6DA36B"),
    WINDSWEPT_GRAVELLY_HILLS("#6DA36B"),
    WINDSWEPT_FOREST("#6DA36B"),
    STONY_SHORE("#6DA36B"),
    SNOWY_BEACH("#64A278"),
    SNOWY_PLAINS("#60A17B"),
    ICE_SPIKES("#60A17B"),
    SNOWY_TAIGA("#60A17B"),
    FROZEN_OCEAN("#60A17B"),
    FROZEN_RIVER("#60A17B"),
    GROVE("#60A17B"),
    SNOWY_SLOPES("#60A17B"),
    FROZEN_PEAKS("#60A17B"),
    JAGGED_PEAKS("#60A17B");

    private final Color color;

    FoliageColor(String hex) {
        color = Color.valueOf(hex);
    }

    public Color getColor() {
        return color;
    }
}
