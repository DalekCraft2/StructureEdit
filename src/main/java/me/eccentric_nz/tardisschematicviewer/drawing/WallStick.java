package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

public class WallStick {

    public static void draw(GL4bc gl, Color color, float sizeX, float sizeY, float sizeZ, CompoundTag properties) {

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
        gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f);

        gl.glTranslatef(0.0f, 0.0f, sizeZ - 1.0f);

        Cube.draw(gl, color, sizeX, sizeY, sizeZ);
    }
}
