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

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4bc;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

/**
 * @author eccentric_nz
 */
public class ThinCube {

    public static void draw(GL4bc gl, Color color, float scale, float sizeX, float sizeY, float sizeZ, Object properties, boolean transparent) {

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
            } else if (((String) properties).contains("rotation=")) {
                String rotationToEnd = ((String) properties).substring(((String) properties).indexOf("rotation=") + "rotation".length() + 1);
                int endIndex = rotationToEnd.contains(",") ? rotationToEnd.indexOf(',') : rotationToEnd.indexOf(']');
                int rotationInt = Integer.parseInt(rotationToEnd.substring(0, endIndex));
                yaw = rotationInt * 22.5f;
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
            } else if (((CompoundTag) properties).containsKey("rotation")) {
                int rotationInt = ((CompoundTag) properties).getInt("rotation");
                yaw = rotationInt * 22.5f;
            }
        }
        gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f);

        gl.glTranslatef(0.0f, sizeY - scale, 0.0f);

        Cube.draw(gl, color, scale, sizeX, sizeY, sizeZ, transparent);
    }
}
