package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.GzipUtils;
import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public record TardisSchematic(JSONObject schematic) implements Schematic {

    private static final Logger LOGGER = LogManager.getLogger(TardisSchematic.class);

    @Override
    public void saveTo(File file) throws IOException {
        GzipUtils.zip(schematic, file);
    }

    @Contract(pure = true)
    @Override
    public JSONObject getData() {
        return schematic;
    }

    @Contract(pure = true)
    @Override
    @NotNull
    public String getFormat() {
        return EXTENSION_TARDIS;
    }

    @Override
    public int @NotNull [] getSize() {
        JSONObject size = schematic.getJSONObject("dimensions");
        return new int[]{size.getInt("width"), size.getInt("height"), size.getInt("length")};
    }

    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        JSONObject size = schematic.getJSONObject("dimensions");
        size.put("width", sizeX);
        size.put("height", sizeY);
        size.put("length", sizeZ);
        schematic.put("dimensions", size);
    }

    @Override
    public JSONObject getBlock(int x, int y, int z) {
        JSONArray blocks = schematic.getJSONArray("input");
        JSONArray level = blocks.getJSONArray(y);
        JSONArray row = level.getJSONArray(x);
        return row.getJSONObject(z);
    }

    @Override
    public void setBlock(int x, int y, int z, Object block) {
        JSONArray blocks = schematic.getJSONArray("input");
        JSONArray level = blocks.getJSONArray(y);
        JSONArray row = level.getJSONArray(x);
        row.put(z, block);
        level.put(x, row);
        blocks.put(y, level);
        schematic.put("input", blocks);
    }

    @Override
    @NotNull
    public String getBlockId(int x, int y, int z) {
        String state = getBlock(x, y, z).getString("data");
        int nameEndIndex = state.length();
        if (state.contains("[")) {
            nameEndIndex = state.indexOf('[');
        } else if (state.contains("{")) {
            nameEndIndex = state.indexOf('{');
        }
        return state.substring(0, nameEndIndex);
    }

    @Override
    public void setBlockId(int x, int y, int z, String id) {
        getBlock(x, y, z).put("data", id + getBlockPropertiesAsString(x, y, z));
    }

    @Override
    public CompoundTag getBlockProperties(int x, int y, int z) {
        String properties = getBlockPropertiesAsString(x, y, z);
        String replaced = properties.replace('[', '{').replace(']', '}').replace('=', ':');
        CompoundTag tag = new CompoundTag();
        try {
            tag = (CompoundTag) SNBTUtil.fromSNBT(replaced);
        } catch (StringIndexOutOfBoundsException ignored) {
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return PropertyUtils.byteToString(tag);
    }

    @Override
    public void setBlockProperties(int x, int y, int z, CompoundTag properties) {
        String propertiesString = "";
        try {
            propertiesString = SNBTUtil.toSNBT(PropertyUtils.byteToString(properties)).replace("\"", "");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        try {
            setBlockPropertiesAsString(x, y, z, propertiesString);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }

    @Override
    @NotNull
    public String getBlockPropertiesAsString(int x, int y, int z) {
        String state = getBlock(x, y, z).getString("data");
        return !state.substring(getBlockId(x, y, z).length()).equals("") ? state.substring(getBlockId(x, y, z).length()) : "[]";
    }

    @Override
    public void setBlockPropertiesAsString(int x, int y, int z, @NotNull String propertiesString) throws IOException {
        String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':');
        try {
            SNBTUtil.fromSNBT(replaced); // Check whether the SNBT is parsable
            getBlock(x, y, z).put("data", getBlockId(x, y, z) + propertiesString);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
    }

    @Contract(value = "_ -> fail", pure = true)
    @Override
    public CompoundTag getBlockNbt(int x, int y, int z) {
        throw new UnsupportedOperationException("NBT storage is not supported by the TSCHM format.");
    }

    @Contract(value = "_, _ -> fail", pure = true)
    @Override
    public void setBlockNbt(int x, int y, int z, CompoundTag nbt) {
        throw new UnsupportedOperationException("NBT storage is not supported by the TSCHM format.");
    }

    @Contract(value = "_ -> fail", pure = true)
    @Override
    public String getBlockSnbt(int x, int y, int z) {
        throw new UnsupportedOperationException("NBT storage is not supported by the TSCHM format.");
    }

    @Contract(value = "_, _ -> fail", pure = true)
    @Override
    public void setBlockSnbt(int x, int y, int z, String snbt) {
        throw new UnsupportedOperationException("NBT storage is not supported by the TSCHM format.");
    }
}
