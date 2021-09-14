/*
 * Copyright (C) 2021 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;
import me.eccentric_nz.tardisschematicviewer.util.BlockStateUtils;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

/**
 * @author eccentric_nz
 */
public class Stair {

    public static void draw(GL4bc gl, Color color, float sizeX, float sizeY, float sizeZ, CompoundTag properties) {

        float roll = 0.0f;
        float yaw = 180.0f;

        if (properties.containsKey("facing")) {
            switch (properties.getString("facing")) {
                case "south" -> yaw = 0.0f;
                case "east" -> yaw = 90.0f;
                default -> {
                } // north
                case "west" -> yaw = -90.0f;
            }
            ;
        }
        if (properties.containsKey("half") && properties.getString("half").equals("top")) {
            roll = 180.0f;
        }
        gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(roll, 0.0f, 0.0f, 1.0f);

        gl.glTranslatef(0.0f, -sizeY / 2.0f, 0.0f);

        Cube.draw(gl, color, sizeX, sizeY / 2.0f, sizeZ);

        if (properties.containsKey("shape")) {
            switch (properties.getString("shape")) {
                case "inner_left" -> {
                    if (roll == 180.0f) {
                        gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                    }
                    gl.glTranslatef(0.0f, sizeY, sizeZ / 2.0f);
                    Cube.draw(gl, color, sizeX, sizeY / 2.0f, sizeZ / 2.0f);
                    gl.glTranslatef(sizeX / 2.0f, 0.0f, -sizeZ);
                    Cube.draw(gl, color, sizeX / 2.0f, sizeY / 2.0f, sizeZ / 2.0f);
                }
                case "inner_right" -> {
                    if (roll == 180.0f) {
                        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    }
                    gl.glTranslatef(0.0f, sizeY, sizeZ / 2.0f);
                    Cube.draw(gl, color, sizeX, sizeY / 2.0f, sizeZ / 2.0f);
                    gl.glTranslatef(-sizeX / 2.0f, 0.0f, -sizeZ);
                    Cube.draw(gl, color, sizeX / 2.0f, sizeY / 2.0f, sizeZ / 2.0f);
                }
                case "outer_left" -> {
                    if (roll == 180.0f) {
                        gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                    }
                    gl.glTranslatef(sizeX / 2.0f, sizeY, sizeZ / 2.0f);
                    Cube.draw(gl, color, sizeX / 2.0f, sizeY / 2.0f, sizeZ / 2.0f);
                }
                case "outer_right" -> {
                    if (roll == 180.0f) {
                        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    }
                    gl.glTranslatef(-sizeX / 2.0f, sizeY, sizeZ / 2.0f);
                    Cube.draw(gl, color, sizeX / 2.0f, sizeY / 2.0f, sizeZ / 2.0f);
                }
                default -> { // straight
                    gl.glTranslatef(0.0f, sizeY, sizeZ / 2.0f);
                    Cube.draw(gl, color, sizeX, sizeY / 2.0f, sizeZ / 2.0f);
                }
            }
        } else {
            gl.glTranslatef(0.0f, sizeY, sizeZ / 2.0f);
            Cube.draw(gl, color, sizeX, sizeY / 2.0f, sizeZ / 2.0f);
        }
    }
}
