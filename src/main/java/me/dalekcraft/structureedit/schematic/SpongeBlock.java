package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

// TODO Optimize this entire class. god this format is killing me
public class SpongeBlock implements Block {

    private static final Logger LOGGER = LogManager.getLogger(SpongeSchematic.class);

    private final CompoundTag blockEntityTag;
    private final SpongeSchematic schematic;
    private int[] position;

    @Contract(pure = true)
    public SpongeBlock(CompoundTag blockEntityTag, SpongeSchematic schematic, int[] position) {
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
        SpongePalette palette = schematic.getPalette();
        String state = palette.getState(getState());
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
        CompoundTag palette = schematic.getPalette().getData();
        Set<Map.Entry<String, Tag<?>>> entrySet = palette.entrySet();
        for (Map.Entry<String, Tag<?>> tagEntry : entrySet) {
            if (((IntTag) tagEntry.getValue()).asInt() == getState()) {
                String tagName = tagEntry.getKey();
                palette.put(id + getPropertiesAsString(), palette.remove(tagName));
                break;
            }
        }
        CompoundTag nbt = getNbt();
        if (nbt != null) {
            nbt.putString("Id", id);
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
        SpongePalette palette = schematic.getPalette();
        String state = palette.getState(getState());
        return !state.substring(getId().length()).equals("") ? state.substring(getId().length()) : "[]";
    }

    @Override
    public void setPropertiesAsString(String propertiesString) throws IOException {
        CompoundTag palette = schematic.getPalette().getData();
        Set<Map.Entry<String, Tag<?>>> entrySet = palette.entrySet();
        for (Map.Entry<String, Tag<?>> tagEntry : entrySet) {
            if (((IntTag) tagEntry.getValue()).asInt() == getState()) {
                String tagName = tagEntry.getKey();
                String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':').replace("\"", "");
                try {
                    SNBTUtil.fromSNBT(replaced); // Check whether the SNBT is parsable
                } catch (StringIndexOutOfBoundsException ignored) {
                }
                palette.put(getId() + propertiesString, palette.remove(tagName));
                break;
            }
        }
    }

    @Override
    public CompoundTag getNbt() {
        return blockEntityTag;
    }

    @Override
    public void setNbt(@NotNull CompoundTag nbt) {
        if (!nbt.containsKey("Id")) {
            nbt.putString("Id", getId());
        }
        if (!nbt.containsKey("Pos")) {
            nbt.putIntArray("Pos", position);
        }
        ListTag<CompoundTag> blockEntityList = schematic.getBlockEntityList();
        for (CompoundTag block : blockEntityList) {
            IntArrayTag positionTag = block.getIntArrayTag("Pos");
            int[] position = positionTag.getValue();
            if (Arrays.equals(this.position, position)) {
                CompoundTag clone = nbt.clone();
                clone.remove("Id");
                clone.remove("Pos");
                if (clone.entrySet().isEmpty()) {
                    blockEntityList.remove(blockEntityList.indexOf(block));
                } else {
                    blockEntityList.set(blockEntityList.indexOf(block), nbt);
                }
                return;
            }
        }
        blockEntityList.add(nbt);
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
        CompoundTag nbt = getNbt() == null ? new CompoundTag() : getNbt();
        try {
            nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setNbt(nbt);
    }

    @Override
    public int getState() {
        int[] size = schematic.getSize();
        byte[] blocks = schematic.getBlockList().getValue();
        for (int i = 0; i < blocks.length; i++) {
            byte block = blocks[i];
            // index = x + (y * length * width) + (z * width)
            int x1 = (i % (size[0] * size[2])) % size[0];
            int y1 = i / (size[0] * size[2]);
            int z1 = (i % (size[0] * size[2])) / size[0];
            int[] position = {x1, y1, z1};
            if (Arrays.equals(this.position, position)) {
                return block;
            }
        }
        return -1;
    }

    @Override
    public void setState(int state) {
        int[] size = schematic.getSize();
        ByteArrayTag blocks = schematic.getBlockList();
        byte[] blocksArray = blocks.getValue();
        for (int i = 0; i < blocksArray.length; i++) {
            // index = x + (y * length * width) + (z * width)
            int x1 = (i % (size[0] * size[2])) % size[0];
            int y1 = i / (size[0] * size[2]);
            int z1 = (i % (size[0] * size[2])) / size[0];
            int[] position = {x1, y1, z1};
            if (Arrays.equals(this.position, position)) {
                blocksArray[i] = (byte) state;
                blocks.setValue(blocksArray);
            }
        }
    }
}
