package me.eccentric_nz.tardisschematicviewer.drawing;

import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

import static org.lwjgl.opengl.GL46.glRotatef;
import static org.lwjgl.opengl.GL46.glTranslatef;

public class WallStick {

    public static void draw(Color color, float sizeX, float sizeY, float sizeZ, CompoundTag properties) {

        // rotate if necessary
        float yaw = 180.0f;

        if (properties.containsKey("facing")) {
            switch (properties.getString("facing")) {
                case "south" -> yaw = 0.0f;
                case "east" -> yaw = 90.0f;
                default -> {
                } // north
                case "west" -> yaw = -90.0f;
            }
        }
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);

        glTranslatef(0.0f, 0.0f, sizeZ - 1.0f);

        Cube.draw(color, sizeX, sizeY, sizeZ);
    }
}
