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

public class SpongeSchematic implements Schematic {

    private final NamedTag schematic;
    private final CompoundTag root;

    public SpongeSchematic(NamedTag schematic) throws IOException {
        this.schematic = schematic;
        if (schematic.getTag() instanceof CompoundTag compoundTag) {
            if (compoundTag.size() == 1 && compoundTag.containsKey("Schematic")) {
                root = compoundTag.getCompoundTag("Schematic");
            } else {
                root = compoundTag;
            }
        } else {
            throw new IOException("Not a schematic file");
        }
    }

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
        return new int[]{root.getShort("Width"), root.getShort("Height"), root.getShort("Length")};
    }

    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        root.putShort("Width", (short) sizeX);
        root.putShort("Height", (short) sizeY);
        root.putShort("Length", (short) sizeZ);
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
        if (getVersion() == 3) {
            return new SpongePalette(root.getCompoundTag("Blocks").getCompoundTag("Palette"));
        }
        return new SpongePalette(root.getCompoundTag("Palette")); // Versions 1 and 2
    }

    @Override
    public void setPalette(Palette palette) {
        if (getVersion() == 3) {
            root.getCompoundTag("Blocks").put("Palette", ((SpongePalette) palette).getData());
        } else {
            root.put("Palette", ((SpongePalette) palette).getData()); // Versions 1 and 2
        }
    }

    public ByteArrayTag getBlockList() {
        if (getVersion() == 3) {
            return root.getCompoundTag("Blocks").getByteArrayTag("Data");
        }
        return root.getByteArrayTag("BlockData"); // Versions 1 and 2
    }

    public void setBlockList(ByteArrayTag blocks) {
        if (getVersion() == 3) {
            root.getCompoundTag("Blocks").put("Data", blocks);
        } else {
            root.put("BlockData", blocks); // Versions 1 and 2
        }
    }

    public ListTag<CompoundTag> getBlockEntityList() {
        if (getVersion() == 3) {
            return root.getCompoundTag("Blocks").getListTag("BlockEntities").asCompoundTagList();
        }
        return root.getListTag("BlockEntities").asCompoundTagList(); // Versions 1 and 2
    }

    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        if (getVersion() == 3) {
            root.getCompoundTag("Blocks").put("BlockEntities", blockEntities);
        } else {
            root.put("BlockEntities", blockEntities); // Versions 1 and 2
        }
    }

    // TODO Change how methods work depending on which Sponge schematic version is used by the schematic.
    public int getVersion() {
        if (root.containsKey("Version")) {
            return root.getInt("Version");
        } else {
            return 1;
        }
    }
}
