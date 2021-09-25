package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;

public record McEditSchematic(NamedTag schematic) implements Schematic {

    @Override
    public void saveTo(File file) throws IOException {
        NBTUtil.write(schematic, file);
    }

    @Override
    public Object getData() {
        return schematic;
    }

    @Override
    public String getFormat() {
        return EXTENSION_MCEDIT;
    }

    @Override
    public int[] getSize() {
        CompoundTag tag = (CompoundTag) schematic.getTag();
        return new int[]{tag.getInt("Width"), tag.getInt("Height"), tag.getInt("Length")};
    }

    @Override
    public void setSize(int x, int y, int z) {
        CompoundTag tag = (CompoundTag) schematic.getTag();
        tag.putInt("Width", x);
        tag.putInt("Height", y);
        tag.putInt("Length", z);
    }

    @Override
    public Object getBlock(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, Object block) {

    }

    @Override
    public String getBlockId(Object block) {
        return null;
    }

    @Override
    public void setBlockId(Object block, String id) {

    }

    @Override
    public CompoundTag getBlockProperties(Object block) {
        return null;
    }

    @Override
    public void setBlockProperties(Object block, CompoundTag properties) {

    }

    @Override
    public String getBlockPropertiesAsString(Object block) {
        return null;
    }

    @Override
    public void setBlockPropertiesAsString(Object block, String propertiesString) throws IOException {

    }
}
