package me.eccentric_nz.tardisschematicviewer;

import me.eccentric_nz.tardisschematicviewer.util.BlockStateUtils;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;

public class NbtSchematic implements Schematic {

    private final NamedTag schematic;

    public NbtSchematic(NamedTag schematic) {
        this.schematic = schematic;
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
    public String getBlockId(int x, int y, int z) {
        return getState(getBlock(x, y, z)).getString("Name");
    }

    @Override
    public void setBlockId(int x, int y, int z, String id) {
        CompoundTag block = getBlock(x, y, z);
        CompoundTag state = getState(block);
        state.putString("Name", id);
        setState(block, state);
    }

    @Override
    public CompoundTag getBlockProperties(int x, int y, int z) {
        CompoundTag block = getBlock(x, y, z);
        return getState(block).getCompoundTag("Properties");
    }

    @Override
    public String getBlockPropertiesAsString(int x, int y, int z) {
        return BlockStateUtils.fromTag(getBlockProperties(x, y, z), false);
    }

    @Override
    public void setBlockProperties(int x, int y, int z, CompoundTag properties) {
        CompoundTag state = getState(getBlock(x, y, z));
        state.put("Properties", properties);
    }

    @Override
    public void setBlockPropertiesAsString(int x, int y, int z, String properties) {

    }

    public CompoundTag getBlockNbt(int x, int y, int z) {
        return getBlock(x, y, z).getCompoundTag("nbt");
    }

    public void setBlockNbt(int x, int y, int z, CompoundTag nbt) {
        CompoundTag block = getBlock(x, y, z);
        if (nbt != null && !nbt.entrySet().isEmpty()) {
            block.put("nbt", nbt);
        } else {
            block.remove("nbt");
        }
    }

    public int getBlockState(int x, int y, int z) {
        return getBlock(x, y, z).getInt("state");
    }

    public void setBlockState(int x, int y, int z, int state) {
        CompoundTag block = getBlock(x, y, z);
        block.putInt("state", state);
    }

    public CompoundTag getState(CompoundTag block) {
        return getPalette().get(block.getInt("state"));
    }

    public void setState(CompoundTag block, CompoundTag state) {
        getPalette().set(block.getInt("state"), state);
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
