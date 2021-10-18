package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SpongeV2Schematic extends SpongeSchematic implements VersionedSchematic {

    public SpongeV2Schematic(@NotNull NamedTag schematic) throws IOException {
        super(schematic);
    }

    @Override
    public int getDataVersion() {
        return root.getInt("DataVersion");
    }

    @Override
    public ListTag<CompoundTag> getBlockEntityList() {
        return root.getListTag("BlockEntities").asCompoundTagList();
    }

    @Override
    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        root.put("BlockEntities", blockEntities);
    }
}
