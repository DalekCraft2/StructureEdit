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

import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

import static org.lwjgl.opengl.GL46.glRotatef;
import static org.lwjgl.opengl.GL46.glTranslatef;

/**
 * @author eccentric_nz
 */
public class Rotational {

    public static void draw(Color color, float sizeX, float sizeY, float sizeZ, CompoundTag properties) {

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
        } else if (properties.containsKey("rotation")) {
            int rotationInt = properties.getInt("rotation");
            yaw = rotationInt * 22.5f;
        }
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);

        glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(color, sizeX, sizeY, sizeZ);
    }
}
