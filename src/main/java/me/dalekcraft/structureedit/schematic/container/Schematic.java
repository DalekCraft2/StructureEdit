package me.dalekcraft.structureedit.schematic.container;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Vector;

public class Schematic {

    private final int[] size = new int[3];
    private final int[] offset = new int[3];
    private ObservableList<ObservableList<BlockState>> blockPalettes = FXCollections.observableArrayList();
    private Vector<Vector<Vector<Block>>> blocks = new Vector<>();
    private ObservableList<BiomeState> biomePalette = FXCollections.observableArrayList();
    private Vector<Vector<Vector<Biome>>> biomes = new Vector<>();
    private ObservableList<Entity> entities = FXCollections.observableArrayList();
    private int dataVersion = -1;
    private CompoundTag metadata;
    private boolean hasBiomes;

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

        blocks.setSize(sizeX);
        biomes.setSize(sizeX);
        for (int x = 0; x < sizeX; x++) {
            Vector<Vector<Block>> blockRow = blocks.get(x);
            if (blockRow == null) {
                blocks.set(x, new Vector<>());
                blockRow = blocks.get(x);
            }
            blockRow.setSize(sizeY);

            Vector<Vector<Biome>> biomeRow = biomes.get(x);
            if (biomeRow == null) {
                biomes.set(x, new Vector<>());
                biomeRow = biomes.get(x);
            }
            biomeRow.setSize(sizeY);

            for (int y = 0; y < sizeY; y++) {
                Vector<Block> blockColumn = blockRow.get(y);
                if (blockColumn == null) {
                    blockRow.set(y, new Vector<>());
                    blockColumn = blockRow.get(y);
                }
                blockColumn.setSize(sizeZ);

                Vector<Biome> biomeColumn = biomeRow.get(y);
                if (biomeColumn == null) {
                    biomeRow.set(y, new Vector<>());
                    biomeColumn = biomeRow.get(y);
                }
                biomeColumn.setSize(sizeZ);

                for (int z = 0; z < sizeZ; z++) {
                    // TODO maybe
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

    public Vector<Vector<Vector<Block>>> getBlocks() {
        return blocks;
    }

    public void setBlocks(Vector<Vector<Vector<Block>>> blocks) {
        this.blocks = blocks;
    }

    public BlockState getBlockState(int index) {
        return getBlockState(index, 0);
    }

    public BlockState getBlockState(int index, int paletteIndex) {
        while (blockPalettes.size() <= paletteIndex) {
            blockPalettes.add(FXCollections.observableArrayList());
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
            blockPalettes.add(FXCollections.observableArrayList());
        }
        blockPalettes.forEach(blockPalette -> {
            while (blockPalette.size() <= index) {
                blockPalette.add(null);
            }
        });
        blockPalettes.get(paletteIndex).set(index, blockState);
    }

    public ObservableList<BlockState> getBlockPalette() {
        return getBlockPalette(0);
    }

    public void setBlockPalette(ObservableList<BlockState> blockPalette) {
        setBlockPalette(0, blockPalette);
    }

    public ObservableList<BlockState> getBlockPalette(int paletteIndex) {
        while (blockPalettes.size() <= paletteIndex) {
            blockPalettes.add(FXCollections.observableArrayList());
        }
        return blockPalettes.get(paletteIndex);
    }

    public void setBlockPalette(int paletteIndex, ObservableList<BlockState> blockPalette) {
        blockPalettes.set(paletteIndex, blockPalette);
    }

    public ObservableList<ObservableList<BlockState>> getBlockPalettes() {
        return blockPalettes;
    }

    public void setBlockPalettes(ObservableList<ObservableList<BlockState>> blockPalettes) {
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
        if (biome != null) {
            hasBiomes = true;
        }
    }

    public Vector<Vector<Vector<Biome>>> getBiomes() {
        return biomes;
    }

    public void setBiomes(Vector<Vector<Vector<Biome>>> biomes) {
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

    public ObservableList<BiomeState> getBiomePalette() {
        return biomePalette;
    }

    public void setBiomePalette(ObservableList<BiomeState> biomePalette) {
        this.biomePalette = biomePalette;
    }

    public boolean hasBiomes() {
        return hasBiomes;
    }

    public ObservableList<Entity> getEntities() {
        return entities;
    }

    public void setEntities(ObservableList<Entity> entities) {
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
