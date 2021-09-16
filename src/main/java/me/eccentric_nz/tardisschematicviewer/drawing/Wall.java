package me.eccentric_nz.tardisschematicviewer.drawing;

import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

import static org.lwjgl.opengl.GL46.glTranslatef;


public class Wall {

    public static void draw(Color color, float thickness, float sizeX, float sizeY, float sizeZ, CompoundTag properties) {

        if (properties.containsKey("up") && properties.getString("up").equals("false")) {
            glTranslatef(0.0f, sizeY * 0.8f - 1.0f, 0.0f);
            Cube.draw(color, thickness, sizeY * 0.8f, thickness);
            glTranslatef(0.0f, 1.0f - sizeY * 0.8f, 0.0f);
        } else {
            Cube.draw(color, thickness, 1.0f, thickness);
        }
        glTranslatef(0.0f, 1.0f - sizeY, 0.0f);
        if (properties.containsKey("south")) {
            switch (properties.getString("south")) {
                case "low" -> {
                    glTranslatef(0.0f, sizeY * 0.8f - 1.0f, thickness * 1.5f);
                    Cube.draw(color, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f);
                    glTranslatef(0.0f, 1.0f - sizeY * 0.8f, -thickness * 1.5f);
                }
                case "tall" -> {
                    glTranslatef(0.0f, 0.0f, thickness * 1.5f);
                    Cube.draw(color, thickness, 1.0f, (sizeZ - thickness) / 2.0f);
                    glTranslatef(0.0f, 0.0f, -thickness * 1.5f);
                }
            }
        }
        if (properties.containsKey("east")) {
            switch (properties.getString("east")) {
                case "low" -> {
                    glTranslatef(thickness * 1.5f, sizeY * 0.8f - 1.0f, 0.0f);
                    Cube.draw(color, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness);
                    glTranslatef(-thickness * 1.5f, 1.0f - sizeY * 0.8f, 0.0f);
                }
                case "tall" -> {
                    glTranslatef(thickness * 1.5f, 0.0f, 0.0f);
                    Cube.draw(color, (sizeX - thickness) / 2.0f, 1.0f, thickness);
                    glTranslatef(-thickness * 1.5f, 0.0f, 0.0f);
                }
            }
        }
        if (properties.containsKey("north")) {
            switch (properties.getString("north")) {
                case "low" -> {
                    glTranslatef(0.0f, sizeY * 0.8f - 1.0f, -thickness * 1.5f);
                    Cube.draw(color, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f);
                    glTranslatef(0.0f, 1.0f - sizeY * 0.8f, thickness * 1.5f);
                }
                case "tall" -> {
                    glTranslatef(0.0f, 0.0f, -thickness * 1.5f);
                    Cube.draw(color, thickness, 1.0f, (sizeZ - thickness) / 2.0f);
                    glTranslatef(0.0f, 0.0f, thickness * 1.5f);
                }
            }
        }
        if (properties.containsKey("west")) {
            switch (properties.getString("west")) {
                case "low" -> {
                    glTranslatef(-thickness * 1.5f, sizeY * 0.8f - 1.0f, 0.0f);
                    Cube.draw(color, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness);
                    glTranslatef(thickness * 1.5f, 1.0f - sizeY * 0.8f, 0.0f);
                }
                case "tall" -> {
                    glTranslatef(-thickness * 1.5f, 0.0f, 0.0f);
                    Cube.draw(color, (sizeX - thickness) / 2.0f, 1.0f, thickness);
                    glTranslatef(thickness * 1.5f, 0.0f, 0.0f);
                }
            }
        }
    }
}
