package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.exception.MissingKeyException;
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

public class TardisSchematic implements Schematic {

    public static final String EXTENSION = "tschm";
    private final JSONObject schematic;

    public TardisSchematic(JSONObject schematic) throws MissingKeyException {
        this.schematic = schematic;
        validate();
    }

    @Override
    public void validate() throws MissingKeyException {
        String currentKey = "input";
        Class<?> expectedType = JSONArray.class;
        if (!schematic.has("input")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(schematic.get("input"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        JSONArray levels = schematic.getJSONArray("input");
        if (!levels.isEmpty()) {
            for (int y = 0; y < levels.length(); y++) {
                currentKey = "input[" + y + "]";
                expectedType = JSONArray.class;
                if (!expectedType.isInstance(levels.get(y))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
                JSONArray rows = levels.getJSONArray(y);
                if (!rows.isEmpty()) {
                    for (int x = 0; x < rows.length(); x++) {
                        currentKey = "input[" + y + "][" + x + "]";
                        expectedType = JSONArray.class;
                        if (!expectedType.isInstance(rows.get(x))) {
                            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                        }
                        JSONArray columns = rows.getJSONArray(x);
                        if (!columns.isEmpty()) {
                            for (int z = 0; z < columns.length(); z++) {
                                currentKey = "input[" + y + "][" + x + "][" + z + "]";
                                expectedType = JSONObject.class;
                                if (!expectedType.isInstance(columns.get(z))) {
                                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                                }
                                JSONObject block = columns.getJSONObject(z);
                                currentKey = "input[" + y + "][" + x + "][" + z + "].data";
                                expectedType = String.class;
                                if (!block.has("data")) {
                                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                                }
                                if (!expectedType.isInstance(block.get("data"))) {
                                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                                }
                            }
                        }
                    }
                }
            }
        }

        if (schematic.has("relative")) {
            currentKey = "relative";
            expectedType = JSONObject.class;
            if (!expectedType.isInstance(schematic.get("relative"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            JSONObject relative = schematic.getJSONObject("relative");
            currentKey = "relative.x";
            expectedType = Integer.class;
            if (!relative.has("x") || !expectedType.isInstance(relative.get("x"))) {
                throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
            }
            currentKey = "relative.y";
            if (!relative.has("y") || !expectedType.isInstance(relative.get("y"))) {
                throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
            }
            currentKey = "relative.z";
            if (!relative.has("y") || !expectedType.isInstance(relative.get("z"))) {
                throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
            }
        }

        currentKey = "dimensions";
        expectedType = JSONObject.class;
        if (!schematic.has("dimensions")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(schematic.get("dimensions"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        JSONObject dimensions = schematic.getJSONObject("dimensions");
        currentKey = "dimensions.width";
        expectedType = Integer.class;
        if (!dimensions.has("width") || !expectedType.isInstance(dimensions.get("width"))) {
            throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
        }
        currentKey = "dimensions.length";
        if (!dimensions.has("length") || !expectedType.isInstance(dimensions.get("length"))) {
            throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
        }
        currentKey = "dimensions.height";
        if (!dimensions.has("height") || !expectedType.isInstance(dimensions.get("height"))) {
            throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
        }
    }

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
        return EXTENSION;
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
    }

    @Override
    @NotNull
    public TardisBlock getBlock(int x, int y, int z) {
        JSONArray levels = schematic.getJSONArray("input");
        JSONArray rows = levels.getJSONArray(y);
        JSONArray columns = rows.getJSONArray(x);
        JSONObject blockObject = columns.getJSONObject(z);
        return new TardisBlock(blockObject, new int[]{x, y, z});
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
        JSONArray blocks = schematic.getJSONArray("input");
        JSONArray level = blocks.getJSONArray(y);
        JSONArray row = level.getJSONArray(x);
        row.put(z, block);
    }

    public static class TardisBlock implements Block {

        private static final Logger LOGGER = LogManager.getLogger(TardisBlock.class);

        private final JSONObject blockObject;
        private int[] position;

        @Contract(pure = true)
        public TardisBlock(JSONObject blockObject, int[] position) {
            this.blockObject = blockObject;
            this.position = position;
        }

        @Override
        public int[] getPosition() {
            return position;
        }

        @Override
        public void setPosition(int x, int y, int z) {
            // TODO Set TARDIS schematic block positions. Is this method even that useful?
        }

        @Override
        public String getId() {
            String state = blockObject.getString("data");
            int nameEndIndex = state.length();
            if (state.contains("[")) {
                nameEndIndex = state.indexOf('[');
            } else if (state.contains("{")) {
                nameEndIndex = state.indexOf('{');
            }
            return state.substring(0, nameEndIndex);
        }

        @Override
        public void setId(@NotNull String id) {
            if (!id.contains(":")) {
                id = "minecraft:" + id;
            }
            blockObject.put("data", id + getPropertiesAsString());
        }

        @Override
        public CompoundTag getProperties() {
            String propertiesString = getPropertiesAsString();
            String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':');
            CompoundTag properties = new CompoundTag();
            try {
                properties = (CompoundTag) SNBTUtil.fromSNBT(replaced);
            } catch (StringIndexOutOfBoundsException ignored) {
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            return PropertyUtils.byteToString(properties);
        }

        @Override
        public void setProperties(CompoundTag properties) {
            String propertiesString = "";
            try {
                propertiesString = SNBTUtil.toSNBT(PropertyUtils.byteToString(properties));
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            try {
                setPropertiesAsString(propertiesString);
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }

        @Override
        public String getPropertiesAsString() {
            String state = blockObject.getString("data");
            return !state.substring(getId().length()).equals("") ? state.substring(getId().length()) : "[]";
        }

        @Override
        public void setPropertiesAsString(@NotNull String propertiesString) throws IOException {
            String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':');
            try {
                SNBTUtil.fromSNBT(replaced); // Check whether the SNBT is parsable
            } catch (StringIndexOutOfBoundsException ignored) {
            }
            blockObject.put("data", getId() + propertiesString);
        }

        @Override
        public CompoundTag getNbt() {
            throw new UnsupportedOperationException("NBT storage is not supported by the TSCHM format.");
        }

        @Override
        public void setNbt(CompoundTag nbt) {
            throw new UnsupportedOperationException("NBT storage is not supported by the TSCHM format.");
        }

        @Override
        public String getSnbt() {
            throw new UnsupportedOperationException("NBT storage is not supported by the TSCHM format.");
        }

        @Override
        public void setSnbt(String snbt) {
            throw new UnsupportedOperationException("NBT storage is not supported by the TSCHM format.");
        }
    }
}
