package me.eccentric_nz.tardisschematicviewer;

public interface Schematic {

    String getFormat();

    int[] getSize();

    void setSize(int x, int y, int z);

    Object getBlock(int x, int y, int z);

    void setBlock(int x, int y, int z, Object block);

    String getBlockId(int x, int y, int z);

    void setBlockId(int x, int y, int z, String id);

    Object getProperties(int x, int y, int z);

    void setProperties(int x, int y, int z, Object properties);
}
