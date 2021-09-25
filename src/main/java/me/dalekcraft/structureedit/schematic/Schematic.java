package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.GzipUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public interface Schematic {

    String EXTENSION_NBT = "nbt";
    String EXTENSION_MCEDIT = "schematic";
    String EXTENSION_SPONGE = "schem";
    String EXTENSION_TARDIS = "tschm";

    @Nullable
    static Schematic openFrom(@NotNull File file) throws IOException, JSONException {
        String path = file.getCanonicalPath();
        switch (path.substring(path.lastIndexOf('.') + 1)) {
            case EXTENSION_TARDIS -> {
                return new TardisSchematic(new JSONObject(GzipUtils.unzip(file)));
            }
            case EXTENSION_NBT -> {
                return new NbtStructure(NBTUtil.read(file));
            }
            case EXTENSION_MCEDIT -> {
                System.err.println("MCEdit schematics are not yet supported!");
                return new McEditSchematic(NBTUtil.read(file));
            }
            case EXTENSION_SPONGE -> {
                return new SpongeSchematic(NBTUtil.read(file));
            }
            default -> {
                return null;
            }
        }
    }

    void saveTo(File file) throws IOException;

    Object getData();

    String getFormat();

    int[] getSize();

    void setSize(int x, int y, int z);

    Object getBlock(int x, int y, int z);

    void setBlock(int x, int y, int z, Object block);

    String getBlockId(Object block);

    void setBlockId(Object block, String id);

    CompoundTag getBlockProperties(Object block);

    void setBlockProperties(Object block, CompoundTag properties);

    String getBlockPropertiesAsString(Object block);

    void setBlockPropertiesAsString(Object block, String propertiesString) throws IOException;

    CompoundTag getBlockNbt(Object block);

    void setBlockNbt(Object block, CompoundTag nbt);

    String getBlockSnbt(Object block);

    void setBlockSnbt(Object block, String snbt) throws IOException;
}
