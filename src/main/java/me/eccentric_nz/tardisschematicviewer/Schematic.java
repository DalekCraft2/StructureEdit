package me.eccentric_nz.tardisschematicviewer;

import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

public interface Schematic {

    Object getData();

    String getFormat();

    int[] getSize();

    void setSize(int x, int y, int z);

    Object getBlock(int x, int y, int z);

    void setBlock(int x, int y, int z, Object block);

    String getBlockId(Object block);

    void setBlockId(Object block, String id);

    CompoundTag getBlockProperties(Object block);

    void setBlockProperties(Object block, CompoundTag properties);

    String getBlockPropertiesAsString(Object block);

    void setBlockPropertiesAsString(Object block, String propertiesString) throws IOException;
}
