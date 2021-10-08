package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public record NbtStructure(NamedTag schematic) implements Schematic {

    private static final Logger LOGGER = LogManager.getLogger(NbtStructure.class);

    @Override
    public void saveTo(File file) throws IOException {
        NBTUtil.write(schematic, file);
    }

    @Contract(pure = true)
    @Override
    public NamedTag getData() {
        return schematic;
    }

    @Contract(pure = true)
    @Override
    @NotNull
    public String getFormat() {
        return EXTENSION_NBT;
    }

    @Override
    public int @NotNull [] getSize() {
        ListTag<IntTag> size = ((CompoundTag) schematic.getTag()).getListTag("size").asIntTagList();
        return new int[]{size.get(0).asInt(), size.get(1).asInt(), size.get(2).asInt()};
    }

    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        ListTag<IntTag> size = ((CompoundTag) schematic.getTag()).getListTag("size").asIntTagList();
        size.set(0, new IntTag(sizeX));
        size.set(1, new IntTag(sizeY));
        size.set(2, new IntTag(sizeZ));
        ((CompoundTag) schematic.getTag()).put("size", size);
    }

    @Override
    @Nullable
    public CompoundTag getBlock(int x, int y, int z) {
        for (CompoundTag block : getBlockList()) {
            ListTag<IntTag> positionTag = block.getListTag("pos").asIntTagList();
            int[] position = new int[3];
            position[0] = positionTag.get(0).asInt();
            position[1] = positionTag.get(1).asInt();
            position[2] = positionTag.get(2).asInt();
            if (position[0] == x && position[1] == y && position[2] == z) {
                return block;
            }
        }
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, Object block) {
        ListTag<CompoundTag> blocks = getBlockList();
        for (CompoundTag block1 : blocks) {
            int[] position = block1.getIntArray("pos");
            if (position[0] == x && position[1] == y && position[2] == z) {
                blocks.set(blocks.indexOf(block1), (CompoundTag) block);
                setBlockList(blocks);
            }
        }
    }

    @Override
    public String getBlockId(int x, int y, int z) {
        return getState(x, y, z).getString("Name");
    }

    @Override
    public void setBlockId(int x, int y, int z, String id) {
        CompoundTag state = getState(x, y, z);
        state.putString("Name", id);
        setState(x, y, z, state);
    }

    public String getBlockId(int x, int y, int z, ListTag<CompoundTag> palette) {
        return getState(x, y, z, palette).getString("Name");
    }

    public void setBlockId(int x, int y, int z, String id, ListTag<CompoundTag> palette) {
        CompoundTag state = getState(x, y, z, palette);
        state.putString("Name", id);
        setState(x, y, z, state, palette);
    }

    @Override
    public CompoundTag getBlockProperties(int x, int y, int z) {
        if (getState(x, y, z).getCompoundTag("Properties") == null) {
            return new CompoundTag();
        }
        return PropertyUtils.byteToString(getState(x, y, z).getCompoundTag("Properties"));
    }

    @Override
    public void setBlockProperties(int x, int y, int z, CompoundTag properties) {
        CompoundTag state = getState(x, y, z);
        state.put("Properties", properties);
    }

    public CompoundTag getBlockProperties(int x, int y, int z, ListTag<CompoundTag> palette) {
        if (getState(x, y, z, palette).getCompoundTag("Properties") == null) {
            return new CompoundTag();
        }
        return PropertyUtils.byteToString(getState(x, y, z, palette).getCompoundTag("Properties"));
    }

    public void setBlockProperties(int x, int y, int z, CompoundTag properties, ListTag<CompoundTag> palette) {
        CompoundTag state = getState(x, y, z, palette);
        state.put("Properties", properties);
    }

    @Override
    public String getBlockPropertiesAsString(int x, int y, int z) {
        String propertiesString = "{}";
        CompoundTag properties = getBlockProperties(x, y, z) == null ? new CompoundTag() : getBlockProperties(x, y, z);
        try {
            propertiesString = SNBTUtil.toSNBT(properties).replace("\"", "");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return propertiesString;
    }

    @Override
    public void setBlockPropertiesAsString(int x, int y, int z, String propertiesString) throws IOException {
        CompoundTag properties = new CompoundTag();
        try {
            properties = (CompoundTag) SNBTUtil.fromSNBT(propertiesString);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setBlockProperties(x, y, z, properties);
    }

    public String getBlockPropertiesAsString(int x, int y, int z, ListTag<CompoundTag> palette) {
        String propertiesString = "{}";
        CompoundTag properties = getBlockProperties(x, y, z, palette) == null ? new CompoundTag() : getBlockProperties(x, y, z, palette);
        try {
            propertiesString = SNBTUtil.toSNBT(properties).replace("\"", "");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        return propertiesString;
    }

    public void setBlockPropertiesAsString(int x, int y, int z, String propertiesString, ListTag<CompoundTag> palette) throws IOException {
        CompoundTag properties = new CompoundTag();
        try {
            properties = (CompoundTag) SNBTUtil.fromSNBT(propertiesString);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setBlockProperties(x, y, z, properties, palette);
    }

    @Override
    public CompoundTag getBlockNbt(int x, int y, int z) {
        return getBlock(x, y, z).getCompoundTag("nbt");
    }

    @Override
    public void setBlockNbt(int x, int y, int z, CompoundTag nbt) {
        if (nbt != null && !nbt.entrySet().isEmpty()) {
            getBlock(x, y, z).put("nbt", nbt);
        } else {
            getBlock(x, y, z).remove("nbt");
        }
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

    public int getBlockState(int x, int y, int z) {
        return getBlock(x, y, z).getInt("state");
    }

    public void setBlockState(int x, int y, int z, int state) {
        getBlock(x, y, z).putInt("state", state);
    }

    public CompoundTag getState(int x, int y, int z) {
        return getPalette().get(getBlock(x, y, z).getInt("state"));
    }

    public void setState(int x, int y, int z, CompoundTag state) {
        getPalette().set(getBlock(x, y, z).getInt("state"), state);
    }

    public CompoundTag getState(int x, int y, int z, @NotNull ListTag<CompoundTag> palette) {
        return palette.get(getBlock(x, y, z).getInt("state"));
    }

    public void setState(int x, int y, int z, CompoundTag state, @NotNull ListTag<CompoundTag> palette) {
        palette.set(getBlock(x, y, z).getInt("state"), state);
    }

    public ListTag<CompoundTag> getBlockList() {
        return ((CompoundTag) schematic.getTag()).getListTag("blocks").asCompoundTagList();
    }

    public void setBlockList(ListTag<CompoundTag> blocks) {
        ((CompoundTag) schematic.getTag()).put("blocks", blocks);
    }

    public ListTag<CompoundTag> getPalette() {
        return ((CompoundTag) schematic.getTag()).getListTag("palette").asCompoundTagList();
    }

    public void setPalette(ListTag<CompoundTag> palette) {
        ((CompoundTag) schematic.getTag()).put("palette", palette);
    }

    public ListTag<CompoundTag> getPaletteListEntry(int index) {
        return getPaletteList().get(index).asCompoundTagList();
    }

    public void setPaletteListEntry(int index, ListTag<CompoundTag> palette) {
        getPaletteList().set(index, palette);
    }

    public ListTag<ListTag<?>> getPaletteList() {
        return ((CompoundTag) schematic.getTag()).getListTag("palettes").asListTagList();
    }

    public void setPaletteList(ListTag<ListTag<?>> palettes) {
        ((CompoundTag) schematic.getTag()).put("palettes", palettes);
    }

    public boolean hasPaletteList() {
        return ((CompoundTag) schematic.getTag()).containsKey("palettes");
    }
}
