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

public class SpongeSchematic implements PaletteSchematic {

    public static final String EXTENSION = "schem";
    protected final NamedTag schematic;
    protected final CompoundTag root;

    public SpongeSchematic(@NotNull NamedTag schematic) throws IOException {
        this.schematic = schematic;
        root = initializeRoot();
    }

    @Contract("_ -> new")
    @NotNull
    public static SpongeSchematic getInstance(@NotNull NamedTag namedTag) throws IOException {
        if (namedTag.getTag() instanceof CompoundTag compoundTag) {
            if (compoundTag.containsKey("Schematic")) {
                int version = compoundTag.getCompoundTag("Schematic").getInt("Version");
                if (version == 3) {
                    return new SpongeV3Schematic(namedTag);
                }
                throw new IOException("Illegal schematic version " + version);
            } else {
                int version = compoundTag.getInt("Version");
                if (version == 1) {
                    return new SpongeSchematic(namedTag);
                } else if (version == 2) {
                    return new SpongeV2Schematic(namedTag);
                }
                throw new IOException("Illegal schematic version " + version);
            }
        } else {
            throw new IOException("Not a schematic file");
        }
    }

    protected CompoundTag initializeRoot() throws IOException {
        if (schematic.getTag() instanceof CompoundTag compoundTag) {
            return compoundTag;
        } else {
            throw new IOException("Not a schematic file");
        }
    }

    @Override
    public void saveTo(File file) throws IOException {
        NBTUtil.write(schematic, file);
    }

    @Override
    public NamedTag getData() {
        return schematic;
    }

    @Override
    public String getFormat() {
        return EXTENSION;
    }

    @Override
    public int @NotNull [] getSize() {
        return new int[]{root.getShort("Width"), root.getShort("Height"), root.getShort("Length")};
    }

    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        root.putShort("Width", (short) sizeX);
        root.putShort("Height", (short) sizeY);
        root.putShort("Length", (short) sizeZ);
    }

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
        // TODO This.
    }

    @Override
    @NotNull
    public SpongePalette getPalette() {
        return new SpongePalette(root.getCompoundTag("Palette"));
    }

    @Override
    public void setPalette(Palette palette) {
        root.put("Palette", ((SpongePalette) palette).getData());
    }

    public ByteArrayTag getBlockList() {
        return root.getByteArrayTag("BlockData");
    }

    public void setBlockList(ByteArrayTag blocks) {
        root.put("BlockData", blocks);
    }

    public ListTag<CompoundTag> getBlockEntityList() {
        return root.getListTag("TileEntities").asCompoundTagList();
    }

    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        root.put("TileEntities", blockEntities);
    }

    public int getVersion() {
        return root.getInt("Version");
    }
}
