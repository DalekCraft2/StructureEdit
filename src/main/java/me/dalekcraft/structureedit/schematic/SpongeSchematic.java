package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public record SpongeSchematic(NamedTag schematic) implements Schematic {

    private static final Logger LOGGER = LogManager.getLogger(SpongeSchematic.class);

    @Override
    public void saveTo(File file) throws IOException {
        NBTUtil.write(schematic, file);
    }

    @Contract(pure = true)
    @Override
    public Object getData() {
        return schematic;
    }

    @Contract(pure = true)
    @Override
    public String getFormat() {
        return EXTENSION_SPONGE;
    }

    @Override
    public int @NotNull [] getSize() {
        CompoundTag tag = (CompoundTag) schematic.getTag();
        return new int[]{tag.getShort("Width"), tag.getShort("Height"), tag.getShort("Length")};
    }

    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        CompoundTag tag = (CompoundTag) schematic.getTag();
        tag.putShort("Width", (short) sizeX);
        tag.putShort("Height", (short) sizeY);
        tag.putShort("Length", (short) sizeZ);
    }

    @Override
    @Nullable
    public Integer getBlock(int x, int y, int z) {
        return getBlockState(x, y, z);
    }

    @Override
    public void setBlock(int x, int y, int z, Object block) {
        if (block instanceof Integer blockInt) {
            setBlockState(x, y, z, blockInt);
        }
        throw new IllegalArgumentException("Block must be a number");
    }

    @Override
    public String getBlockId(int x, int y, int z) {
        String blockId = null;
        CompoundTag palette = getPalette();
        Set<Map.Entry<String, Tag<?>>> entrySet = palette.entrySet();
        for (Map.Entry<String, Tag<?>> tagEntry : entrySet) {
            if (((IntTag) tagEntry.getValue()).asInt() == getBlockState(x, y, z)) {
                String tagName = tagEntry.getKey();
                int nameEndIndex = tagName.length();
                if (tagName.contains("[")) {
                    nameEndIndex = tagName.indexOf('[');
                } else if (tagName.contains("{")) {
                    nameEndIndex = tagName.indexOf('{');
                }
                blockId = tagName.substring(0, nameEndIndex);
                break;
            }
        }
        return blockId;
    }

    @Override
    public void setBlockId(int x, int y, int z, String id) {
        CompoundTag palette = getPalette();
        Set<Map.Entry<String, Tag<?>>> entrySet = palette.entrySet();
        for (Map.Entry<String, Tag<?>> tagEntry : entrySet) {
            if (((IntTag) tagEntry.getValue()).asInt() == getBlockState(x, y, z)) {
                String tagName = tagEntry.getKey();
                palette.put(id + getBlockPropertiesAsString(x, y, z), palette.remove(tagName));
                break;
            }
        }
        CompoundTag nbt = getBlockNbt(x, y, z);
        if (nbt != null) {
            nbt.putString("Id", id);
        }
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
    public String getBlockPropertiesAsString(int x, int y, int z) {
        String propertiesString = "[]";
        CompoundTag palette = getPalette();
        Set<String> keySet = palette.keySet();
        for (String tagName : keySet) {
            if (palette.getInt(tagName) == getBlockState(x, y, z)) {
                propertiesString = !tagName.substring(getBlockId(x, y, z).length()).equals("") ? tagName.substring(getBlockId(x, y, z).length()) : "[]";
            }
        }
        return propertiesString;
    }

    @Override
    public void setBlockPropertiesAsString(int x, int y, int z, String propertiesString) throws IOException {
        CompoundTag palette = getPalette();
        Set<Map.Entry<String, Tag<?>>> entrySet = palette.entrySet();
        for (Map.Entry<String, Tag<?>> tagEntry : entrySet) {
            if (((IntTag) tagEntry.getValue()).asInt() == getBlockState(x, y, z)) {
                String tagName = tagEntry.getKey();
                String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':').replace("\"", "");
                try {
                    SNBTUtil.fromSNBT(replaced); // Check whether the SNBT is parsable
                    palette.put(getBlockId(x, y, z) + propertiesString, palette.remove(tagName));
                } catch (StringIndexOutOfBoundsException ignored) {
                }
                break;
            }
        }
    }


    @Override
    @Nullable
    public CompoundTag getBlockNbt(int x, int y, int z) {
        for (CompoundTag block : getBlockEntityList()) {
            IntArrayTag positionTag = block.getIntArrayTag("Pos");
            int[] position = positionTag.getValue();
            if (position[0] == x && position[1] == y && position[2] == z) {
                return block;
            }
        }
        return null;
    }

    @Override
    public void setBlockNbt(int x, int y, int z, CompoundTag nbt) {
        ListTag<CompoundTag> blockEntityList = getBlockEntityList();
        for (CompoundTag block : blockEntityList) {
            IntArrayTag positionTag = block.getIntArrayTag("Pos");
            int[] position = positionTag.getValue();
            if (position[0] == x && position[1] == y && position[2] == z) {
                CompoundTag clone = nbt.clone();
                clone.remove("Id");
                clone.remove("Pos");
                if (clone.size() == 0) {
                    blockEntityList.remove(blockEntityList.indexOf(block));
                } else {
                    blockEntityList.set(blockEntityList.indexOf(block), nbt);
                }
                return;
            }
        }
        CompoundTag newNbt = new CompoundTag();
        newNbt.putString("Id", getBlockId(x, y, z));
        newNbt.putIntArray("Pos", new int[]{x, y, z});
        blockEntityList.add(newNbt);
    }

    @Override
    public String getBlockSnbt(int x, int y, int z) {
        String snbt = "{}";
        CompoundTag nbt = getBlockNbt(x, y, z) == null ? new CompoundTag() : getBlockNbt(x, y, z);
        try {
            snbt = SNBTUtil.toSNBT(nbt);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return snbt;
    }

    @Override
    public void setBlockSnbt(int x, int y, int z, String snbt) throws IOException {
        CompoundTag nbt = getBlockNbt(x, y, z) == null ? new CompoundTag() : getBlockNbt(x, y, z);
        try {
            nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setBlockNbt(x, y, z, nbt);
    }

    @Override
    public int getBlockState(int x, int y, int z) {
        int[] size = getSize();
        byte[] blocks = getBlockList().getValue();
        for (int i = 0; i < blocks.length; i++) {
            byte block = blocks[i];
            // index = x + (y * length * width) + (z * width)
            int x1 = (i % (size[0] * size[2])) % size[0];
            int y1 = i / (size[0] * size[2]);
            int z1 = (i % (size[0] * size[2])) / size[0];
            if (x == x1 && y == y1 && z == z1) {
                return block;
            }
        }
        return -1;
    }

    @Override
    public void setBlockState(int x, int y, int z, int state) {
        int[] size = getSize();
        ByteArrayTag blocks = getBlockList();
        byte[] blocksArray = blocks.getValue();
        for (int i = 0; i < blocksArray.length; i++) {
            // index = x + (y * length * width) + (z * width)
            int x1 = (i % (size[0] * size[2])) % size[0];
            int y1 = i / (size[0] * size[2]);
            int z1 = (i % (size[0] * size[2])) / size[0];
            if (x == x1 && y == y1 && z == z1) {
                blocksArray[i] = (byte) state;
                blocks.setValue(blocksArray);
            }
        }
    }

    @Override
    public CompoundTag getState(int x, int y, int z) {
        return null;
    }

    @Override
    public void setState(int x, int y, int z, CompoundTag state) {

    }

    @Override
    public CompoundTag getPalette() {
        return ((CompoundTag) schematic.getTag()).getCompoundTag("Palette");
    }

    @Override
    public void setPalette(Tag<?> palette) {
        ((CompoundTag) schematic.getTag()).put("Palette", palette);
    }

    public ByteArrayTag getBlockList() {
        return ((CompoundTag) schematic.getTag()).getByteArrayTag("BlockData");
    }

    public void setBlockList(ByteArrayTag blocks) {
        ((CompoundTag) schematic.getTag()).put("BlockData", blocks);
    }

    public ListTag<CompoundTag> getBlockEntityList() {
        return ((CompoundTag) schematic.getTag()).getListTag("BlockEntities").asCompoundTagList();
    }

    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        ((CompoundTag) schematic.getTag()).put("BlockEntities", blockEntities);
    }

    // TODO Change how methods work depending on which Sponge schematic version is used by the schematic.
    public int getVersion() {
        return ((CompoundTag) schematic.getTag()).getInt("Version");
    }
}
