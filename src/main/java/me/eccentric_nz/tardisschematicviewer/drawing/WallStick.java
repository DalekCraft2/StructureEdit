package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import me.eccentric_nz.tardisschematicviewer.util.BlockStateUtils;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

public class WallStick {

    public static void draw(GL4bc gl, Color color, float sizeX, float sizeY, float sizeZ, Object properties) {

        // rotate if necessary
        float yaw = 0.0f;

        CompoundTag tag = null;

        if (properties instanceof String) {
            tag = BlockStateUtils.toTag((String) properties);
        } else if (properties instanceof CompoundTag) {
            tag = (CompoundTag) properties;
        }
        if (tag.containsKey("facing")) {
            switch (tag.getString("facing")) {
                case "south" -> yaw = 0.0f;
                case "east" -> yaw = 90.0f;
                default -> yaw = 180.0f; // north
                case "west" -> yaw = -90.0f;
            }
        }
        gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f);

        gl.glTranslatef(0.0f, 0.0f, sizeZ - 1.0f);

        Cube.draw(gl, color, sizeX, sizeY, sizeZ);
    }
}
