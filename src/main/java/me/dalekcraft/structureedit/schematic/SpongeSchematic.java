package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public record SpongeSchematic(NamedTag schematic) implements Schematic {

    @Override
    public void saveTo(File file) throws IOException {
        NBTUtil.write(schematic, file);
    }

    @Override
    public Object getData() {
        return schematic;
    }

    @Override
    public String getFormat() {
        return EXTENSION_SPONGE;
    }

    @Override
    public int[] getSize() {
        CompoundTag tag = (CompoundTag) schematic.getTag();
        return new int[]{tag.getShort("Width"), tag.getShort("Height"), tag.getShort("Length")};
    }

    @Override
    public void setSize(int x, int y, int z) {
        CompoundTag tag = (CompoundTag) schematic.getTag();
        tag.putShort("Width", (short) x);
        tag.putShort("Height", (short) y);
        tag.putShort("Length", (short) z);
    }

    @Override
    @Nullable
    public Object getBlock(int x, int y, int z) {
        int[] size = getSize();
        byte[] blocks = getBlockList().getValue();
        for (int i = 0; i < blocks.length; i++) {
            Byte block = blocks[i];
            // index = (y * length * width) + (z * width) + x
            int x1 = (i % (size[0] * size[2])) % size[0];
            int y1 = i / (size[0] * size[2]);
            int z1 = (i % (size[0] * size[2])) / size[0];
            if (x == x1 && y == y1 && z == z1) {
                return block;
            }
        }
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, Object block) {
        ByteArrayTag blocks = getBlockList();
        byte[] blocksArray = blocks.getValue();
        for (Byte block1 : blocksArray) {
            int[] size = getSize();
            Byte blockIndex = (byte) (x + z * size[0] + y * size[0] * size[2]);
            if (block1.equals(blockIndex)) {
                blocksArray[blockIndex] = (Byte) block;
                blocks.setValue(blocksArray);
            }
        }
    }

    @Override
    public String getBlockId(Object block) {
        String blockId = null;
        CompoundTag palette = getPalette();
        Set<Map.Entry<String, Tag<?>>> entrySet = palette.entrySet();
        for (Map.Entry<String, Tag<?>> tagEntry : entrySet) {
            if (((IntTag) tagEntry.getValue()).asInt() == (Byte) block) {
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
    public void setBlockId(Object block, String id) {
        CompoundTag palette = getPalette();
        Set<Map.Entry<String, Tag<?>>> entrySet = palette.entrySet();
        for (Map.Entry<String, Tag<?>> tagEntry : entrySet) {
            if (((IntTag) tagEntry.getValue()).asInt() == (Byte) block) {
                String tagName = tagEntry.getKey();
                palette.put(id + getBlockPropertiesAsString(block), palette.remove(tagName));
                return;
            }
        }
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
        String propertiesString = "[]";
        CompoundTag palette = getPalette();
        Set<String> keySet = palette.keySet();
        for (String tagName : keySet) {
            if (palette.getInt(tagName) == (Byte) block) {
                propertiesString = !tagName.substring(getBlockId(block).length()).equals("") ? tagName.substring(getBlockId(block).length()) : "[]";
            }
        }
        return propertiesString;
    }

    @Override
    public void setBlockPropertiesAsString(Object block, String propertiesString) throws IOException {
        CompoundTag palette = getPalette();
        Set<Map.Entry<String, Tag<?>>> entrySet = palette.entrySet();
        for (Map.Entry<String, Tag<?>> tagEntry : entrySet) {
            if (((IntTag) tagEntry.getValue()).asInt() == (Byte) block) {
                String tagName = tagEntry.getKey();
                String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':');
                try {
                    SNBTUtil.fromSNBT(replaced); // Check whether the SNBT is parsable
                    palette.put(getBlockId(block) + propertiesString, palette.remove(tagName));
                } catch (StringIndexOutOfBoundsException ignored) {
                }
                return;
            }
        }
    }

    //    public CompoundTag getBlockNbt(Byte block) {
    //        return block.getCompoundTag("nbt");
    //    }
    //
    //    public void setBlockNbt(Byte block, CompoundTag nbt) {
    //        if (nbt != null && !nbt.entrySet().isEmpty()) {
    //            block.put("nbt", nbt);
    //        } else {
    //            block.remove("nbt");
    //        }
    //    }
    //
    //    public String getBlockSnbt(Byte block) {
    //        String snbt = "{}";
    //        CompoundTag nbt = getBlockNbt(block) == null ? new CompoundTag() : getBlockNbt(block);
    //        try {
    //            snbt = SNBTUtil.toSNBT(nbt);
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        return snbt;
    //    }
    //
    //    public void setBlockSnbt(Byte block, String snbt) throws IOException {
    //        CompoundTag nbt = getBlockNbt(block) == null ? new CompoundTag() : getBlockNbt(block);
    //        try {
    //            nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
    //        } catch (StringIndexOutOfBoundsException ignored) {
    //        }
    //        setBlockNbt(block, nbt);
    //    }

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

    public CompoundTag getPalette() {
        return ((CompoundTag) schematic.getTag()).getCompoundTag("Palette");
    }

    public void setPalette(CompoundTag palette) {
        ((CompoundTag) schematic.getTag()).put("Palette", palette);
    }

    // TODO Change how methods work depending on which Sponge schematic version is used by the schematic.
    public int getVersion() {
        return ((CompoundTag) schematic.getTag()).getInt("Version");
    }
}
