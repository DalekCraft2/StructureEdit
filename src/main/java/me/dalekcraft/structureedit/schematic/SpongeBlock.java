package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.ListTag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class SpongeBlock implements Block {

    private static final Logger LOGGER = LogManager.getLogger(SpongeSchematic.class);
    private final SpongeSchematic schematic;
    private CompoundTag blockEntityTag;
    private String state;
    private int[] position;

    @Contract(pure = true)
    public SpongeBlock(String state, CompoundTag blockEntityTag, SpongeSchematic schematic, int[] position) {
        this.state = state;
        this.blockEntityTag = blockEntityTag;
        this.schematic = schematic;
        this.position = position;
    }

    @Override
    public int[] getPosition() {
        return position;
    }

    @Override
    public void setPosition(int x, int y, int z) {
        // TODO Same as TardisBlock's to-do thing.
    }

    @Override
    public String getId() {
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
        SpongePalette palette = schematic.getPalette();
        state = id + getPropertiesAsString();
        palette.setState(getStateIndex(), state);

        CompoundTag nbt = getNbt();
        if (nbt != null) {
            CompoundTag clone = nbt.clone();
            clone.remove("Id");
            clone.remove("Pos");
            if (clone.entrySet().isEmpty()) {
                nbt.putString("Id", id);
            }
        }
    }

    @Override
    public CompoundTag getProperties() {
        String properties = getPropertiesAsString();
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
        return !state.substring(getId().length()).equals("") ? state.substring(getId().length()) : "[]";
    }

    @Override
    public void setPropertiesAsString(String propertiesString) throws IOException {
        SpongePalette palette = schematic.getPalette();
        String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':').replace("\"", "");
        try {
            SNBTUtil.fromSNBT(replaced); // Check whether the SNBT is parsable
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        state = getId() + propertiesString;
        palette.setState(getStateIndex(), state);
    }

    @Override
    public CompoundTag getNbt() {
        return blockEntityTag;
    }

    @Override
    public void setNbt(CompoundTag nbt) {
        ListTag<CompoundTag> blockEntityList = schematic.getBlockEntityList();
        for (CompoundTag block : blockEntityList) {
            if (block.containsKey("Pos")) {
                IntArrayTag positionTag = block.getIntArrayTag("Pos");
                int[] position = positionTag.getValue();
                if (Arrays.equals(this.position, position)) {
                    if (nbt != null) {
                        CompoundTag clone = nbt.clone();
                        clone.remove("Id");
                        clone.remove("Pos");
                        if (clone.entrySet().isEmpty()) {
                            blockEntityTag = null;
                            blockEntityList.remove(blockEntityList.indexOf(block));
                        } else {
                            blockEntityTag = nbt;
                            blockEntityList.set(blockEntityList.indexOf(block), nbt);
                        }
                    } else {
                        blockEntityTag = null;
                        blockEntityList.remove(blockEntityList.indexOf(block));
                    }
                    return;
                }
            }
        }
        if (nbt != null) {
            nbt.remove("Id");
            nbt.remove("Pos");
            if (!nbt.entrySet().isEmpty()) {
                nbt.putString("Id", getId());
                nbt.putIntArray("Pos", position);
                blockEntityTag = nbt;
                blockEntityList.add(nbt);
            }
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
    public int getStateIndex() {
        int[] size = schematic.getSize();
        int[] position = getPosition();
        // index = x + (y * length * width) + (z * width)
        int index = position[0] + (position[1] * size[2] * size[0]) + (position[2] * size[0]);
        return schematic.getBlockList().getValue()[index];
    }

    @Override
    public void setStateIndex(int state) {
        int[] size = schematic.getSize();
        int[] position = getPosition();
        // index = x + (y * length * width) + (z * width)
        int index = position[0] + (position[1] * size[2] * size[0]) + (position[2] * size[0]);
        schematic.getBlockList().getValue()[index] = (byte) state;
    }
}
