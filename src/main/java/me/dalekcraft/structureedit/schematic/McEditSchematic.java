package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public record McEditSchematic(NamedTag schematic) implements Schematic {

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
        return EXTENSION_MCEDIT;
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

    @Override
    public Object getBlock(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, Object block) {

    }

    @Override
    public String getBlockId(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockId(int x, int y, int z, String id) {

    }

    @Override
    public CompoundTag getBlockProperties(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockProperties(int x, int y, int z, CompoundTag properties) {

    }

    @Override
    public String getBlockPropertiesAsString(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockPropertiesAsString(int x, int y, int z, String propertiesString) throws IOException {

    }

    @Override
    public CompoundTag getBlockNbt(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockNbt(int x, int y, int z, CompoundTag nbt) {

    }

    @Override
    public String getBlockSnbt(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockSnbt(int x, int y, int z, String snbt) throws IOException {

    }
}
