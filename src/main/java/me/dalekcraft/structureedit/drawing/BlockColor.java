/*
 * Copyright (C) 2015 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.dalekcraft.structureedit.drawing;

import javafx.scene.paint.Color;


/**
 * An enum of all materials accepted by the official server and client.
 *
 * @see <a href="https://minecraft.fandom.com/wiki/Map_item_format">Map Item Format</a>
 */
@SuppressWarnings("unused")
public enum BlockColor {

    // TODO Possibly use this enum to make default block states for blocks.
    // TODO Should I order these how they are ordered in Minecraft's code so it's easier to add new entries?

    ACACIA_BUTTON(MapColor.COLOR_ORANGE),
    ACACIA_DOOR(MapColor.COLOR_ORANGE),
    ACACIA_FENCE(MapColor.COLOR_ORANGE),
    ACACIA_FENCE_GATE(MapColor.COLOR_ORANGE),
    ACACIA_LEAVES(MapColor.PLANT),
    ACACIA_LOG(MapColor.STONE),
    ACACIA_PLANKS(MapColor.COLOR_ORANGE),
    ACACIA_PRESSURE_PLATE(MapColor.COLOR_ORANGE),
    ACACIA_SAPLING(MapColor.PLANT),
    ACACIA_SIGN(MapColor.COLOR_ORANGE),
    ACACIA_SLAB(MapColor.COLOR_ORANGE),
    ACACIA_STAIRS(MapColor.COLOR_ORANGE),
    ACACIA_TRAPDOOR(MapColor.COLOR_ORANGE),
    ACACIA_WALL_SIGN(MapColor.COLOR_ORANGE),
    ACACIA_WOOD(MapColor.COLOR_GRAY),
    ACTIVATOR_RAIL(MapColor.METAL),
    AIR(MapColor.TRANSPARENT),
    ALLIUM(MapColor.PLANT),
    AMETHYST_BLOCK(MapColor.COLOR_PURPLE),
    AMETHYST_CLUSTER(MapColor.COLOR_PURPLE),
    ANCIENT_DEBRIS(MapColor.COLOR_BLACK),
    ANDESITE(MapColor.STONE),
    ANDESITE_SLAB(MapColor.STONE),
    ANDESITE_STAIRS(MapColor.STONE),
    ANDESITE_WALL(MapColor.STONE),
    ANVIL(MapColor.METAL),
    ATTACHED_MELON_STEM(MapColor.PLANT),
    ATTACHED_PUMPKIN_STEM(MapColor.PLANT),
    AZALEA(MapColor.PLANT),
    AZALEA_LEAVES(MapColor.PLANT),
    AZURE_BLUET(MapColor.PLANT),
    BAMBOO(MapColor.PLANT),
    BAMBOO_SAPLING(MapColor.WOOD),
    BARREL(MapColor.WOOD),
    BARRIER(MapColor.TRANSPARENT),
    BASALT(MapColor.COLOR_BLACK),
    BEACON(MapColor.DIAMOND),
    BEDROCK(MapColor.STONE),
    BEE_NEST(MapColor.COLOR_YELLOW),
    BEEHIVE(MapColor.WOOD),
    BEETROOTS(MapColor.PLANT),
    BELL(MapColor.GOLD),
    BIG_DRIPLEAF(MapColor.PLANT),
    BIG_DRIPLEAF_STEM(MapColor.PLANT),
    BIRCH_BUTTON(MapColor.SAND),
    BIRCH_DOOR(MapColor.SAND),
    BIRCH_FENCE(MapColor.SAND),
    BIRCH_FENCE_GATE(MapColor.SAND),
    BIRCH_LEAVES(MapColor.PLANT),
    BIRCH_LOG(MapColor.QUARTZ),
    BIRCH_PLANKS(MapColor.SAND),
    BIRCH_PRESSURE_PLATE(MapColor.SAND),
    BIRCH_SAPLING(MapColor.PLANT),
    BIRCH_SIGN(MapColor.SAND),
    BIRCH_SLAB(MapColor.SAND),
    BIRCH_STAIRS(MapColor.SAND),
    BIRCH_TRAPDOOR(MapColor.SAND),
    BIRCH_WALL_SIGN(MapColor.SAND),
    BIRCH_WOOD(MapColor.SAND),
    BLACK_BANNER(MapColor.WOOD),
    BLACK_BED(MapColor.COLOR_BLACK),
    BLACK_CANDLE(MapColor.COLOR_BLACK),
    BLACK_CANDLE_CAKE(MapColor.QUARTZ),
    BLACK_CARPET(MapColor.COLOR_BLACK),
    BLACK_CONCRETE(MapColor.COLOR_BLACK),
    BLACK_CONCRETE_POWDER(MapColor.COLOR_BLACK),
    BLACK_GLAZED_TERRACOTTA(MapColor.COLOR_BLACK),
    BLACK_SHULKER_BOX(MapColor.COLOR_BLACK),
    BLACK_STAINED_GLASS(MapColor.COLOR_BLACK),
    BLACK_STAINED_GLASS_PANE(MapColor.COLOR_BLACK),
    BLACK_TERRACOTTA(MapColor.TERRACOTTA_BLACK),
    BLACK_WALL_BANNER(MapColor.WOOD),
    BLACK_WOOL(MapColor.COLOR_BLACK),
    BLACKSTONE(MapColor.COLOR_BLACK),
    BLACKSTONE_SLAB(MapColor.COLOR_BLACK),
    BLACKSTONE_STAIRS(MapColor.COLOR_BLACK),
    BLACKSTONE_WALL(MapColor.COLOR_BLACK),
    BLAST_FURNACE(MapColor.STONE),
    BLUE_BANNER(MapColor.WOOD),
    BLUE_BED(MapColor.COLOR_BLUE),
    BLUE_CANDLE(MapColor.COLOR_BLUE),
    BLUE_CANDLE_CAKE(MapColor.QUARTZ),
    BLUE_CARPET(MapColor.COLOR_BLUE),
    BLUE_CONCRETE(MapColor.COLOR_BLUE),
    BLUE_CONCRETE_POWDER(MapColor.COLOR_BLUE),
    BLUE_GLAZED_TERRACOTTA(MapColor.COLOR_BLUE),
    BLUE_ICE(MapColor.ICE),
    BLUE_ORCHID(MapColor.PLANT),
    BLUE_SHULKER_BOX(MapColor.COLOR_BLUE),
    BLUE_STAINED_GLASS(MapColor.COLOR_BLUE),
    BLUE_STAINED_GLASS_PANE(MapColor.COLOR_BLUE),
    BLUE_TERRACOTTA(MapColor.TERRACOTTA_BLUE),
    BLUE_WALL_BANNER(MapColor.WOOD),
    BLUE_WOOL(MapColor.COLOR_BLUE),
    BONE_BLOCK(MapColor.SAND),
    BOOKSHELF(MapColor.WOOD),
    BRAIN_CORAL(MapColor.COLOR_PINK),
    BRAIN_CORAL_BLOCK(MapColor.COLOR_PINK),
    BRAIN_CORAL_FAN(MapColor.COLOR_PINK),
    BRAIN_CORAL_WALL_FAN(MapColor.COLOR_PINK),
    BREWING_STAND(MapColor.METAL),
    BRICK_SLAB(MapColor.COLOR_RED),
    BRICK_STAIRS(MapColor.COLOR_RED),
    BRICK_WALL(MapColor.COLOR_RED),
    BRICKS(MapColor.COLOR_RED),
    BROWN_BANNER(MapColor.WOOD),
    BROWN_BED(MapColor.COLOR_BROWN),
    BROWN_CANDLE(MapColor.COLOR_BROWN),
    BROWN_CANDLE_CAKE(MapColor.QUARTZ),
    BROWN_CARPET(MapColor.COLOR_BROWN),
    BROWN_CONCRETE(MapColor.COLOR_BROWN),
    BROWN_CONCRETE_POWDER(MapColor.COLOR_BROWN),
    BROWN_GLAZED_TERRACOTTA(MapColor.COLOR_BROWN),
    BROWN_MUSHROOM(MapColor.COLOR_BROWN),
    BROWN_MUSHROOM_BLOCK(MapColor.DIRT),
    BROWN_SHULKER_BOX(MapColor.COLOR_BROWN),
    BROWN_STAINED_GLASS(MapColor.COLOR_BROWN),
    BROWN_STAINED_GLASS_PANE(MapColor.COLOR_BROWN),
    BROWN_TERRACOTTA(MapColor.TERRACOTTA_BROWN),
    BROWN_WALL_BANNER(MapColor.WOOD),
    BROWN_WOOL(MapColor.COLOR_BROWN),
    BUBBLE_COLUMN(MapColor.WATER),
    BUBBLE_CORAL(MapColor.COLOR_PURPLE),
    BUBBLE_CORAL_BLOCK(MapColor.COLOR_PURPLE),
    BUBBLE_CORAL_FAN(MapColor.COLOR_PURPLE),
    BUBBLE_CORAL_WALL_FAN(MapColor.COLOR_PURPLE),
    BUDDING_AMETHYST(MapColor.COLOR_PURPLE),
    CACTUS(MapColor.PLANT),
    CAKE(MapColor.QUARTZ),
    CALCITE(MapColor.TERRACOTTA_WHITE),
    CAMPFIRE(MapColor.PODZOL),
    CANDLE(MapColor.SAND),
    CANDLE_CAKE(MapColor.QUARTZ),
    CARROTS(MapColor.PLANT),
    CARTOGRAPHY_TABLE(MapColor.WOOD),
    CARVED_PUMPKIN(MapColor.COLOR_ORANGE),
    CAULDRON(MapColor.STONE),
    CAVE_AIR(MapColor.TRANSPARENT),
    CAVE_VINES(MapColor.PLANT),
    CAVE_VINES_PLANT(MapColor.PLANT),
    CHAIN(MapColor.METAL),
    CHAIN_COMMAND_BLOCK(MapColor.COLOR_GREEN),
    CHEST(MapColor.WOOD),
    CHIPPED_ANVIL(MapColor.METAL),
    CHISELED_DEEPSLATE(MapColor.DEEPSLATE),
    CHISELED_NETHER_BRICKS(MapColor.NETHER),
    CHISELED_POLISHED_BLACKSTONE(MapColor.COLOR_BLACK),
    CHISELED_QUARTZ_BLOCK(MapColor.QUARTZ),
    CHISELED_RED_SANDSTONE(MapColor.COLOR_ORANGE),
    CHISELED_SANDSTONE(MapColor.SAND),
    CHISELED_STONE_BRICKS(MapColor.STONE),
    CHORUS_FLOWER(MapColor.COLOR_PURPLE),
    CHORUS_PLANT(MapColor.COLOR_PURPLE),
    CLAY(MapColor.CLAY),
    COAL_BLOCK(MapColor.COLOR_BLACK),
    COAL_ORE(MapColor.STONE),
    COARSE_DIRT(MapColor.DIRT),
    COBBLED_DEEPSLATE(MapColor.DEEPSLATE),
    COBBLED_DEEPSLATE_SLAB(MapColor.DEEPSLATE),
    COBBLED_DEEPSLATE_STAIRS(MapColor.DEEPSLATE),
    COBBLED_DEEPSLATE_WALL(MapColor.DEEPSLATE),
    COBBLESTONE(MapColor.STONE),
    COBBLESTONE_SLAB(MapColor.STONE),
    COBBLESTONE_STAIRS(MapColor.STONE),
    COBBLESTONE_WALL(MapColor.STONE),
    COBWEB(MapColor.WOOL),
    COCOA(MapColor.PLANT),
    COMMAND_BLOCK(MapColor.COLOR_BROWN),
    COMPARATOR(MapColor.STONE),
    COMPOSTER(MapColor.WOOD),
    CONDUIT(MapColor.DIAMOND),
    COPPER_BLOCK(MapColor.COLOR_ORANGE),
    COPPER_ORE(MapColor.STONE),
    CORNFLOWER(MapColor.PLANT),
    CRACKED_DEEPSLATE_BRICKS(MapColor.DEEPSLATE),
    CRACKED_DEEPSLATE_TILES(MapColor.DEEPSLATE),
    CRACKED_NETHER_BRICKS(MapColor.NETHER),
    CRACKED_POLISHED_BLACKSTONE_BRICKS(MapColor.COLOR_BLACK),
    CRACKED_STONE_BRICKS(MapColor.STONE),
    CRAFTING_TABLE(MapColor.WOOD),
    CREEPER_HEAD(MapColor.COLOR_LIGHT_GREEN),
    CREEPER_WALL_HEAD(MapColor.COLOR_LIGHT_GREEN),
    CRIMSON_BUTTON(MapColor.CRIMSON_STEM),
    CRIMSON_DOOR(MapColor.CRIMSON_STEM),
    CRIMSON_FENCE(MapColor.CRIMSON_STEM),
    CRIMSON_FENCE_GATE(MapColor.CRIMSON_STEM),
    CRIMSON_FUNGUS(MapColor.NETHER),
    CRIMSON_HYPHAE(MapColor.CRIMSON_HYPHAE),
    CRIMSON_NYLIUM(MapColor.CRIMSON_NYLIUM),
    CRIMSON_PLANKS(MapColor.CRIMSON_STEM),
    CRIMSON_PRESSURE_PLATE(MapColor.CRIMSON_STEM),
    CRIMSON_ROOTS(MapColor.NETHER),
    CRIMSON_SIGN(MapColor.CRIMSON_STEM),
    CRIMSON_SLAB(MapColor.CRIMSON_STEM),
    CRIMSON_STAIRS(MapColor.CRIMSON_STEM),
    CRIMSON_STEM(MapColor.CRIMSON_STEM),
    CRIMSON_TRAPDOOR(MapColor.CRIMSON_STEM),
    CRIMSON_WALL_SIGN(MapColor.CRIMSON_STEM),
    CRYING_OBSIDIAN(MapColor.COLOR_BLACK),
    CUT_COPPER(MapColor.COLOR_ORANGE),
    CUT_COPPER_SLAB(MapColor.COLOR_ORANGE),
    CUT_COPPER_STAIRS(MapColor.COLOR_ORANGE),
    CUT_RED_SANDSTONE(MapColor.COLOR_ORANGE),
    CUT_RED_SANDSTONE_SLAB(MapColor.COLOR_ORANGE),
    CUT_SANDSTONE(MapColor.SAND),
    CUT_SANDSTONE_SLAB(MapColor.SAND),
    CYAN_BANNER(MapColor.WOOD),
    CYAN_BED(MapColor.COLOR_CYAN),
    CYAN_CANDLE(MapColor.COLOR_CYAN),
    CYAN_CANDLE_CAKE(MapColor.QUARTZ),
    CYAN_CARPET(MapColor.COLOR_CYAN),
    CYAN_CONCRETE(MapColor.COLOR_CYAN),
    CYAN_CONCRETE_POWDER(MapColor.COLOR_CYAN),
    CYAN_GLAZED_TERRACOTTA(MapColor.COLOR_CYAN),
    CYAN_SHULKER_BOX(MapColor.COLOR_CYAN),
    CYAN_STAINED_GLASS(MapColor.COLOR_CYAN),
    CYAN_STAINED_GLASS_PANE(MapColor.COLOR_CYAN),
    CYAN_TERRACOTTA(MapColor.TERRACOTTA_CYAN),
    CYAN_WALL_BANNER(MapColor.WOOD),
    CYAN_WOOL(MapColor.COLOR_CYAN),
    DAMAGED_ANVIL(MapColor.METAL),
    DANDELION(MapColor.PLANT),
    DARK_OAK_BUTTON(MapColor.COLOR_BROWN),
    DARK_OAK_DOOR(MapColor.COLOR_BROWN),
    DARK_OAK_FENCE(MapColor.COLOR_BROWN),
    DARK_OAK_FENCE_GATE(MapColor.COLOR_BROWN),
    DARK_OAK_LEAVES(MapColor.PLANT),
    DARK_OAK_LOG(MapColor.COLOR_BROWN),
    DARK_OAK_PLANKS(MapColor.COLOR_BROWN),
    DARK_OAK_PRESSURE_PLATE(MapColor.COLOR_BROWN),
    DARK_OAK_SAPLING(MapColor.PLANT),
    DARK_OAK_SIGN(MapColor.COLOR_BROWN),
    DARK_OAK_SLAB(MapColor.COLOR_BROWN),
    DARK_OAK_STAIRS(MapColor.COLOR_BROWN),
    DARK_OAK_TRAPDOOR(MapColor.COLOR_BROWN),
    DARK_OAK_WALL_SIGN(MapColor.COLOR_BROWN),
    DARK_OAK_WOOD(MapColor.COLOR_BROWN),
    DARK_PRISMARINE(MapColor.DIAMOND),
    DARK_PRISMARINE_SLAB(MapColor.DIAMOND),
    DARK_PRISMARINE_STAIRS(MapColor.DIAMOND),
    DAYLIGHT_DETECTOR(MapColor.WOOD),
    DEAD_BRAIN_CORAL(MapColor.COLOR_GRAY),
    DEAD_BRAIN_CORAL_BLOCK(MapColor.COLOR_GRAY),
    DEAD_BRAIN_CORAL_FAN(MapColor.COLOR_GRAY),
    DEAD_BRAIN_CORAL_WALL_FAN(MapColor.COLOR_GRAY),
    DEAD_BUBBLE_CORAL(MapColor.COLOR_GRAY),
    DEAD_BUBBLE_CORAL_BLOCK(MapColor.COLOR_GRAY),
    DEAD_BUBBLE_CORAL_FAN(MapColor.COLOR_GRAY),
    DEAD_BUBBLE_CORAL_WALL_FAN(MapColor.COLOR_GRAY),
    DEAD_BUSH(MapColor.WOOD),
    DEAD_FIRE_CORAL(MapColor.COLOR_GRAY),
    DEAD_FIRE_CORAL_BLOCK(MapColor.COLOR_GRAY),
    DEAD_FIRE_CORAL_FAN(MapColor.COLOR_GRAY),
    DEAD_FIRE_CORAL_WALL_FAN(MapColor.COLOR_GRAY),
    DEAD_HORN_CORAL(MapColor.COLOR_GRAY),
    DEAD_HORN_CORAL_BLOCK(MapColor.COLOR_GRAY),
    DEAD_HORN_CORAL_FAN(MapColor.COLOR_GRAY),
    DEAD_HORN_CORAL_WALL_FAN(MapColor.COLOR_GRAY),
    DEAD_TUBE_CORAL(MapColor.COLOR_GRAY),
    DEAD_TUBE_CORAL_BLOCK(MapColor.COLOR_GRAY),
    DEAD_TUBE_CORAL_FAN(MapColor.COLOR_GRAY),
    DEAD_TUBE_CORAL_WALL_FAN(MapColor.COLOR_GRAY),
    DEEPSLATE(MapColor.DEEPSLATE),
    DEEPSLATE_BRICK_SLAB(MapColor.DEEPSLATE),
    DEEPSLATE_BRICK_STAIRS(MapColor.DEEPSLATE),
    DEEPSLATE_BRICK_WALL(MapColor.DEEPSLATE),
    DEEPSLATE_BRICKS(MapColor.DEEPSLATE),
    DEEPSLATE_COAL_ORE(MapColor.DEEPSLATE),
    DEEPSLATE_COPPER_ORE(MapColor.DEEPSLATE),
    DEEPSLATE_DIAMOND_ORE(MapColor.DEEPSLATE),
    DEEPSLATE_EMERALD_ORE(MapColor.DEEPSLATE),
    DEEPSLATE_GOLD_ORE(MapColor.DEEPSLATE),
    DEEPSLATE_IRON_ORE(MapColor.DEEPSLATE),
    DEEPSLATE_LAPIS_ORE(MapColor.DEEPSLATE),
    DEEPSLATE_REDSTONE_ORE(MapColor.DEEPSLATE),
    DEEPSLATE_TILE_SLAB(MapColor.DEEPSLATE),
    DEEPSLATE_TILE_STAIRS(MapColor.DEEPSLATE),
    DEEPSLATE_TILE_WALL(MapColor.DEEPSLATE),
    DEEPSLATE_TILES(MapColor.DEEPSLATE),
    DETECTOR_RAIL(MapColor.METAL),
    DIAMOND_BLOCK(MapColor.DIAMOND),
    DIAMOND_ORE(MapColor.STONE),
    DIORITE(MapColor.QUARTZ),
    DIORITE_SLAB(MapColor.QUARTZ),
    DIORITE_STAIRS(MapColor.QUARTZ),
    DIORITE_WALL(MapColor.QUARTZ),
    DIRT(MapColor.DIRT),
    DIRT_PATH(MapColor.DIRT),
    DISPENSER(MapColor.STONE),
    DRAGON_EGG(MapColor.COLOR_BLACK),
    DRAGON_HEAD(MapColor.COLOR_BLACK),
    DRAGON_WALL_HEAD(MapColor.COLOR_BLACK),
    DRIED_KELP_BLOCK(MapColor.COLOR_GREEN),
    DRIPSTONE_BLOCK(MapColor.TERRACOTTA_BROWN),
    DROPPER(MapColor.STONE),
    EMERALD_BLOCK(MapColor.EMERALD),
    EMERALD_ORE(MapColor.STONE),
    ENCHANTING_TABLE(MapColor.COLOR_RED),
    END_GATEWAY(MapColor.COLOR_BLACK),
    END_PORTAL(MapColor.COLOR_BLACK),
    END_PORTAL_FRAME(MapColor.COLOR_GREEN),
    END_ROD(MapColor.QUARTZ),
    END_STONE(MapColor.SAND),
    END_STONE_BRICK_SLAB(MapColor.SAND),
    END_STONE_BRICK_STAIRS(MapColor.SAND),
    END_STONE_BRICK_WALL(MapColor.SAND),
    END_STONE_BRICKS(MapColor.SAND),
    ENDER_CHEST(MapColor.STONE),
    EXPOSED_COPPER(MapColor.TERRACOTTA_LIGHT_GRAY),
    EXPOSED_CUT_COPPER(MapColor.TERRACOTTA_LIGHT_GRAY),
    EXPOSED_CUT_COPPER_SLAB(MapColor.TERRACOTTA_LIGHT_GRAY),
    EXPOSED_CUT_COPPER_STAIRS(MapColor.TERRACOTTA_LIGHT_GRAY),
    FARMLAND(MapColor.DIRT),
    FERN(MapColor.PLANT),
    FIRE(MapColor.FIRE),
    FIRE_CORAL(MapColor.COLOR_RED),
    FIRE_CORAL_BLOCK(MapColor.COLOR_RED),
    FIRE_CORAL_FAN(MapColor.COLOR_RED),
    FIRE_CORAL_WALL_FAN(MapColor.COLOR_RED),
    FLETCHING_TABLE(MapColor.WOOD),
    FLOWER_POT(MapColor.COLOR_RED),
    FLOWERING_AZALEA(MapColor.PLANT),
    FLOWERING_AZALEA_LEAVES(MapColor.PLANT),
    FROGSPAWN(MapColor.TRANSPARENT),
    FROSTED_ICE(MapColor.ICE),
    FURNACE(MapColor.STONE),
    GILDED_BLACKSTONE(MapColor.COLOR_BLACK),
    GLASS(MapColor.TRANSPARENT),
    GLASS_PANE(MapColor.TRANSPARENT),
    GLOW_LICHEN(MapColor.GLOW_LICHEN),
    GLOWSTONE(MapColor.SAND),
    GOLD_BLOCK(MapColor.GOLD),
    GOLD_ORE(MapColor.STONE),
    GRANITE(MapColor.DIRT),
    GRANITE_SLAB(MapColor.DIRT),
    GRANITE_STAIRS(MapColor.DIRT),
    GRANITE_WALL(MapColor.DIRT),
    GRASS(MapColor.PLANT),
    GRASS_BLOCK(MapColor.GRASS),
    GRAVEL(MapColor.STONE),
    GRAY_BANNER(MapColor.WOOD),
    GRAY_BED(MapColor.COLOR_GRAY),
    GRAY_CANDLE(MapColor.COLOR_GRAY),
    GRAY_CANDLE_CAKE(MapColor.QUARTZ),
    GRAY_CARPET(MapColor.COLOR_GRAY),
    GRAY_CONCRETE(MapColor.COLOR_GRAY),
    GRAY_CONCRETE_POWDER(MapColor.COLOR_GRAY),
    GRAY_GLAZED_TERRACOTTA(MapColor.COLOR_GRAY),
    GRAY_SHULKER_BOX(MapColor.COLOR_GRAY),
    GRAY_STAINED_GLASS(MapColor.COLOR_GRAY),
    GRAY_STAINED_GLASS_PANE(MapColor.COLOR_GRAY),
    GRAY_TERRACOTTA(MapColor.TERRACOTTA_GRAY),
    GRAY_WALL_BANNER(MapColor.WOOD),
    GRAY_WOOL(MapColor.COLOR_GRAY),
    GREEN_BANNER(MapColor.WOOD),
    GREEN_BED(MapColor.COLOR_GREEN),
    GREEN_CANDLE(MapColor.COLOR_GREEN),
    GREEN_CANDLE_CAKE(MapColor.QUARTZ),
    GREEN_CARPET(MapColor.COLOR_GREEN),
    GREEN_CONCRETE(MapColor.COLOR_GREEN),
    GREEN_CONCRETE_POWDER(MapColor.COLOR_GREEN),
    GREEN_GLAZED_TERRACOTTA(MapColor.COLOR_GREEN),
    GREEN_SHULKER_BOX(MapColor.COLOR_GREEN),
    GREEN_STAINED_GLASS(MapColor.COLOR_GREEN),
    GREEN_STAINED_GLASS_PANE(MapColor.COLOR_GREEN),
    GREEN_TERRACOTTA(MapColor.TERRACOTTA_GREEN),
    GREEN_WALL_BANNER(MapColor.WOOD),
    GREEN_WOOL(MapColor.COLOR_GREEN),
    GRINDSTONE(MapColor.METAL),
    HANGING_ROOTS(MapColor.DIRT),
    HAY_BLOCK(MapColor.COLOR_YELLOW),
    HEAVY_WEIGHTED_PRESSURE_PLATE(MapColor.METAL),
    HOPPER(MapColor.STONE),
    HONEY_BLOCK(MapColor.COLOR_ORANGE),
    HONEYCOMB_BLOCK(MapColor.COLOR_ORANGE),
    HORN_CORAL(MapColor.COLOR_YELLOW),
    HORN_CORAL_BLOCK(MapColor.COLOR_YELLOW),
    HORN_CORAL_FAN(MapColor.COLOR_YELLOW),
    HORN_CORAL_WALL_FAN(MapColor.COLOR_YELLOW),
    ICE(MapColor.ICE),
    INFESTED_CHISELED_STONE_BRICKS(MapColor.CLAY),
    INFESTED_COBBLESTONE(MapColor.CLAY),
    INFESTED_CRACKED_STONE_BRICKS(MapColor.CLAY),
    INFESTED_DEEPSLATE(MapColor.DEEPSLATE),
    INFESTED_MOSSY_STONE_BRICKS(MapColor.CLAY),
    INFESTED_STONE(MapColor.CLAY),
    INFESTED_STONE_BRICKS(MapColor.CLAY),
    IRON_BARS(MapColor.METAL),
    IRON_BLOCK(MapColor.METAL),
    IRON_DOOR(MapColor.METAL),
    IRON_ORE(MapColor.STONE),
    IRON_TRAPDOOR(MapColor.METAL),
    JACK_O_LANTERN(MapColor.COLOR_ORANGE),
    JIGSAW(MapColor.COLOR_LIGHT_GRAY),
    JUKEBOX(MapColor.DIRT),
    JUNGLE_BUTTON(MapColor.DIRT),
    JUNGLE_DOOR(MapColor.DIRT),
    JUNGLE_FENCE(MapColor.DIRT),
    JUNGLE_FENCE_GATE(MapColor.DIRT),
    JUNGLE_LEAVES(MapColor.PLANT),
    JUNGLE_LOG(MapColor.PODZOL),
    JUNGLE_PLANKS(MapColor.DIRT),
    JUNGLE_PRESSURE_PLATE(MapColor.DIRT),
    JUNGLE_SAPLING(MapColor.PLANT),
    JUNGLE_SIGN(MapColor.DIRT),
    JUNGLE_SLAB(MapColor.DIRT),
    JUNGLE_STAIRS(MapColor.DIRT),
    JUNGLE_TRAPDOOR(MapColor.DIRT),
    JUNGLE_WALL_SIGN(MapColor.DIRT),
    JUNGLE_WOOD(MapColor.DIRT),
    KELP(MapColor.WATER),
    KELP_PLANT(MapColor.WATER),
    LADDER(MapColor.WOOD),
    LANTERN(MapColor.METAL),
    LAPIS_BLOCK(MapColor.LAPIS),
    LAPIS_ORE(MapColor.STONE),
    LARGE_AMETHYST_BUD(MapColor.COLOR_PURPLE),
    LARGE_FERN(MapColor.PLANT),
    LAVA(MapColor.FIRE),
    LAVA_CAULDRON(MapColor.STONE),
    LECTERN(MapColor.WOOD),
    LEVER(MapColor.STONE),
    LIGHT(MapColor.TRANSPARENT),
    LIGHT_BLUE_BANNER(MapColor.WOOD),
    LIGHT_BLUE_BED(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_CANDLE(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_CANDLE_CAKE(MapColor.QUARTZ),
    LIGHT_BLUE_CARPET(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_CONCRETE(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_CONCRETE_POWDER(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_GLAZED_TERRACOTTA(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_SHULKER_BOX(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_STAINED_GLASS(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_STAINED_GLASS_PANE(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_BLUE_TERRACOTTA(MapColor.TERRACOTTA_LIGHT_BLUE),
    LIGHT_BLUE_WALL_BANNER(MapColor.WOOD),
    LIGHT_BLUE_WOOL(MapColor.COLOR_LIGHT_BLUE),
    LIGHT_GRAY_BANNER(MapColor.WOOD),
    LIGHT_GRAY_BED(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_CANDLE(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_CANDLE_CAKE(MapColor.QUARTZ),
    LIGHT_GRAY_CARPET(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_CONCRETE(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_CONCRETE_POWDER(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_GLAZED_TERRACOTTA(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_SHULKER_BOX(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_STAINED_GLASS(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_STAINED_GLASS_PANE(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_GRAY_TERRACOTTA(MapColor.TERRACOTTA_LIGHT_GRAY),
    LIGHT_GRAY_WALL_BANNER(MapColor.WOOD),
    LIGHT_GRAY_WOOL(MapColor.COLOR_LIGHT_GRAY),
    LIGHT_WEIGHTED_PRESSURE_PLATE(MapColor.GOLD),
    LIGHTNING_ROD(MapColor.COLOR_ORANGE),
    LILAC(MapColor.PLANT),
    LILY_OF_THE_VALLEY(MapColor.PLANT),
    LILY_PAD(MapColor.PLANT),
    LIME_BANNER(MapColor.WOOD),
    LIME_BED(MapColor.COLOR_LIGHT_GREEN),
    LIME_CANDLE(MapColor.COLOR_LIGHT_GREEN),
    LIME_CANDLE_CAKE(MapColor.QUARTZ),
    LIME_CARPET(MapColor.COLOR_LIGHT_GREEN),
    LIME_CONCRETE(MapColor.COLOR_LIGHT_GREEN),
    LIME_CONCRETE_POWDER(MapColor.COLOR_LIGHT_GREEN),
    LIME_GLAZED_TERRACOTTA(MapColor.COLOR_LIGHT_GREEN),
    LIME_SHULKER_BOX(MapColor.COLOR_LIGHT_GREEN),
    LIME_STAINED_GLASS(MapColor.COLOR_LIGHT_GREEN),
    LIME_STAINED_GLASS_PANE(MapColor.COLOR_LIGHT_GREEN),
    LIME_TERRACOTTA(MapColor.TERRACOTTA_LIGHT_GREEN),
    LIME_WALL_BANNER(MapColor.WOOD),
    LIME_WOOL(MapColor.COLOR_LIGHT_GREEN),
    LODESTONE(MapColor.METAL),
    LOOM(MapColor.WOOD),
    MAGENTA_BANNER(MapColor.WOOD),
    MAGENTA_BED(MapColor.COLOR_MAGENTA),
    MAGENTA_CANDLE(MapColor.COLOR_MAGENTA),
    MAGENTA_CANDLE_CAKE(MapColor.QUARTZ),
    MAGENTA_CARPET(MapColor.COLOR_MAGENTA),
    MAGENTA_CONCRETE(MapColor.COLOR_MAGENTA),
    MAGENTA_CONCRETE_POWDER(MapColor.COLOR_MAGENTA),
    MAGENTA_GLAZED_TERRACOTTA(MapColor.COLOR_MAGENTA),
    MAGENTA_SHULKER_BOX(MapColor.COLOR_MAGENTA),
    MAGENTA_STAINED_GLASS(MapColor.COLOR_MAGENTA),
    MAGENTA_STAINED_GLASS_PANE(MapColor.COLOR_MAGENTA),
    MAGENTA_TERRACOTTA(MapColor.TERRACOTTA_MAGENTA),
    MAGENTA_WALL_BANNER(MapColor.WOOD),
    MAGENTA_WOOL(MapColor.COLOR_MAGENTA),
    MAGMA_BLOCK(MapColor.NETHER),
    MANGROVE_BUTTON(MapColor.COLOR_RED),
    MANGROVE_DOOR(MapColor.COLOR_RED),
    MANGROVE_FENCE(MapColor.COLOR_RED),
    MANGROVE_FENCE_GATE(MapColor.COLOR_RED),
    MANGROVE_LEAVES(MapColor.PLANT),
    MANGROVE_LOG(MapColor.PODZOL),
    MANGROVE_PLANKS(MapColor.COLOR_RED),
    MANGROVE_PRESSURE_PLATE(MapColor.COLOR_RED),
    MANGROVE_PROPAGULE(MapColor.PLANT),
    MANGROVE_ROOTS(MapColor.PODZOL),
    MANGROVE_SIGN(MapColor.COLOR_RED),
    MANGROVE_SLAB(MapColor.COLOR_RED),
    MANGROVE_STAIRS(MapColor.COLOR_RED),
    MANGROVE_TRAPDOOR(MapColor.COLOR_RED),
    MANGROVE_WALL_SIGN(MapColor.COLOR_RED),
    MANGROVE_WOOD(MapColor.COLOR_RED),
    MEDIUM_AMETHYST_BUD(MapColor.COLOR_PURPLE),
    MELON(MapColor.COLOR_LIGHT_GREEN),
    MELON_STEM(MapColor.PLANT),
    MOSS_BLOCK(MapColor.COLOR_GREEN),
    MOSS_CARPET(MapColor.COLOR_GREEN),
    MOSSY_COBBLESTONE(MapColor.STONE),
    MOSSY_COBBLESTONE_SLAB(MapColor.STONE),
    MOSSY_COBBLESTONE_STAIRS(MapColor.STONE),
    MOSSY_COBBLESTONE_WALL(MapColor.STONE),
    MOSSY_STONE_BRICK_SLAB(MapColor.STONE),
    MOSSY_STONE_BRICK_STAIRS(MapColor.STONE),
    MOSSY_STONE_BRICK_WALL(MapColor.STONE),
    MOSSY_STONE_BRICKS(MapColor.STONE),
    MOVING_PISTON(MapColor.TRANSPARENT),
    MUD(MapColor.TERRACOTTA_CYAN),
    MUD_BRICK_SLAB(MapColor.TERRACOTTA_LIGHT_GRAY),
    MUD_BRICK_STAIRS(MapColor.TERRACOTTA_LIGHT_GRAY),
    MUD_BRICK_WALL(MapColor.TERRACOTTA_LIGHT_GRAY),
    MUD_BRICKS(MapColor.TERRACOTTA_LIGHT_GRAY),
    MUDDY_MANGROVE_ROOTS(MapColor.PODZOL),
    MUSHROOM_STEM(MapColor.WOOL),
    MYCELIUM(MapColor.COLOR_PURPLE),
    NETHER_BRICK_FENCE(MapColor.NETHER),
    NETHER_BRICK_SLAB(MapColor.NETHER),
    NETHER_BRICK_STAIRS(MapColor.NETHER),
    NETHER_BRICK_WALL(MapColor.NETHER),
    NETHER_BRICKS(MapColor.NETHER),
    NETHER_GOLD_ORE(MapColor.NETHER),
    NETHER_PORTAL(MapColor.COLOR_PURPLE),
    NETHER_QUARTZ_ORE(MapColor.NETHER),
    NETHER_SPROUTS(MapColor.COLOR_CYAN),
    NETHER_WART(MapColor.COLOR_RED),
    NETHER_WART_BLOCK(MapColor.COLOR_RED),
    NETHERITE_BLOCK(MapColor.COLOR_BLACK),
    NETHERRACK(MapColor.NETHER),
    NOTE_BLOCK(MapColor.WOOD),
    OAK_BUTTON(MapColor.WOOD),
    OAK_DOOR(MapColor.WOOD),
    OAK_FENCE(MapColor.WOOD),
    OAK_FENCE_GATE(MapColor.WOOD),
    OAK_LEAVES(MapColor.PLANT),
    OAK_LOG(MapColor.PODZOL),
    OAK_PLANKS(MapColor.WOOD),
    OAK_PRESSURE_PLATE(MapColor.WOOD),
    OAK_SAPLING(MapColor.PLANT),
    OAK_SIGN(MapColor.WOOD),
    OAK_SLAB(MapColor.WOOD),
    OAK_STAIRS(MapColor.WOOD),
    OAK_TRAPDOOR(MapColor.WOOD),
    OAK_WALL_SIGN(MapColor.WOOD),
    OAK_WOOD(MapColor.WOOD),
    OBSERVER(MapColor.STONE),
    OBSIDIAN(MapColor.COLOR_BLACK),
    OCHRE_FROGLIGHT(MapColor.SAND),
    ORANGE_BANNER(MapColor.WOOD),
    ORANGE_BED(MapColor.COLOR_ORANGE),
    ORANGE_CANDLE(MapColor.COLOR_ORANGE),
    ORANGE_CANDLE_CAKE(MapColor.QUARTZ),
    ORANGE_CARPET(MapColor.COLOR_ORANGE),
    ORANGE_CONCRETE(MapColor.COLOR_ORANGE),
    ORANGE_CONCRETE_POWDER(MapColor.COLOR_ORANGE),
    ORANGE_GLAZED_TERRACOTTA(MapColor.COLOR_ORANGE),
    ORANGE_SHULKER_BOX(MapColor.COLOR_ORANGE),
    ORANGE_STAINED_GLASS(MapColor.COLOR_ORANGE),
    ORANGE_STAINED_GLASS_PANE(MapColor.COLOR_ORANGE),
    ORANGE_TERRACOTTA(MapColor.TERRACOTTA_ORANGE),
    ORANGE_TULIP(MapColor.PLANT),
    ORANGE_WALL_BANNER(MapColor.WOOD),
    ORANGE_WOOL(MapColor.COLOR_ORANGE),
    OXEYE_DAISY(MapColor.PLANT),
    OXIDIZED_COPPER(MapColor.WARPED_NYLIUM),
    OXIDIZED_CUT_COPPER(MapColor.WARPED_NYLIUM),
    OXIDIZED_CUT_COPPER_SLAB(MapColor.WARPED_NYLIUM),
    OXIDIZED_CUT_COPPER_STAIRS(MapColor.WARPED_NYLIUM),
    PACKED_ICE(MapColor.ICE),
    PACKED_MUD(MapColor.DIRT),
    PEARLESCENT_FROGLIGHT(MapColor.COLOR_PINK),
    PEONY(MapColor.PLANT),
    PETRIFIED_OAK_SLAB(MapColor.WOOD),
    PINK_BANNER(MapColor.WOOD),
    PINK_BED(MapColor.COLOR_PINK),
    PINK_CANDLE(MapColor.COLOR_PINK),
    PINK_CANDLE_CAKE(MapColor.QUARTZ),
    PINK_CARPET(MapColor.COLOR_PINK),
    PINK_CONCRETE(MapColor.COLOR_PINK),
    PINK_CONCRETE_POWDER(MapColor.COLOR_PINK),
    PINK_GLAZED_TERRACOTTA(MapColor.COLOR_PINK),
    PINK_SHULKER_BOX(MapColor.COLOR_PINK),
    PINK_STAINED_GLASS(MapColor.COLOR_PINK),
    PINK_STAINED_GLASS_PANE(MapColor.COLOR_PINK),
    PINK_TERRACOTTA(MapColor.TERRACOTTA_PINK),
    PINK_TULIP(MapColor.PLANT),
    PINK_WALL_BANNER(MapColor.WOOD),
    PINK_WOOL(MapColor.COLOR_PINK),
    PISTON(MapColor.STONE),
    PISTON_HEAD(MapColor.STONE),
    PLAYER_HEAD(MapColor.COLOR_BROWN),
    PLAYER_WALL_HEAD(MapColor.COLOR_BROWN),
    PODZOL(MapColor.PODZOL),
    POINTED_DRIPSTONE(MapColor.TERRACOTTA_BROWN),
    POLISHED_ANDESITE(MapColor.STONE),
    POLISHED_ANDESITE_SLAB(MapColor.STONE),
    POLISHED_ANDESITE_STAIRS(MapColor.STONE),
    POLISHED_BASALT(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_BRICK_SLAB(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_BRICK_STAIRS(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_BRICK_WALL(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_BRICKS(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_BUTTON(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_PRESSURE_PLATE(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_SLAB(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_STAIRS(MapColor.COLOR_BLACK),
    POLISHED_BLACKSTONE_WALL(MapColor.COLOR_BLACK),
    POLISHED_DEEPSLATE(MapColor.DEEPSLATE),
    POLISHED_DEEPSLATE_SLAB(MapColor.DEEPSLATE),
    POLISHED_DEEPSLATE_STAIRS(MapColor.DEEPSLATE),
    POLISHED_DEEPSLATE_WALL(MapColor.DEEPSLATE),
    POLISHED_DIORITE(MapColor.QUARTZ),
    POLISHED_DIORITE_SLAB(MapColor.QUARTZ),
    POLISHED_DIORITE_STAIRS(MapColor.QUARTZ),
    POLISHED_GRANITE(MapColor.DIRT),
    POLISHED_GRANITE_SLAB(MapColor.DIRT),
    POLISHED_GRANITE_STAIRS(MapColor.DIRT),
    POPPY(MapColor.PLANT),
    POTATOES(MapColor.PLANT),
    POTTED_ACACIA_SAPLING(MapColor.COLOR_RED),
    POTTED_ALLIUM(MapColor.COLOR_RED),
    POTTED_AZALEA_BUSH(MapColor.COLOR_RED),
    POTTED_AZURE_BLUET(MapColor.COLOR_RED),
    POTTED_BAMBOO(MapColor.COLOR_RED),
    POTTED_BIRCH_SAPLING(MapColor.COLOR_RED),
    POTTED_BLUE_ORCHID(MapColor.COLOR_RED),
    POTTED_BROWN_MUSHROOM(MapColor.COLOR_RED),
    POTTED_CACTUS(MapColor.COLOR_RED),
    POTTED_CORNFLOWER(MapColor.COLOR_RED),
    POTTED_CRIMSON_FUNGUS(MapColor.COLOR_RED),
    POTTED_CRIMSON_ROOTS(MapColor.COLOR_RED),
    POTTED_DANDELION(MapColor.COLOR_RED),
    POTTED_DARK_OAK_SAPLING(MapColor.COLOR_RED),
    POTTED_DEAD_BUSH(MapColor.COLOR_RED),
    POTTED_FERN(MapColor.COLOR_RED),
    POTTED_FLOWERING_AZALEA_BUSH(MapColor.COLOR_RED),
    POTTED_JUNGLE_SAPLING(MapColor.COLOR_RED),
    POTTED_LILY_OF_THE_VALLEY(MapColor.COLOR_RED),
    POTTED_MANGROVE_PROPAGULE(MapColor.COLOR_RED),
    POTTED_OAK_SAPLING(MapColor.COLOR_RED),
    POTTED_ORANGE_TULIP(MapColor.COLOR_RED),
    POTTED_OXEYE_DAISY(MapColor.COLOR_RED),
    POTTED_PINK_TULIP(MapColor.COLOR_RED),
    POTTED_POPPY(MapColor.COLOR_RED),
    POTTED_RED_MUSHROOM(MapColor.COLOR_RED),
    POTTED_RED_TULIP(MapColor.COLOR_RED),
    POTTED_SPRUCE_SAPLING(MapColor.COLOR_RED),
    POTTED_WARPED_FUNGUS(MapColor.COLOR_RED),
    POTTED_WARPED_ROOTS(MapColor.COLOR_RED),
    POTTED_WHITE_TULIP(MapColor.COLOR_RED),
    POTTED_WITHER_ROSE(MapColor.COLOR_RED),
    POWDER_SNOW(MapColor.SNOW),
    POWDER_SNOW_CAULDRON(MapColor.STONE),
    POWERED_RAIL(MapColor.METAL),
    PRISMARINE(MapColor.COLOR_CYAN),
    PRISMARINE_BRICK_SLAB(MapColor.DIAMOND),
    PRISMARINE_BRICK_STAIRS(MapColor.DIAMOND),
    PRISMARINE_BRICKS(MapColor.DIAMOND),
    PRISMARINE_SLAB(MapColor.COLOR_CYAN),
    PRISMARINE_STAIRS(MapColor.COLOR_CYAN),
    PRISMARINE_WALL(MapColor.COLOR_CYAN),
    PUMPKIN(MapColor.COLOR_ORANGE),
    PUMPKIN_STEM(MapColor.PLANT),
    PURPLE_BANNER(MapColor.WOOD),
    PURPLE_BED(MapColor.COLOR_PURPLE),
    PURPLE_CANDLE(MapColor.COLOR_PURPLE),
    PURPLE_CANDLE_CAKE(MapColor.QUARTZ),
    PURPLE_CARPET(MapColor.COLOR_PURPLE),
    PURPLE_CONCRETE(MapColor.COLOR_PURPLE),
    PURPLE_CONCRETE_POWDER(MapColor.COLOR_PURPLE),
    PURPLE_GLAZED_TERRACOTTA(MapColor.COLOR_PURPLE),
    PURPLE_SHULKER_BOX(MapColor.TERRACOTTA_PURPLE),
    PURPLE_STAINED_GLASS(MapColor.COLOR_PURPLE),
    PURPLE_STAINED_GLASS_PANE(MapColor.COLOR_PURPLE),
    PURPLE_TERRACOTTA(MapColor.TERRACOTTA_PURPLE),
    PURPLE_WALL_BANNER(MapColor.WOOD),
    PURPLE_WOOL(MapColor.COLOR_PURPLE),
    PURPUR_BLOCK(MapColor.COLOR_MAGENTA),
    PURPUR_PILLAR(MapColor.COLOR_MAGENTA),
    PURPUR_SLAB(MapColor.COLOR_MAGENTA),
    PURPUR_STAIRS(MapColor.COLOR_MAGENTA),
    QUARTZ_BLOCK(MapColor.QUARTZ),
    QUARTZ_BRICKS(MapColor.QUARTZ),
    QUARTZ_PILLAR(MapColor.QUARTZ),
    QUARTZ_SLAB(MapColor.QUARTZ),
    QUARTZ_STAIRS(MapColor.QUARTZ),
    RAIL(MapColor.METAL),
    RAW_COPPER_BLOCK(MapColor.COLOR_ORANGE),
    RAW_GOLD_BLOCK(MapColor.GOLD),
    RAW_IRON_BLOCK(MapColor.RAW_IRON),
    RED_BANNER(MapColor.WOOD),
    RED_BED(MapColor.COLOR_RED),
    RED_CANDLE(MapColor.COLOR_RED),
    RED_CANDLE_CAKE(MapColor.QUARTZ),
    RED_CARPET(MapColor.COLOR_RED),
    RED_CONCRETE(MapColor.COLOR_RED),
    RED_CONCRETE_POWDER(MapColor.COLOR_RED),
    RED_GLAZED_TERRACOTTA(MapColor.COLOR_RED),
    RED_MUSHROOM(MapColor.COLOR_RED),
    RED_MUSHROOM_BLOCK(MapColor.COLOR_RED),
    RED_NETHER_BRICK_SLAB(MapColor.NETHER),
    RED_NETHER_BRICK_STAIRS(MapColor.NETHER),
    RED_NETHER_BRICK_WALL(MapColor.NETHER),
    RED_NETHER_BRICKS(MapColor.NETHER),
    RED_SAND(MapColor.COLOR_ORANGE),
    RED_SANDSTONE(MapColor.COLOR_ORANGE),
    RED_SANDSTONE_SLAB(MapColor.COLOR_ORANGE),
    RED_SANDSTONE_STAIRS(MapColor.COLOR_ORANGE),
    RED_SANDSTONE_WALL(MapColor.COLOR_ORANGE),
    RED_SHULKER_BOX(MapColor.COLOR_RED),
    RED_STAINED_GLASS(MapColor.COLOR_RED),
    RED_STAINED_GLASS_PANE(MapColor.COLOR_RED),
    RED_TERRACOTTA(MapColor.TERRACOTTA_RED),
    RED_TULIP(MapColor.PLANT),
    RED_WALL_BANNER(MapColor.WOOD),
    RED_WOOL(MapColor.COLOR_RED),
    REDSTONE_BLOCK(MapColor.FIRE),
    REDSTONE_LAMP(MapColor.FIRE),
    REDSTONE_ORE(MapColor.STONE),
    REDSTONE_TORCH(MapColor.FIRE),
    REDSTONE_WALL_TORCH(MapColor.FIRE),
    REDSTONE_WIRE(MapColor.FIRE),
    REINFORCED_DEEPSLATE(MapColor.DEEPSLATE),
    REPEATER(MapColor.STONE),
    REPEATING_COMMAND_BLOCK(MapColor.COLOR_PURPLE),
    RESPAWN_ANCHOR(MapColor.COLOR_BLACK),
    ROOTED_DIRT(MapColor.DIRT),
    ROSE_BUSH(MapColor.PLANT),
    SAND(MapColor.SAND),
    SANDSTONE(MapColor.SAND),
    SANDSTONE_SLAB(MapColor.SAND),
    SANDSTONE_STAIRS(MapColor.SAND),
    SANDSTONE_WALL(MapColor.SAND),
    SCAFFOLDING(MapColor.SAND),
    SCULK(MapColor.COLOR_BLACK),
    SCULK_CATALYST(MapColor.COLOR_BLACK),
    SCULK_SENSOR(MapColor.COLOR_CYAN),
    SCULK_SHRIEKER(MapColor.COLOR_BLACK),
    SCULK_VEIN(MapColor.COLOR_BLACK),
    SEA_LANTERN(MapColor.QUARTZ),
    SEA_PICKLE(MapColor.COLOR_GREEN),
    SEAGRASS(MapColor.WATER),
    SHROOMLIGHT(MapColor.COLOR_RED),
    SHULKER_BOX(MapColor.COLOR_PURPLE),
    SKELETON_SKULL(MapColor.QUARTZ),
    SKELETON_WALL_SKULL(MapColor.QUARTZ),
    SLIME_BLOCK(MapColor.GRASS),
    SMALL_AMETHYST_BUD(MapColor.COLOR_PURPLE),
    SMALL_DRIPLEAF(MapColor.PLANT),
    SMITHING_TABLE(MapColor.WOOD),
    SMOKER(MapColor.STONE),
    SMOOTH_BASALT(MapColor.COLOR_BLACK),
    SMOOTH_QUARTZ(MapColor.QUARTZ),
    SMOOTH_QUARTZ_SLAB(MapColor.QUARTZ),
    SMOOTH_QUARTZ_STAIRS(MapColor.QUARTZ),
    SMOOTH_RED_SANDSTONE(MapColor.COLOR_ORANGE),
    SMOOTH_RED_SANDSTONE_SLAB(MapColor.COLOR_ORANGE),
    SMOOTH_RED_SANDSTONE_STAIRS(MapColor.COLOR_ORANGE),
    SMOOTH_SANDSTONE(MapColor.SAND),
    SMOOTH_SANDSTONE_SLAB(MapColor.SAND),
    SMOOTH_SANDSTONE_STAIRS(MapColor.SAND),
    SMOOTH_STONE(MapColor.STONE),
    SMOOTH_STONE_SLAB(MapColor.STONE),
    SNOW(MapColor.SNOW),
    SNOW_BLOCK(MapColor.SNOW),
    SOUL_CAMPFIRE(MapColor.PODZOL),
    SOUL_FIRE(MapColor.COLOR_LIGHT_BLUE),
    SOUL_LANTERN(MapColor.METAL),
    SOUL_SAND(MapColor.COLOR_BROWN),
    SOUL_SOIL(MapColor.COLOR_BROWN),
    SOUL_TORCH(MapColor.COLOR_LIGHT_BLUE),
    SOUL_WALL_TORCH(MapColor.COLOR_LIGHT_BLUE),
    SPAWNER(MapColor.STONE),
    SPONGE(MapColor.COLOR_YELLOW),
    SPORE_BLOSSOM(MapColor.PLANT),
    SPRUCE_BUTTON(MapColor.PODZOL),
    SPRUCE_DOOR(MapColor.PODZOL),
    SPRUCE_FENCE(MapColor.PODZOL),
    SPRUCE_FENCE_GATE(MapColor.PODZOL),
    SPRUCE_LEAVES(MapColor.PLANT),
    SPRUCE_LOG(MapColor.COLOR_BROWN),
    SPRUCE_PLANKS(MapColor.PODZOL),
    SPRUCE_PRESSURE_PLATE(MapColor.PODZOL),
    SPRUCE_SAPLING(MapColor.PLANT),
    SPRUCE_SIGN(MapColor.PODZOL),
    SPRUCE_SLAB(MapColor.PODZOL),
    SPRUCE_STAIRS(MapColor.PODZOL),
    SPRUCE_TRAPDOOR(MapColor.PODZOL),
    SPRUCE_WALL_SIGN(MapColor.PODZOL),
    SPRUCE_WOOD(MapColor.PODZOL),
    STICKY_PISTON(MapColor.STONE),
    STONE(MapColor.STONE),
    STONE_BRICK_SLAB(MapColor.STONE),
    STONE_BRICK_STAIRS(MapColor.STONE),
    STONE_BRICK_WALL(MapColor.STONE),
    STONE_BRICKS(MapColor.STONE),
    STONE_BUTTON(MapColor.STONE),
    STONE_PRESSURE_PLATE(MapColor.STONE),
    STONE_SLAB(MapColor.STONE),
    STONE_STAIRS(MapColor.STONE),
    STONECUTTER(MapColor.STONE),
    STRIPPED_ACACIA_LOG(MapColor.COLOR_ORANGE),
    STRIPPED_ACACIA_WOOD(MapColor.COLOR_ORANGE),
    STRIPPED_BIRCH_LOG(MapColor.SAND),
    STRIPPED_BIRCH_WOOD(MapColor.SAND),
    STRIPPED_CRIMSON_HYPHAE(MapColor.CRIMSON_HYPHAE),
    STRIPPED_CRIMSON_STEM(MapColor.CRIMSON_STEM),
    STRIPPED_DARK_OAK_LOG(MapColor.COLOR_BROWN),
    STRIPPED_DARK_OAK_WOOD(MapColor.COLOR_BROWN),
    STRIPPED_JUNGLE_LOG(MapColor.DIRT),
    STRIPPED_JUNGLE_WOOD(MapColor.DIRT),
    STRIPPED_OAK_LOG(MapColor.WOOD),
    STRIPPED_OAK_WOOD(MapColor.WOOD),
    STRIPPED_SPRUCE_LOG(MapColor.PODZOL),
    STRIPPED_SPRUCE_WOOD(MapColor.PODZOL),
    STRIPPED_WARPED_HYPHAE(MapColor.WARPED_HYPHAE),
    STRIPPED_WARPED_STEM(MapColor.WARPED_STEM),
    STRUCTURE_BLOCK(MapColor.COLOR_LIGHT_GRAY),
    STRUCTURE_VOID(MapColor.TRANSPARENT),
    SUGAR_CANE(MapColor.PLANT),
    SUNFLOWER(MapColor.PLANT),
    SWEET_BERRY_BUSH(MapColor.PLANT),
    TALL_GRASS(MapColor.PLANT),
    TALL_SEAGRASS(MapColor.WATER),
    TARGET(MapColor.QUARTZ),
    TERRACOTTA(MapColor.COLOR_ORANGE),
    TINTED_GLASS(MapColor.COLOR_GRAY),
    TNT(MapColor.FIRE),
    TORCH(MapColor.COLOR_YELLOW),
    TRAPPED_CHEST(MapColor.WOOD),
    TRIPWIRE(MapColor.SNOW),
    TRIPWIRE_HOOK(MapColor.WOOD),
    TUBE_CORAL(MapColor.COLOR_BLUE),
    TUBE_CORAL_BLOCK(MapColor.COLOR_BLUE),
    TUBE_CORAL_FAN(MapColor.COLOR_BLUE),
    TUBE_CORAL_WALL_FAN(MapColor.COLOR_BLUE),
    TUFF(MapColor.TERRACOTTA_GRAY),
    TURTLE_EGG(MapColor.SAND),
    TWISTING_VINES(MapColor.COLOR_CYAN),
    TWISTING_VINES_PLANT(MapColor.COLOR_CYAN),
    VERDANT_FROGLIGHT(MapColor.GLOW_LICHEN),
    VINE(MapColor.PLANT),
    VOID_AIR(MapColor.TRANSPARENT),
    WALL_TORCH(MapColor.COLOR_YELLOW),
    WARPED_BUTTON(MapColor.WARPED_STEM),
    WARPED_DOOR(MapColor.WARPED_STEM),
    WARPED_FENCE(MapColor.WARPED_STEM),
    WARPED_FENCE_GATE(MapColor.WARPED_STEM),
    WARPED_FUNGUS(MapColor.COLOR_CYAN),
    WARPED_HYPHAE(MapColor.WARPED_HYPHAE),
    WARPED_NYLIUM(MapColor.WARPED_NYLIUM),
    WARPED_PLANKS(MapColor.WARPED_STEM),
    WARPED_PRESSURE_PLATE(MapColor.WARPED_STEM),
    WARPED_ROOTS(MapColor.COLOR_CYAN),
    WARPED_SIGN(MapColor.WARPED_STEM),
    WARPED_SLAB(MapColor.WARPED_STEM),
    WARPED_STAIRS(MapColor.WARPED_STEM),
    WARPED_STEM(MapColor.WARPED_STEM),
    WARPED_TRAPDOOR(MapColor.WARPED_STEM),
    WARPED_WALL_SIGN(MapColor.WARPED_STEM),
    WARPED_WART_BLOCK(MapColor.WARPED_WART_BLOCK),
    WATER(MapColor.WATER),
    WATER_CAULDRON(MapColor.STONE),
    WAXED_COPPER_BLOCK(MapColor.COLOR_ORANGE),
    WAXED_CUT_COPPER(MapColor.COLOR_ORANGE),
    WAXED_CUT_COPPER_SLAB(MapColor.COLOR_ORANGE),
    WAXED_CUT_COPPER_STAIRS(MapColor.COLOR_ORANGE),
    WAXED_EXPOSED_COPPER(MapColor.TERRACOTTA_LIGHT_GRAY),
    WAXED_EXPOSED_CUT_COPPER(MapColor.TERRACOTTA_LIGHT_GRAY),
    WAXED_EXPOSED_CUT_COPPER_SLAB(MapColor.TERRACOTTA_LIGHT_GRAY),
    WAXED_EXPOSED_CUT_COPPER_STAIRS(MapColor.TERRACOTTA_LIGHT_GRAY),
    WAXED_OXIDIZED_COPPER(MapColor.WARPED_NYLIUM),
    WAXED_OXIDIZED_CUT_COPPER(MapColor.WARPED_NYLIUM),
    WAXED_OXIDIZED_CUT_COPPER_SLAB(MapColor.WARPED_NYLIUM),
    WAXED_OXIDIZED_CUT_COPPER_STAIRS(MapColor.WARPED_NYLIUM),
    WAXED_WEATHERED_COPPER(MapColor.WARPED_STEM),
    WAXED_WEATHERED_CUT_COPPER(MapColor.WARPED_STEM),
    WAXED_WEATHERED_CUT_COPPER_SLAB(MapColor.WARPED_STEM),
    WAXED_WEATHERED_CUT_COPPER_STAIRS(MapColor.WARPED_STEM),
    WEATHERED_COPPER(MapColor.WARPED_STEM),
    WEATHERED_CUT_COPPER(MapColor.WARPED_STEM),
    WEATHERED_CUT_COPPER_SLAB(MapColor.WARPED_STEM),
    WEATHERED_CUT_COPPER_STAIRS(MapColor.WARPED_STEM),
    WEEPING_VINES(MapColor.NETHER),
    WEEPING_VINES_PLANT(MapColor.NETHER),
    WET_SPONGE(MapColor.COLOR_YELLOW),
    WHEAT(MapColor.PLANT),
    WHITE_BANNER(MapColor.WOOD),
    WHITE_BED(MapColor.SNOW),
    WHITE_CANDLE(MapColor.WOOL),
    WHITE_CANDLE_CAKE(MapColor.QUARTZ),
    WHITE_CARPET(MapColor.SNOW),
    WHITE_CONCRETE(MapColor.SNOW),
    WHITE_CONCRETE_POWDER(MapColor.SNOW),
    WHITE_GLAZED_TERRACOTTA(MapColor.SNOW),
    WHITE_SHULKER_BOX(MapColor.SNOW),
    WHITE_STAINED_GLASS(MapColor.SNOW),
    WHITE_STAINED_GLASS_PANE(MapColor.SNOW),
    WHITE_TERRACOTTA(MapColor.TERRACOTTA_WHITE),
    WHITE_TULIP(MapColor.PLANT),
    WHITE_WALL_BANNER(MapColor.WOOD),
    WHITE_WOOL(MapColor.SNOW),
    WITHER_ROSE(MapColor.PLANT),
    WITHER_SKELETON_SKULL(MapColor.TERRACOTTA_BLACK),
    WITHER_SKELETON_WALL_SKULL(MapColor.TERRACOTTA_BLACK),
    YELLOW_BANNER(MapColor.WOOD),
    YELLOW_BED(MapColor.COLOR_YELLOW),
    YELLOW_CANDLE(MapColor.COLOR_YELLOW),
    YELLOW_CANDLE_CAKE(MapColor.QUARTZ),
    YELLOW_CARPET(MapColor.COLOR_YELLOW),
    YELLOW_CONCRETE(MapColor.COLOR_YELLOW),
    YELLOW_CONCRETE_POWDER(MapColor.COLOR_YELLOW),
    YELLOW_GLAZED_TERRACOTTA(MapColor.COLOR_YELLOW),
    YELLOW_SHULKER_BOX(MapColor.COLOR_YELLOW),
    YELLOW_STAINED_GLASS(MapColor.COLOR_YELLOW),
    YELLOW_STAINED_GLASS_PANE(MapColor.COLOR_YELLOW),
    YELLOW_TERRACOTTA(MapColor.TERRACOTTA_YELLOW),
    YELLOW_WALL_BANNER(MapColor.WOOD),
    YELLOW_WOOL(MapColor.COLOR_YELLOW),
    ZOMBIE_HEAD(MapColor.COLOR_BROWN),
    ZOMBIE_WALL_HEAD(MapColor.COLOR_BROWN);

    private final Color color;

    BlockColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the Color of this Material
     *
     * @return Color of this material
     */
    public Color getColor() {
        return color;
    }
}
