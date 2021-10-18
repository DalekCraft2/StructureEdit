package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.Configuration;
import me.dalekcraft.structureedit.util.GzipUtils;
import net.querz.nbt.io.NBTUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public interface Schematic {

    Logger LOGGER = LogManager.getLogger(Schematic.class);

    /**
     * Extracts a {@link Schematic} from a {@link File}.
     *
     * @param file the file that contains the {@link Schematic}
     * @return the {@link Schematic} stored in the {@link File}, or {@code null} if the {@link File} is not a {@link Schematic} file
     * @throws IOException   if the {@link File}'s NBT can not be parsed
     * @throws JSONException if the {@link File}'s JSON can not be parsed (exclusive to TARDIS schematics)
     */
    @Nullable
    static Schematic openFrom(@NotNull File file) throws IOException, JSONException {
        String path = file.getCanonicalPath();
        switch (path.substring(path.lastIndexOf('.') + 1)) {
            case TardisSchematic.EXTENSION -> {
                return new TardisSchematic(new JSONObject(GzipUtils.unzip(file)));
            }
            case NbtStructure.EXTENSION -> {
                return new NbtStructure(NBTUtil.read(file));
            }
            case McEditSchematic.EXTENSION -> {
                LOGGER.log(Level.WARN, "MCEdit schematics are not yet supported!");
                return new McEditSchematic(NBTUtil.read(file));
            }
            case SpongeSchematic.EXTENSION -> {
                return SpongeSchematic.getInstance(NBTUtil.read(file));
            }
            default -> {
                LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.not_schematic"));
                return null;
            }
        }
    }

    /**
     * Saves this {@link Schematic} to a {@link File}.
     *
     * @param file the {@link File} to which to save this {@link Schematic}
     * @throws IOException if the {@link Schematic} can not be saved
     */
    void saveTo(File file) throws IOException;

    /**
     * Returns the raw data of this {@link Schematic}.
     *
     * @return the raw data of this {@link Schematic}
     */
    Object getData();

    /**
     * Returns the file extension for this {@link Schematic}'s format.
     *
     * @return the file extension for this {@link Schematic}'s format
     */
    String getFormat();

    /**
     * Returns the dimensions of this {@link Schematic} as an {@code int[]}, ordered as {@code sizeX, sizeY, sizeZ}.
     *
     * @return an {@code int[]} containing the dimensions of this {@link Schematic}
     */
    int[] getSize();

    /**
     * Sets the dimensions of this {@link Schematic}.
     *
     * @param size the new dimensions for this schematic
     */
    default void setSize(int @NotNull [] size) {
        setSize(size[0], size[1], size[2]);
    }

    /**
     * Sets the dimensions of this {@link Schematic}.
     *
     * @param sizeX the size of the x dimension
     * @param sizeY the size of the y dimension
     * @param sizeZ the size of the z dimension
     */
    void setSize(int sizeX, int sizeY, int sizeZ);

    /**
     * Returns the block at the specified position.
     *
     * @param position the position of the block
     * @return the block, or {@code null} if no block is at the position
     */
    default Block getBlock(int @NotNull [] position) {
        return getBlock(position[0], position[1], position[2]);
    }

    /**
     * Returns the block at the specified position.
     *
     * @param x the x coordinate of the block
     * @param y the y coordinate of the block
     * @param z the z coordinate of the block
     * @return the block, or {@code null} if no block is at the position
     */
    Block getBlock(int x, int y, int z);

    /**
     * Sets the block at the specified position.
     *
     * @param position the position of the block
     * @param block    the new block
     */
    default void setBlock(int @NotNull [] position, Block block) {
        setBlock(position[0], position[1], position[2], block);
    }

    /**
     * Sets the block at the specified position.
     *
     * @param x     the x coordinate of the block
     * @param y     the y coordinate of the block
     * @param z     the z coordinate of the block
     * @param block the new block
     */
    void setBlock(int x, int y, int z, Block block);
}
