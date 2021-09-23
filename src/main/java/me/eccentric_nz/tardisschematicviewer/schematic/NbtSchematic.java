package me.eccentric_nz.tardisschematicviewer.schematic;

import me.eccentric_nz.tardisschematicviewer.util.PropertyUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;

import java.io.File;
import java.io.IOException;

public class NbtSchematic implements Schematic {

    private final NamedTag schematic;

    public NbtSchematic(NamedTag schematic) {
        this.schematic = schematic;
    }

    @Override
    public void saveTo(File file) throws IOException {
        NBTUtil.write(getData(), file);
    }

    @Override
    public NamedTag getData() {
        return schematic;
    }

    @Override
    public String getFormat() {
        return "nbt";
    }

    @Override
    public int[] getSize() {
        ListTag<IntTag> size = ((CompoundTag) schematic.getTag()).getListTag("size").asIntTagList();
        return new int[]{size.get(0).asInt(), size.get(1).asInt(), size.get(2).asInt()};
    }

    @Override
    public void setSize(int x, int y, int z) {
        ListTag<IntTag> size = ((CompoundTag) schematic.getTag()).getListTag("size").asIntTagList();
        size.set(0, new IntTag(x));
        size.set(1, new IntTag(y));
        size.set(2, new IntTag(z));
        ((CompoundTag) schematic.getTag()).put("size", size);
    }

    @Override
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
    public String getBlockId(Object block) {
        return getState((CompoundTag) block).getString("Name");
    }

    @Override
    public void setBlockId(Object block, String id) {
        CompoundTag state = getState((CompoundTag) block);
        state.putString("Name", id);
        setState((CompoundTag) block, state);
    }

    public String getBlockId(Object block, ListTag<CompoundTag> palette) {
        return getState((CompoundTag) block, palette).getString("Name");
    }

    public void setBlockId(Object block, String id, ListTag<CompoundTag> palette) {
        CompoundTag state = getState((CompoundTag) block, palette);
        state.putString("Name", id);
        setState((CompoundTag) block, state, palette);
    }

    @Override
    public CompoundTag getBlockProperties(Object block) {
        if (getState((CompoundTag) block).getCompoundTag("Properties") == null) {
            return new CompoundTag();
        }
        return PropertyUtils.byteToString(getState((CompoundTag) block).getCompoundTag("Properties"));
    }

    @Override
    public void setBlockProperties(Object block, CompoundTag properties) {
        CompoundTag state = getState((CompoundTag) block);
        state.put("Properties", properties);
    }

    public CompoundTag getBlockProperties(Object block, ListTag<CompoundTag> palette) {
        if (getState((CompoundTag) block, palette).getCompoundTag("Properties") == null) {
            return new CompoundTag();
        }
        return PropertyUtils.byteToString(getState((CompoundTag) block, palette).getCompoundTag("Properties"));
    }

    public void setBlockProperties(Object block, CompoundTag properties, ListTag<CompoundTag> palette) {
        CompoundTag state = getState((CompoundTag) block, palette);
        state.put("Properties", properties);
    }

    @Override
    public String getBlockPropertiesAsString(Object block) {
        String propertiesString = "{}";
        CompoundTag properties = getBlockProperties(block) == null ? new CompoundTag() : getBlockProperties(block);
        try {
            propertiesString = SNBTUtil.toSNBT(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propertiesString;
    }

    @Override
    public void setBlockPropertiesAsString(Object block, String propertiesString) throws IOException {
        CompoundTag properties = new CompoundTag();
        try {
            properties = (CompoundTag) SNBTUtil.fromSNBT(propertiesString);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setBlockProperties(block, properties);
    }

    public String getBlockPropertiesAsString(Object block, ListTag<CompoundTag> palette) {
        String propertiesString = "{}";
        CompoundTag properties = getBlockProperties(block, palette) == null ? new CompoundTag() : getBlockProperties(block, palette);
        try {
            propertiesString = SNBTUtil.toSNBT(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propertiesString;
    }

    public void setBlockPropertiesAsString(Object block, String propertiesString, ListTag<CompoundTag> palette) throws IOException {
        CompoundTag properties = new CompoundTag();
        try {
            properties = (CompoundTag) SNBTUtil.fromSNBT(propertiesString);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setBlockProperties(block, properties, palette);
    }

    public CompoundTag getBlockNbt(CompoundTag block) {
        return block.getCompoundTag("nbt");
    }

    public void setBlockNbt(CompoundTag block, CompoundTag nbt) {
        if (nbt != null && !nbt.entrySet().isEmpty()) {
            block.put("nbt", nbt);
        } else {
            block.remove("nbt");
        }
    }

    public String getBlockSnbt(CompoundTag block) {
        String snbt = "{}";
        CompoundTag nbt = getBlockNbt(block) == null ? new CompoundTag() : getBlockNbt(block);
        try {
            snbt = SNBTUtil.toSNBT(nbt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return snbt;
    }

    public void setBlockSnbt(CompoundTag block, String snbt) throws IOException {
        CompoundTag nbt = getBlockNbt(block) == null ? new CompoundTag() : getBlockNbt(block);
        try {
            nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        setBlockNbt(block, nbt);
    }

    public int getBlockState(CompoundTag block) {
        return block.getInt("state");
    }

    public void setBlockState(CompoundTag block, int state) {
        block.putInt("state", state);
    }

    public CompoundTag getState(CompoundTag block) {
        return getPalette().get(block.getInt("state"));
    }

    public void setState(CompoundTag block, CompoundTag state) {
        getPalette().set(block.getInt("state"), state);
    }

    public CompoundTag getState(CompoundTag block, ListTag<CompoundTag> palette) {
        return palette.get(block.getInt("state"));
    }

    public void setState(CompoundTag block, CompoundTag state, ListTag<CompoundTag> palette) {
        palette.set(block.getInt("state"), state);
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
