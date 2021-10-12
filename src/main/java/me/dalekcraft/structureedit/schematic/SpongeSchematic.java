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
    public @NotNull SpongeBlock getBlock(int x, int y, int z) {

        int[] size = getSize();
        byte[] blocks = getBlockList().getValue();
        for (int i = 0; i < blocks.length; i++) {
            byte block = blocks[i];
            // index = x + (y * length * width) + (z * width)
            int x1 = (i % (size[0] * size[2])) % size[0];
            int y1 = i / (size[0] * size[2]);
            int z1 = (i % (size[0] * size[2])) / size[0];
            if (x == x1 && y == y1 && z == z1) {

            }
        }
        CompoundTag blockEntityTag = null;
        for (CompoundTag block : getBlockEntityList()) {
            IntArrayTag positionTag = block.getIntArrayTag("Pos");
            int[] position = positionTag.getValue();
            if (position[0] == x && position[1] == y && position[2] == z) {
                blockEntityTag = block;
                break;
            }
        }
        return new SpongeBlock(blockEntityTag, this, new int[]{x, y, z});
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {

    }

    @Override
    public SpongePalette getPalette() {
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
