package me.dalekcraft.structureedit.schematic;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public record McEditSchematic(NamedTag schematic) implements Schematic {

    public static final String EXTENSION = "schematic";

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
        return EXTENSION;
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
    public Block getBlock(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {

    }

    @Override
    public Palette getPalette() {
        return null;
    }

    @Override
    public void setPalette(Palette palette) {

    }
}
