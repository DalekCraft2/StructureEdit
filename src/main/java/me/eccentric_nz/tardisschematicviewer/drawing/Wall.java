package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL2;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

public class Wall {

    public static void draw(GL2 gl, Color color, float scale, float thickness, float sizeX, float sizeY, float sizeZ, Object properties, boolean transparent) {

        sizeX *= scale;
        sizeY *= scale;
        sizeZ *= scale;

        if (properties instanceof String) {
            if (((String) properties).contains("up=false")) {
                gl.glTranslatef(0.0f, sizeY * 0.8f - scale, 0.0f);
                Cube.draw(gl, color, scale, thickness, sizeY * 0.8f, thickness, transparent);
                gl.glTranslatef(0.0f, scale - sizeY * 0.8f, 0.0f);
            } else {
                Cube.draw(gl, color, scale, thickness, scale, thickness, transparent);
            }
            if (((String) properties).contains("south=low")) {
                gl.glTranslatef(0.0f, sizeY * 0.8f - scale, thickness * 1.5f);
                Cube.draw(gl, color, scale, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f, transparent);
                gl.glTranslatef(0.0f, scale - sizeY * 0.8f, -thickness * 1.5f);
            } else if (((String) properties).contains("south=tall")) {
                gl.glTranslatef(0.0f, 0.0f, thickness * 1.5f);
                Cube.draw(gl, color, scale, thickness, scale, (sizeZ - thickness) / 2.0f, transparent);
                gl.glTranslatef(0.0f, 0.0f, -thickness * 1.5f);
            }
            if (((String) properties).contains("east=low")) {
                gl.glTranslatef(thickness * 1.5f, sizeY * 0.8f - scale, 0.0f);
                Cube.draw(gl, color, scale, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness, transparent);
                gl.glTranslatef(-thickness * 1.5f, scale - sizeY * 0.8f, 0.0f);
            } else if (((String) properties).contains("east=tall")) {
                gl.glTranslatef(thickness * 1.5f, 0.0f, 0.0f);
                Cube.draw(gl, color, scale, (sizeX - thickness) / 2.0f, scale, thickness, transparent);
                gl.glTranslatef(-thickness * 1.5f, 0.0f, 0.0f);
            }
            if (((String) properties).contains("north=low")) {
                gl.glTranslatef(0.0f, sizeY * 0.8f - scale, -thickness * 1.5f);
                Cube.draw(gl, color, scale, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f, transparent);
                gl.glTranslatef(0.0f, scale - sizeY * 0.8f, thickness * 1.5f);
            } else if (((String) properties).contains("north=tall")) {
                gl.glTranslatef(0.0f, 0.0f, -thickness * 1.5f);
                Cube.draw(gl, color, scale, thickness, scale, (sizeZ - thickness) / 2.0f, transparent);
                gl.glTranslatef(0.0f, 0.0f, thickness * 1.5f);
            }
            if (((String) properties).contains("west=low")) {
                gl.glTranslatef(-thickness * 1.5f, sizeY * 0.8f - scale, 0.0f);
                Cube.draw(gl, color, scale, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness, transparent);
                gl.glTranslatef(thickness * 1.5f, scale - sizeY * 0.8f, 0.0f);
            } else if (((String) properties).contains("west=tall")) {
                gl.glTranslatef(-thickness * 1.5f, 0.0f, 0.0f);
                Cube.draw(gl, color, scale, (sizeX - thickness) / 2.0f, scale, thickness, transparent);
                gl.glTranslatef(thickness * 1.5f, 0.0f, 0.0f);
            }
        } else if (properties instanceof CompoundTag) {
            if (((CompoundTag) properties).getString("up").equals("false")) {
                gl.glTranslatef(0.0f, sizeY * 0.8f - scale, 0.0f);
                Cube.draw(gl, color, scale, thickness, sizeY * 0.8f, thickness, transparent);
                gl.glTranslatef(0.0f, scale - sizeY * 0.8f, 0.0f);
            } else {
                Cube.draw(gl, color, scale, thickness, scale, thickness, transparent);
            }
            gl.glTranslatef(0.0f, scale - sizeY, 0.0f);
            if (((CompoundTag) properties).getString("south").equals("low")) {
                gl.glTranslatef(0.0f, sizeY * 0.8f - scale, thickness * 1.5f);
                Cube.draw(gl, color, scale, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f, transparent);
                gl.glTranslatef(0.0f, scale - sizeY * 0.8f, -thickness * 1.5f);
            } else if (((CompoundTag) properties).getString("south").equals("tall")) {
                gl.glTranslatef(0.0f, 0.0f, thickness * 1.5f);
                Cube.draw(gl, color, scale, thickness, scale, (sizeZ - thickness) / 2.0f, transparent);
                gl.glTranslatef(0.0f, 0.0f, -thickness * 1.5f);
            }
            if (((CompoundTag) properties).getString("east").equals("low")) {
                gl.glTranslatef(thickness * 1.5f, sizeY * 0.8f - scale, 0.0f);
                Cube.draw(gl, color, scale, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness, transparent);
                gl.glTranslatef(-thickness * 1.5f, scale - sizeY * 0.8f, 0.0f);
            } else if (((CompoundTag) properties).getString("east").equals("tall")) {
                gl.glTranslatef(thickness * 1.5f, 0.0f, 0.0f);
                Cube.draw(gl, color, scale, (sizeX - thickness) / 2.0f, scale, thickness, transparent);
                gl.glTranslatef(-thickness * 1.5f, 0.0f, 0.0f);
            }
            if (((CompoundTag) properties).getString("north").equals("low")) {
                gl.glTranslatef(0.0f, sizeY * 0.8f - scale, -thickness * 1.5f);
                Cube.draw(gl, color, scale, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f, transparent);
                gl.glTranslatef(0.0f, scale - sizeY * 0.8f, thickness * 1.5f);
            } else if (((CompoundTag) properties).getString("north").equals("tall")) {
                gl.glTranslatef(0.0f, 0.0f, -thickness * 1.5f);
                Cube.draw(gl, color, scale, thickness, scale, (sizeZ - thickness) / 2.0f, transparent);
                gl.glTranslatef(0.0f, 0.0f, thickness * 1.5f);
            }
            if (((CompoundTag) properties).getString("west").equals("low")) {
                gl.glTranslatef(-thickness * 1.5f, sizeY * 0.8f - scale, 0.0f);
                Cube.draw(gl, color, scale, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness, transparent);
                gl.glTranslatef(thickness * 1.5f, scale - sizeY * 0.8f, 0.0f);
            } else if (((CompoundTag) properties).getString("east").equals("tall")) {
                gl.glTranslatef(-thickness * 1.5f, 0.0f, 0.0f);
                Cube.draw(gl, color, scale, (sizeX - thickness) / 2.0f, scale, thickness, transparent);
                gl.glTranslatef(thickness * 1.5f, 0.0f, 0.0f);
            }
        }
    }
}