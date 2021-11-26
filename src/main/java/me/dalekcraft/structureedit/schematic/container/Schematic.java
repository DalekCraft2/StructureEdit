package me.dalekcraft.structureedit.schematic.container;

import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Schematic {

    private final int[] size = new int[3];
    private final int[] offset = new int[3];
    private List<BlockState> blockPalette;
    private ArrayList<ArrayList<ArrayList<Block>>> blocks;
    private List<BiomeState> biomePalette;
    private ArrayList<ArrayList<ArrayList<Biome>>> biomes;
    private List<Entity> entities;
    private int dataVersion;
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
        blocks.ensureCapacity(sizeX);
        blocks.forEach(row -> {
            if (row == null) {
                row = new ArrayList<>();
            }
            row.ensureCapacity(sizeY);
            row.forEach(column -> {
                if (column == null) {
                    column = new ArrayList<>();
                }
                column.ensureCapacity(sizeZ);
            });
        });
        biomes.forEach(row -> {
            if (row == null) {
                row = new ArrayList<>();
            }
            row.ensureCapacity(sizeY);
            row.forEach(column -> {
                if (column == null) {
                    column = new ArrayList<>();
                }
                column.ensureCapacity(sizeZ);
            });
        });
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
        return blocks.get(x).get(y).get(z);
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
        blocks.get(x).get(y).set(z, block);
    }

    public ArrayList<ArrayList<ArrayList<Block>>> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<ArrayList<ArrayList<Block>>> blocks) {
        this.blocks = blocks;
    }

    public List<BlockState> getBlockPalette() {
        return blockPalette;
    }

    public void setBlockPalette(List<BlockState> blockPalette) {
        this.blockPalette = blockPalette;
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
        return biomes.get(x).get(y).get(z);
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
        biomes.get(x).get(y).set(z, biome);
    }

    public ArrayList<ArrayList<ArrayList<Biome>>> getBiomes() {
        return biomes;
    }

    public void setBiomes(ArrayList<ArrayList<ArrayList<Biome>>> biomes) {
        this.biomes = biomes;
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
