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

import java.awt.*;

/**
 * @author eccentric_nz
 */
public class Plant {

    public static void draw(GL2 gl, Color color, float scale, float thickness, float sizeX, float sizeY, float sizeZ, boolean transparent) {

        sizeX *= scale;
        sizeY *= scale;
        sizeZ *= scale;

        // rotate 45 degrees
        gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);

        gl.glTranslatef(0.0f, sizeY - scale, 0.0f);

        Cube.draw(gl, color, scale, thickness, sizeY, sizeZ, transparent);
        Cube.draw(gl, color, scale, sizeX, sizeY, thickness, transparent);
    }
}
