package me.eccentric_nz.tardisschematicviewer;

import net.querz.nbt.tag.CompoundTag;

public interface Schematic {

    Object getData();

    String getFormat();

    int[] getSize();

    void setSize(int x, int y, int z);

    Object getBlock(int x, int y, int z);

    void setBlock(int x, int y, int z, Object block);

    String getBlockId(int x, int y, int z);

    void setBlockId(int x, int y, int z, String id);

    CompoundTag getBlockProperties(int x, int y, int z);

    String getBlockPropertiesAsString(int x, int y, int z);

    void setBlockProperties(int x, int y, int z, CompoundTag properties);

    void setBlockPropertiesAsString(int x, int y, int z, String properties);
}
