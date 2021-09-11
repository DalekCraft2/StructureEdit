package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import me.eccentric_nz.tardisschematicviewer.util.BlockStateUtils;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

public class Pane {

    public static void draw(GL4bc gl, Color color, float thickness, float sizeX, float sizeY, float sizeZ, Object properties) {

        gl.glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(gl, color, thickness, sizeY, thickness);

        CompoundTag tag = null;

        if (properties instanceof String) {
            tag = BlockStateUtils.toTag((String) properties);
        } else if (properties instanceof CompoundTag) {
            tag = (CompoundTag) properties;
        }
        if (tag.getBoolean("south")) {
            gl.glTranslatef(0.0f, 0.0f, thickness * 4.5f);
            Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 0.0f, -thickness * 4.5f);
        }
        if (tag.getBoolean("east")) {
            gl.glTranslatef(thickness * 4.5f, 0.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
            gl.glTranslatef(-thickness * 4.5f, 0.0f, 0.0f);
        }
        if (tag.getBoolean("north")) {
            gl.glTranslatef(0.0f, 0.0f, -thickness * 4.5f);
            Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 0.0f, thickness * 4.5f);
        }
        if (tag.getBoolean("west")) {
            gl.glTranslatef(-thickness * 4.5f, 0.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
            gl.glTranslatef(thickness * 4.5f, 0.0f, 0.0f);
        }
    }
}
