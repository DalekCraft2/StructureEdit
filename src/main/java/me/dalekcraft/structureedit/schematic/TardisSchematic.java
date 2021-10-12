package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.GzipUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public record TardisSchematic(JSONObject schematic) implements Schematic {

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
    @NotNull
    public TardisBlock getBlock(int x, int y, int z) {
        JSONArray blocks = schematic.getJSONArray("input");
        JSONArray level = blocks.getJSONArray(y);
        JSONArray row = level.getJSONArray(x);
        JSONObject blockObject = row.getJSONObject(z);
        return new TardisBlock(blockObject, new int[]{x, y, z});
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
        JSONArray blocks = schematic.getJSONArray("input");
        JSONArray level = blocks.getJSONArray(y);
        JSONArray row = level.getJSONArray(x);
        row.put(z, block);
        level.put(x, row);
        blocks.put(y, level);
        schematic.put("input", blocks);
    }

    @Contract(value = " -> fail", pure = true)
    @Override
    public Palette getPalette() {
        throw new UnsupportedOperationException("Palettes are not supported by the TSCHM format.");
    }

    @Contract(value = "_ -> fail", pure = true)
    @Override
    public void setPalette(Palette palette) {
        throw new UnsupportedOperationException("Palettes are not supported by the TSCHM format.");
    }
}
