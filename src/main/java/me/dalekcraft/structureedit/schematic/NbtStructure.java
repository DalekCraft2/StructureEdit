package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class NbtStructure implements Schematic {

    private final NamedTag schematic;
    private ListTag<CompoundTag> palette;

    public NbtStructure(NamedTag schematic) {
        this.schematic = schematic;
        if (hasPaletteList()) {
            palette = getPaletteListEntry(0);
        } else {
            palette = getPalette();
        }
    }

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
    public NbtBlock getBlock(int x, int y, int z) {
        for (CompoundTag block : getBlockList()) {
            ListTag<IntTag> positionTag = block.getListTag("pos").asIntTagList();
            int[] position = new int[3];
            position[0] = positionTag.get(0).asInt();
            position[1] = positionTag.get(1).asInt();
            position[2] = positionTag.get(2).asInt();
            if (position[0] == x && position[1] == y && position[2] == z) {
                CompoundTag state = getState(block.getInt("state"));
                return new NbtBlock(block, state);
            }
        }
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
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
    public CompoundTag getState(int index) {
        return palette.get(index);
    }

    @Override
    public void setState(int index, CompoundTag state) {
        palette.set(index, state);
    }

    public ListTag<CompoundTag> getBlockList() {
        return ((CompoundTag) schematic.getTag()).getListTag("blocks").asCompoundTagList();
    }

    public void setBlockList(ListTag<CompoundTag> blocks) {
        ((CompoundTag) schematic.getTag()).put("blocks", blocks);
    }

    @Override
    public ListTag<CompoundTag> getPalette() {
        return ((CompoundTag) schematic.getTag()).getListTag("palette").asCompoundTagList();
    }

    @Override
    public void setPalette(Tag<?> palette) {
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

    public void setActivePalette(int index) {
        palette = getPaletteListEntry(index);
    }
}
