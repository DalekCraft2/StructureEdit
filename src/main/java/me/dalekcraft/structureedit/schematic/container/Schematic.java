package me.dalekcraft.structureedit.schematic.container;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Schematic {

    private final int[] size = new int[3];
    private final int[] offset = new int[3];
    private List<List<BlockState>> blockPalettes = new ArrayList<>();
    private Table<Integer, Integer, List<Block>> blocks = HashBasedTable.create();
    private List<BiomeState> biomePalette = new ArrayList<>();
    private Table<Integer, Integer, List<Biome>> biomes = HashBasedTable.create();
    private List<Entity> entities = new ArrayList<>();
    private int dataVersion = -1;
    private CompoundTag metadata;

    /**
     * Returns the dimensions of this {@link Schematic} as an {@code int[]}, ordered as {@code sizeX, sizeY, sizeZ}.
     *
     * @return an {@code int[]} containing the dimensions of this {@link Schematic}
     */
    public int[] getSize() {
        return size;
    }

    /**
     * Sets the dimensions of this {@link Schematic}.
     *
     * @param size the new dimensions for this {@link Schematic}
     */
    public void setSize(int @NotNull [] size) {
        setSize(size[0], size[1], size[2]);
    }

    /**
     * Sets the dimensions of this {@link Schematic}.
     *
     * @param sizeX the size of the x dimension
     * @param sizeY the size of the y dimension
     * @param sizeZ the size of the z dimension
     */
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        size[0] = sizeX;
        size[1] = sizeY;
        size[2] = sizeZ;
        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {
                if (!blocks.containsRow(x) || !blocks.containsColumn(z) || blocks.get(x, z) == null) {
                    blocks.put(x, z, new ArrayList<>());
                }
                List<Block> blockList = blocks.get(x, z);
                assert blockList != null;

                if (!biomes.containsRow(x) || !biomes.containsColumn(z) || biomes.get(x, z) == null) {
                    biomes.put(x, z, new ArrayList<>());
                }
                List<Biome> biomeList = biomes.get(x, z);
                assert biomeList != null;

                while (blockList.size() <= sizeY) {
                    blockList.add(null);
                }

                while (biomeList.size() <= sizeY) {
                    biomeList.add(null);
                }
            }
        }
    }

    /**
     * Returns the offsets of this {@link Schematic} as an {@code int[]}, ordered as {@code offsetX, offsetY, offsetZ}.
     *
     * @return an {@code int[]} containing the offsets of this {@link Schematic}
     */
    public int[] getOffset() {
        return offset;
    }

    /**
     * Sets the offsets of this {@link Schematic}.
     *
     * @param offset the new offsets for this {@link Schematic}
     */
    public void setOffset(int @NotNull [] offset) {
        setOffset(offset[0], offset[1], offset[2]);
    }

    /**
     * Sets the offsets of this {@link Schematic}.
     *
     * @param offsetX the x offset
     * @param offsetY the y offset
     * @param offsetZ the z offset
     */
    public void setOffset(int offsetX, int offsetY, int offsetZ) {
        offset[0] = offsetX;
        offset[1] = offsetY;
        offset[2] = offsetZ;
    }

    /**
     * Returns the {@link Block} at the specified position.
     *
     * @param position the position of the {@link Block}
     * @return the {@link Block}, or {@code null} if no {@link Block} is at the position
     */
    public Block getBlock(int @NotNull [] position) {
        return getBlock(position[0], position[1], position[2]);
    }

    /**
     * Returns the {@link Block} at the specified position.
     *
     * @param x the x coordinate of the {@link Block}
     * @param y the y coordinate of the {@link Block}
     * @param z the z coordinate of the {@link Block}
     * @return the block, or {@code null} if no {@link Block} is at the position
     */
    public Block getBlock(int x, int y, int z) {
        return blocks.get(x, z).get(y);
    }

    /**
     * Sets the {@link Block} at the specified position.
     *
     * @param position the position of the {@link Block}
     * @param block    the new {@link Block}
     */
    public void setBlock(int @NotNull [] position, Block block) {
        setBlock(position[0], position[1], position[2], block);
    }

    /**
     * Sets the {@link Block} at the specified position.
     *
     * @param x     the x coordinate of the {@link Block}
     * @param y     the y coordinate of the {@link Block}
     * @param z     the z coordinate of the {@link Block}
     * @param block the new {@link Block}
     */
    public void setBlock(int x, int y, int z, Block block) {
        blocks.get(x, z).set(y, block);
    }

    public Table<Integer, Integer, List<Block>> getBlocks() {
        return blocks;
    }

    public void setBlocks(Table<Integer, Integer, List<Block>> blocks) {
        this.blocks = blocks;
    }

    public BlockState getBlockState(int index) {
        return getBlockState(index, 0);
    }

    public BlockState getBlockState(int index, int paletteIndex) {
        while (blockPalettes.size() <= paletteIndex) {
            blockPalettes.add(new ArrayList<>());
        }
        blockPalettes.forEach(blockPalette -> {
            while (blockPalette.size() <= index) {
                blockPalette.add(null);
            }
        });
        return blockPalettes.get(paletteIndex).get(index);
    }

    public void setBlockState(int index, BlockState blockState) {
        setBlockState(index, 0, blockState);
    }

    public void setBlockState(int index, int paletteIndex, BlockState blockState) {
        while (blockPalettes.size() <= paletteIndex) {
            blockPalettes.add(new ArrayList<>());
        }
        blockPalettes.forEach(blockPalette -> {
            while (blockPalette.size() <= index) {
                blockPalette.add(null);
            }
        });
        blockPalettes.get(paletteIndex).set(index, blockState);
    }

    public List<BlockState> getBlockPalette() {
        return getBlockPalette(0);
    }

    public void setBlockPalette(List<BlockState> blockPalette) {
        setBlockPalette(0, blockPalette);
    }

    public List<BlockState> getBlockPalette(int paletteIndex) {
        while (blockPalettes.size() <= paletteIndex) {
            blockPalettes.add(new ArrayList<>());
        }
        return blockPalettes.get(paletteIndex);
    }

    public void setBlockPalette(int paletteIndex, List<BlockState> blockPalette) {
        blockPalettes.set(paletteIndex, blockPalette);
    }

    public List<List<BlockState>> getBlockPalettes() {
        return blockPalettes;
    }

    public void setBlockPalettes(List<List<BlockState>> blockPalettes) {
        this.blockPalettes = blockPalettes;
    }

    /**
     * Returns the {@link Biome} at the specified position.
     *
     * @param position the position of the {@link Biome}
     * @return the {@link Biome}, or {@code null} if no {@link Biome} is at the position
     */
    public Biome getBiome(int @NotNull [] position) {
        return getBiome(position[0], position[1], position[2]);
    }

    /**
     * Returns the {@link Biome} at the specified position.
     *
     * @param x the x coordinate of the {@link Biome}
     * @param y the y coordinate of the {@link Biome}
     * @param z the z coordinate of the {@link Biome}
     * @return the block, or {@code null} if no {@link Biome} is at the position
     */
    public Biome getBiome(int x, int y, int z) {
        return biomes.get(x, z).get(y);
    }

    /**
     * Sets the {@link Biome} at the specified position.
     *
     * @param position the position of the {@link Biome}
     * @param biome    the new {@link Biome}
     */
    public void setBiome(int @NotNull [] position, Biome biome) {
        setBiome(position[0], position[1], position[2], biome);
    }

    /**
     * Sets the {@link Biome} at the specified position.
     *
     * @param x     the x coordinate of the {@link Biome}
     * @param y     the y coordinate of the {@link Biome}
     * @param z     the z coordinate of the {@link Biome}
     * @param biome the new {@link Biome}
     */
    public void setBiome(int x, int y, int z, Biome biome) {
        biomes.get(x, z).set(y, biome);
    }

    public Table<Integer, Integer, List<Biome>> getBiomes() {
        return biomes;
    }

    public void setBiomes(Table<Integer, Integer, List<Biome>> biomes) {
        this.biomes = biomes;
    }

    public BiomeState getBiomeState(int i) {
        if (i >= biomePalette.size()) {
            for (int j = biomePalette.size(); j <= i; j++) {
                biomePalette.add(j, null);
            }
        }
        return biomePalette.get(i);
    }

    public void setBiomeState(int i, BiomeState biomeState) {
        if (i >= biomePalette.size()) {
            for (int j = biomePalette.size(); j <= i; j++) {
                biomePalette.add(j, null);
            }
        }
        biomePalette.set(i, biomeState);
    }

    public List<BiomeState> getBiomePalette() {
        return biomePalette;
    }

    public void setBiomePalette(List<BiomeState> biomePalette) {
        this.biomePalette = biomePalette;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    /**
     * Returns the Minecraft data version of this {@link Schematic}.
     *
     * @return the Minecraft data version of this {@link Schematic}
     */
    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public CompoundTag getMetadata() {
        return metadata;
    }

    public void setMetadata(CompoundTag metadata) {
        this.metadata = metadata;
    }
}
