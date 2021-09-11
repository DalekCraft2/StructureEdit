package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

public class WallStick {

    public static void draw(GL4bc gl, Color color, float sizeX, float sizeY, float sizeZ, Object properties) {

        // rotate if necessary
        float yaw = 0.0f;
        if (properties instanceof String) {
            if (((String) properties).contains("facing=")) {
                if (((String) properties).contains("facing=south")) {
                    yaw = 0.0f;
                } else if (((String) properties).contains("facing=east")) {
                    yaw = 90.0f;
                } else if (((String) properties).contains("facing=north")) {
                    yaw = 180.0f;
                } else if (((String) properties).contains("facing=west")) {
                    yaw = -90.0f;
                }
            }
        } else if (properties instanceof CompoundTag) {
            if (((CompoundTag) properties).containsKey("facing")) {
                if (((CompoundTag) properties).getString("facing").equals("south")) {
                    yaw = 0.0f;
                } else if (((CompoundTag) properties).getString("facing").equals("east")) {
                    yaw = 90.0f;
                } else if (((CompoundTag) properties).getString("facing").equals("north")) {
                    yaw = 180.0f;
                } else if (((CompoundTag) properties).getString("facing").equals("west")) {
                    yaw = -90.0f;
                }
            }
        }
        gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f);

        gl.glTranslatef(0.0f, 0.0f, sizeZ - 1.0f);

        Cube.draw(gl, color, sizeX, sizeY, sizeZ);
    }
}
