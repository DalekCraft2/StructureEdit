package me.eccentric_nz.tardisschematicviewer;

import me.eccentric_nz.tardisschematicviewer.util.BlockStateUtils;
import net.querz.nbt.tag.CompoundTag;
import org.json.JSONArray;
import org.json.JSONObject;

public class TardisSchematic implements Schematic {

    private final JSONObject schematic;

    public TardisSchematic(JSONObject schematic) {
        this.schematic = schematic;
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
    public String getBlockId(int x, int y, int z) {
        String state = getBlock(x, y, z).getString("data");
        int nameEndIndex = state.contains("[") ? state.indexOf('[') : state.length();
        return state.substring(0, nameEndIndex);
    }

    @Override
    public void setBlockId(int x, int y, int z, String id) {
        JSONObject block = getBlock(x, y, z);
        block.put("data", id + getBlockProperties(x, y, z));
        setBlock(x, y, z, block);
    }

    @Override
    public CompoundTag getBlockProperties(int x, int y, int z) {
        return BlockStateUtils.toTag(getBlockPropertiesAsString(x, y, z));
    }

    @Override
    public String getBlockPropertiesAsString(int x, int y, int z) {
        String state = getBlock(x, y, z).getString("data");
        return state.contains("[") ? state.substring(state.indexOf('[')) : "[]";
    }

    @Override
    public void setBlockProperties(int x, int y, int z, CompoundTag properties) {

    }

    @Override
    public void setBlockPropertiesAsString(int x, int y, int z, String properties) {
        JSONObject block = getBlock(x, y, z);
        block.put("data", getBlockId(x, y, z) + properties);
        setBlock(x, y, z, block);
    }
}
