package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

public class TardisBlock implements Block {

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
            propertiesString = SNBTUtil.toSNBT(PropertyUtils.byteToString(properties)).replace("\"", "");
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

    @Override
    public int getStateIndex() {
        throw new UnsupportedOperationException("Palettes are not supported by the TSCHM format.");
    }

    @Override
    public void setStateIndex(int state) {
        throw new UnsupportedOperationException("Palettes are not supported by the TSCHM format.");
    }
}
