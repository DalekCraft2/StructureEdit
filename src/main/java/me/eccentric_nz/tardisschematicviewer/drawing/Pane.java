package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

public class Pane {

    public static void draw(GL4bc gl, Color color, float thickness, float sizeX, float sizeY, float sizeZ, Object properties) {

        gl.glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(gl, color, thickness, sizeY, thickness);

        if (properties instanceof String) {
            if (((String) properties).contains("south=true")) {
                gl.glTranslatef(0.0f, 0.0f, thickness * 4.5f);
                Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
                gl.glTranslatef(0.0f, 0.0f, -thickness * 4.5f);
            }
            if (((String) properties).contains("east=true")) {
                gl.glTranslatef(thickness * 4.5f, 0.0f, 0.0f);
                Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
                gl.glTranslatef(-thickness * 4.5f, 0.0f, 0.0f);
            }
            if (((String) properties).contains("north=true")) {
                gl.glTranslatef(0.0f, 0.0f, -thickness * 4.5f);
                Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
                gl.glTranslatef(0.0f, 0.0f, thickness * 4.5f);
            }
            if (((String) properties).contains("west=true")) {
                gl.glTranslatef(-thickness * 4.5f, 0.0f, 0.0f);
                Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
                gl.glTranslatef(thickness * 4.5f, 0.0f, 0.0f);
            }
        } else if (properties instanceof CompoundTag) {
            if (((CompoundTag) properties).getString("south").equals("true")) {
                gl.glTranslatef(0.0f, 0.0f, thickness * 4.5f);
                Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
                gl.glTranslatef(0.0f, 0.0f, -thickness * 4.5f);
            }
            if (((CompoundTag) properties).getString("east").equals("true")) {
                gl.glTranslatef(thickness * 4.5f, 0.0f, 0.0f);
                Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
                gl.glTranslatef(-thickness * 4.5f, 0.0f, 0.0f);
            }
            if (((CompoundTag) properties).getString("north").equals("true")) {
                gl.glTranslatef(0.0f, 0.0f, -thickness * 4.5f);
                Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
                gl.glTranslatef(0.0f, 0.0f, thickness * 4.5f);
            }
            if (((CompoundTag) properties).getString("west").equals("true")) {
                gl.glTranslatef(-thickness * 4.5f, 0.0f, 0.0f);
                Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
                gl.glTranslatef(thickness * 4.5f, 0.0f, 0.0f);
            }
        }
    }
}
