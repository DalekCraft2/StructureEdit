package me.eccentric_nz.tardisschematicviewer.drawing;

import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

import static org.lwjgl.opengl.GL46.glTranslatef;

public class Pane {

    public static void draw(Color color, float thickness, float sizeX, float sizeY, float sizeZ, CompoundTag properties) {

        glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(color, thickness, sizeY, thickness);

        if (properties.containsKey("south") && properties.getString("south").equals("true")) {
            glTranslatef(0.0f, 0.0f, thickness * 4.5f);
            Cube.draw(color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
            glTranslatef(0.0f, 0.0f, -thickness * 4.5f);
        }
        if (properties.containsKey("east") && properties.getString("east").equals("true")) {
            glTranslatef(thickness * 4.5f, 0.0f, 0.0f);
            Cube.draw(color, (sizeX - thickness) / 2.0f, sizeY, thickness);
            glTranslatef(-thickness * 4.5f, 0.0f, 0.0f);
        }
        if (properties.containsKey("north") && properties.getString("north").equals("true")) {
            glTranslatef(0.0f, 0.0f, -thickness * 4.5f);
            Cube.draw(color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
            glTranslatef(0.0f, 0.0f, thickness * 4.5f);
        }
        if (properties.containsKey("west") && properties.getString("west").equals("true")) {
            glTranslatef(-thickness * 4.5f, 0.0f, 0.0f);
            Cube.draw(color, (sizeX - thickness) / 2.0f, sizeY, thickness);
            glTranslatef(thickness * 4.5f, 0.0f, 0.0f);
        }
    }
}
