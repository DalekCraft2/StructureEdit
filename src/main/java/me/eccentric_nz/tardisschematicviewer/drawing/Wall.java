package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import me.eccentric_nz.tardisschematicviewer.util.BlockStateUtils;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

public class Wall {

    public static void draw(GL4bc gl, Color color, float thickness, float sizeX, float sizeY, float sizeZ, Object properties) {

        CompoundTag tag = null;

        if (properties instanceof String) {
            tag = BlockStateUtils.toTag((String) properties);
        } else if (properties instanceof CompoundTag) {
            tag = (CompoundTag) properties;
        }
        if (!tag.getBoolean("up")) {
            gl.glTranslatef(0.0f, sizeY * 0.8f - 1.0f, 0.0f);
            Cube.draw(gl, color, thickness, sizeY * 0.8f, thickness);
            gl.glTranslatef(0.0f, 1.0f - sizeY * 0.8f, 0.0f);
        } else {
            Cube.draw(gl, color, thickness, 1.0f, thickness);
        }
        gl.glTranslatef(0.0f, 1.0f - sizeY, 0.0f);
        if (tag.getString("south").equals("low")) {
            gl.glTranslatef(0.0f, sizeY * 0.8f - 1.0f, thickness * 1.5f);
            Cube.draw(gl, color, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 1.0f - sizeY * 0.8f, -thickness * 1.5f);
        } else if (tag.getString("south").equals("tall")) {
            gl.glTranslatef(0.0f, 0.0f, thickness * 1.5f);
            Cube.draw(gl, color, thickness, 1.0f, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 0.0f, -thickness * 1.5f);
        }
        if (tag.getString("east").equals("low")) {
            gl.glTranslatef(thickness * 1.5f, sizeY * 0.8f - 1.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness);
            gl.glTranslatef(-thickness * 1.5f, 1.0f - sizeY * 0.8f, 0.0f);
        } else if (tag.getString("east").equals("tall")) {
            gl.glTranslatef(thickness * 1.5f, 0.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, 1.0f, thickness);
            gl.glTranslatef(-thickness * 1.5f, 0.0f, 0.0f);
        }
        if (tag.getString("north").equals("low")) {
            gl.glTranslatef(0.0f, sizeY * 0.8f - 1.0f, -thickness * 1.5f);
            Cube.draw(gl, color, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 1.0f - sizeY * 0.8f, thickness * 1.5f);
        } else if (tag.getString("north").equals("tall")) {
            gl.glTranslatef(0.0f, 0.0f, -thickness * 1.5f);
            Cube.draw(gl, color, thickness, 1.0f, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 0.0f, thickness * 1.5f);
        }
        if (tag.getString("west").equals("low")) {
            gl.glTranslatef(-thickness * 1.5f, sizeY * 0.8f - 1.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness);
            gl.glTranslatef(thickness * 1.5f, 1.0f - sizeY * 0.8f, 0.0f);
        } else if (tag.getString("east").equals("tall")) {
            gl.glTranslatef(-thickness * 1.5f, 0.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, 1.0f, thickness);
            gl.glTranslatef(thickness * 1.5f, 0.0f, 0.0f);
        }
    }
}
