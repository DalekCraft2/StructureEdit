package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SpongeV3Schematic extends SpongeV2Schematic {

    public SpongeV3Schematic(@NotNull NamedTag schematic) throws IOException {
        super(schematic);
    }

    @Override
    protected CompoundTag initializeRoot() throws IOException {
        if (schematic.getTag() instanceof CompoundTag compoundTag) {
            return compoundTag.getCompoundTag("Schematic");
        } else {
            throw new IOException("Not a schematic file");
        }
    }

    @Override
    @NotNull
    public SpongePalette getPalette() {
        return new SpongePalette(root.getCompoundTag("Blocks").getCompoundTag("Palette"));
    }

    @Override
    public void setPalette(Palette palette) {
        root.getCompoundTag("Blocks").put("Palette", ((SpongePalette) palette).getData());
    }

    @Override
    public ByteArrayTag getBlockList() {
        return root.getCompoundTag("Blocks").getByteArrayTag("Data");
    }

    @Override
    public void setBlockList(ByteArrayTag blocks) {
        root.getCompoundTag("Blocks").put("Data", blocks);
    }

    @Override
    public ListTag<CompoundTag> getBlockEntityList() {
        return root.getCompoundTag("Blocks").getListTag("BlockEntities").asCompoundTagList();
    }

    @Override
    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        root.getCompoundTag("Blocks").put("BlockEntities", blockEntities);
    }
}
