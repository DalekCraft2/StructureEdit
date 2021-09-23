package me.eccentric_nz.tardisschematicviewer.schematic;

import me.eccentric_nz.tardisschematicviewer.util.GzipUtils;
import me.eccentric_nz.tardisschematicviewer.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class TardisSchematic implements Schematic {

    private final JSONObject schematic;

    public TardisSchematic(JSONObject schematic) {
        this.schematic = schematic;
    }

    @Override
    public void saveTo(File file) throws IOException {
        GzipUtils.zip(getData(), file);
    }

    @Override
    public JSONObject getData() {
        return schematic;
    }

    @Override
    public String getFormat() {
        return "tschm";
    }

    @Override
    public int[] getSize() {
        JSONObject size = schematic.getJSONObject("dimensions");
        return new int[]{size.getInt("width"), size.getInt("height"), size.getInt("length")};
    }

    @Override
    public void setSize(int x, int y, int z) {
        JSONObject size = schematic.getJSONObject("dimensions");
        size.put("width", x);
        size.put("height", y);
        size.put("length", z);
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
    public String getBlockId(Object block) {
        String state = ((JSONObject) block).getString("data");
        int nameEndIndex = state.length();
        if (state.contains("[")) {
            nameEndIndex = state.indexOf('[');
        } else if (state.contains("{")) {
            nameEndIndex = state.indexOf('{');
        }
        return state.substring(0, nameEndIndex);
    }

    @Override
    public void setBlockId(Object block, String id) {
        ((JSONObject) block).put("data", id + getBlockPropertiesAsString(block));
    }

    @Override
    public CompoundTag getBlockProperties(Object block) {
        String properties = getBlockPropertiesAsString(block);
        String replaced = properties.replace('[', '{').replace(']', '}').replace('=', ':');
        CompoundTag tag = new CompoundTag();
        try {
            tag = (CompoundTag) SNBTUtil.fromSNBT(replaced);
        } catch (StringIndexOutOfBoundsException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return PropertyUtils.byteToString(tag);
    }

    @Override
    public void setBlockProperties(Object block, CompoundTag properties) {
        String propertiesString = "";
        try {
            propertiesString = SNBTUtil.toSNBT(PropertyUtils.byteToString(properties));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            setBlockPropertiesAsString(block, propertiesString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBlockPropertiesAsString(Object block) {
        String state = ((JSONObject) block).getString("data");
        return !state.substring(getBlockId(block).length()).equals("") ? state.substring(getBlockId(block).length()) : "[]";
    }

    @Override
    public void setBlockPropertiesAsString(Object block, String propertiesString) throws IOException {
        String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':');
        try {
            SNBTUtil.fromSNBT(replaced); // Check whether the SNBT is parsable
            ((JSONObject) block).put("data", getBlockId(block) + propertiesString);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
    }
}
