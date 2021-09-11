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
public class Rotational {

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
        } else if (tag.containsKey("rotation")) {
            int rotationInt = tag.getInt("rotation");
            yaw = rotationInt * 22.5f;
        }
        gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f);

        gl.glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(gl, color, sizeX, sizeY, sizeZ);
    }
}
