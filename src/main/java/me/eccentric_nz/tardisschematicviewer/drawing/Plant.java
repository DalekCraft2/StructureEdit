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

import java.awt.*;

import static org.lwjgl.opengl.GL46.glRotatef;
import static org.lwjgl.opengl.GL46.glTranslatef;

/**
 * @author eccentric_nz
 */
public class Plant {

    public static void draw(Color color, float thickness, float sizeX, float sizeY, float sizeZ) {

        // rotate 45 degrees
        glRotatef(45.0f, 0.0f, 1.0f, 0.0f);

        glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(color, thickness, sizeY, sizeZ);
        Cube.draw(color, sizeX, sizeY, thickness);
    }
}
