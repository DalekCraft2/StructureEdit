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

    @Nullable
    static Schematic openFrom(@NotNull File file) throws IOException, JSONException {
        String path = file.getCanonicalPath();
        switch (path.substring(path.lastIndexOf('.') + 1)) {
            case "tschm" -> {
                return new TardisSchematic(new JSONObject(GzipUtils.unzip(file)));
            }
            case "nbt" -> {
                return new NbtSchematic(NBTUtil.read(file));
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
}
