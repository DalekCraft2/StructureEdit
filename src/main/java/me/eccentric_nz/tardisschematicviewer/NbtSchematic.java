package me.eccentric_nz.tardisschematicviewer;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;

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
        for (CompoundTag block : getBlocks()) {
            int[] position = block.getIntArray("pos");
            if (position[0] == x && position[1] == y && position[2] == z) {
                return block;
            }
        }
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, Object block) {
        ListTag<CompoundTag> blocks = getBlocks();
        for (CompoundTag block1 : blocks) {
            int[] position = block1.getIntArray("pos");
            if (position[0] == x && position[1] == y && position[2] == z) {
                blocks.set(blocks.indexOf(block1), (CompoundTag) block);
                setBlocks(blocks);
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
    public CompoundTag getProperties(int x, int y, int z) {
        CompoundTag block = getBlock(x, y, z);
        return getState(block).getCompoundTag("Properties");
    }

    @Override
    public void setProperties(int x, int y, int z, Object properties) {
        CompoundTag state = getState(getBlock(x, y, z));
        state.put("Properties", (CompoundTag) properties);
    }

    public CompoundTag getNbt(int x, int y, int z) {
        return getBlock(x, y, z).getCompoundTag("nbt");
    }

    public void setNbt(int x, int y, int z, CompoundTag nbt) {
        CompoundTag block = getBlock(x, y, z);
        block.put("nbt", nbt);
    }

    public CompoundTag getState(CompoundTag block) {
        return getPalette().get(block.getInt("state"));
    }

    public void setState(CompoundTag block, CompoundTag state) {
        getPalette().set(block.getInt("state"), state);
    }

    public ListTag<CompoundTag> getBlocks() {
        return ((CompoundTag) schematic.getTag()).getListTag("blocks").asCompoundTagList();
    }

    public void setBlocks(ListTag<CompoundTag> blocks) {
        ((CompoundTag) schematic.getTag()).put("blocks", blocks);
    }

    public ListTag<CompoundTag> getPalette() {
        return ((CompoundTag) schematic.getTag()).getListTag("palette").asCompoundTagList();
    }

    public void setPalette(ListTag<CompoundTag> palette) {
        ((CompoundTag) schematic.getTag()).put("palette", palette);
    }

    public ListTag<CompoundTag> getPaletteEntry(int index) {
        return getPalettes().get(index).asCompoundTagList();
    }

    public void setPaletteEntry(int index, ListTag<CompoundTag> palette) {
        getPalettes().set(index, palette);
    }

    public ListTag<ListTag<?>> getPalettes() {
        return ((CompoundTag) schematic.getTag()).getListTag("palettes").asListTagList();
    }

    public void setPalettes(ListTag<ListTag<?>> palettes) {
        ((CompoundTag) schematic.getTag()).put("palettes", palettes);
    }

    public boolean hasPaletteList() {
        return ((CompoundTag) schematic.getTag()).containsKey("palettes");
    }
}
