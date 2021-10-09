package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class NbtBlock implements Block {

    private static final Logger LOGGER = LogManager.getLogger(NbtBlock.class);

    private final CompoundTag blockTag;
    private final CompoundTag stateTag;

    public NbtBlock(CompoundTag blockTag, CompoundTag stateTag) {
        this.blockTag = blockTag;
        this.stateTag = stateTag;
    }

    @Override
    public int[] getPosition() {
        return blockTag.getIntArray("pos");
    }

    @Override
    public void setPosition(int x, int y, int z) {
        blockTag.putIntArray("pos", new int[]{x, y, z});
    }

    @Override
    public String getId() {
        return stateTag.getString("Name");
    }

    @Override
    public void setId(String id) {
        stateTag.putString("Name", id);
    }

    @Override
    public CompoundTag getProperties() {
        return PropertyUtils.byteToString(stateTag.getCompoundTag("Properties"));
    }

    @Override
    public void setProperties(CompoundTag properties) {
        if (properties != null && !properties.entrySet().isEmpty()) {
            stateTag.put("Properties", properties);
        } else {
            stateTag.remove("Properties");
        }
    }

    @Override
    public String getPropertiesAsString() {
        String propertiesString = "{}";
        CompoundTag properties = getProperties() == null ? new CompoundTag() : getProperties();
        try {
            propertiesString = SNBTUtil.toSNBT(properties).replace("\"", "");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return propertiesString;
    }

    @Override
    public void setPropertiesAsString(String propertiesString) throws IOException {
        CompoundTag properties = new CompoundTag();
        try {
            properties = (CompoundTag) SNBTUtil.fromSNBT(propertiesString);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setProperties(properties);
    }

    @Override
    public CompoundTag getNbt() {
        return blockTag.getCompoundTag("nbt");
    }

    @Override
    public void setNbt(CompoundTag nbt) {
        if (nbt != null && !nbt.entrySet().isEmpty()) {
            blockTag.put("nbt", nbt);
        } else {
            blockTag.remove("nbt");
        }
    }

    @Override
    public String getSnbt() {
        String snbt = "{}";
        CompoundTag nbt = getNbt() == null ? new CompoundTag() : getNbt();
        try {
            snbt = SNBTUtil.toSNBT(nbt);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return snbt;
    }

    @Override
    public void setSnbt(String snbt) throws IOException {
        CompoundTag nbt = new CompoundTag();
        try {
            nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setNbt(nbt);
    }

    @Override
    public int getState() {
        return blockTag.getInt("state");
    }

    @Override
    public void setState(int state) {
        blockTag.putInt("state", state);
    }
}
