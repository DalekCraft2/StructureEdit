package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public record SpongeSchematic(NamedTag schematic) implements Schematic {

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

    @Contract("_, _, _ -> new")
    @Override
    @NotNull
    public SpongeBlock getBlock(int x, int y, int z) {
        int[] size = getSize();
        // index = x + (y * length * width) + (z * width)
        int index = x + (y * size[2] * size[0]) + (z * size[0]);
        int stateIndex = getBlockList().getValue()[index];
        String state = getPalette().getState(stateIndex);
        CompoundTag blockEntityTag = null;
        for (CompoundTag blockEntity : getBlockEntityList()) {
            if (blockEntity.containsKey("Pos")) { // Should always have the position key.
                IntArrayTag positionTag = blockEntity.getIntArrayTag("Pos");
                int[] position = positionTag.getValue();
                if (position[0] == x && position[1] == y && position[2] == z) {
                    blockEntityTag = blockEntity;
                    break;
                }
            }
        }
        return new SpongeBlock(state, blockEntityTag, this, new int[]{x, y, z});
    }

    @Contract(pure = true)
    @Override
    public void setBlock(int x, int y, int z, Block block) {

    }

    @Contract(" -> new")
    @Override
    public @NotNull SpongePalette getPalette() {
        return new SpongePalette(((CompoundTag) schematic.getTag()).getCompoundTag("Palette"));
    }

    @Override
    public void setPalette(Palette palette) {
        ((CompoundTag) schematic.getTag()).put("Palette", ((SpongePalette) palette).getData());
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
