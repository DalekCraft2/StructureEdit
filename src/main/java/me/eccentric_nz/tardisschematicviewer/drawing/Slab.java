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
public class Slab {

    public static void draw(GL4bc gl, Color color, float sizeX, float sizeY, float sizeZ, CompoundTag properties) {

        if (properties.containsKey("type") && properties.getString("type").equals("double")) {
            Cube.draw(gl, color, sizeX, sizeY * 2.0f, sizeZ);
            return;
        }
        if ((properties.containsKey("type") && properties.getString("type").equals("top")) || (properties.containsKey("half") && properties.getString("half").equals("top"))) {
            gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        }

        gl.glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(gl, color, sizeX, sizeY, sizeZ);
    }
}
