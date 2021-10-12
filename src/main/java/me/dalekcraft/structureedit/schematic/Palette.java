package me.dalekcraft.structureedit.schematic;

public interface Palette {

    Object getData();

    int size();

    Object getState(int index);

    void setState(int index, Object state);
}
