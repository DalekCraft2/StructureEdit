package me.eccentric_nz.tardisschematicviewer;

import me.eccentric_nz.tardisschematicviewer.util.GzipUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface Schematic {

    static Schematic openFrom(String path) throws IOException, JSONException {
        switch (path.substring(path.lastIndexOf('.') + 1)) {
            case "tschm" -> {
                return new TardisSchematic(new JSONObject(GzipUtils.unzip(path)));
            }
            case "nbt" -> {
                return new NbtSchematic(NBTUtil.read(path));
            }
            default -> {
                return null;
            }
        }
    }

    void saveTo(String path) throws IOException;

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
