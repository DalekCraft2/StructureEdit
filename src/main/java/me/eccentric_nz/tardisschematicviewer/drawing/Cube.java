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

import static org.lwjgl.opengl.GL46.*;

/**
 * @author eccentric_nz
 */
public class Cube {

    // TODO Find a way to cull covered faces.
    public static void draw(Color color, float sizeX, float sizeY, float sizeZ) {

        float[] components = color.getComponents(null);

        if (components[3] == 0) {
            components[3] = 255;
            glLineWidth(2.0f);
            glBegin(GL_LINES);
        } else {
            glBegin(GL_QUADS);
        }

        // Set color
        glColor4f(components[0], components[1], components[2], components[3]);

        // Front Face
        glNormal3f(0.0f, 0.0f, 1.0f);
        glVertex3f(-sizeX, -sizeY, sizeZ); // bottom-left of the quad
        glVertex3f(sizeX, -sizeY, sizeZ);  // bottom-right of the quad
        glVertex3f(sizeX, sizeY, sizeZ);   // top-right of the quad
        glVertex3f(-sizeX, sizeY, sizeZ);  // top-left of the quad

        // Back Face
        glNormal3f(0.0f, 0.0f, -1.0f);
        glVertex3f(-sizeX, -sizeY, -sizeZ);
        glVertex3f(-sizeX, sizeY, -sizeZ);
        glVertex3f(sizeX, sizeY, -sizeZ);
        glVertex3f(sizeX, -sizeY, -sizeZ);

        // Top Face
        glNormal3f(0.0f, 1.0f, 0.0f);
        glVertex3f(-sizeX, sizeY, -sizeZ);
        glVertex3f(-sizeX, sizeY, sizeZ);
        glVertex3f(sizeX, sizeY, sizeZ);
        glVertex3f(sizeX, sizeY, -sizeZ);

        // Bottom Face
        glNormal3f(0.0f, -1.0f, 0.0f);
        glVertex3f(-sizeX, -sizeY, -sizeZ);
        glVertex3f(sizeX, -sizeY, -sizeZ);
        glVertex3f(sizeX, -sizeY, sizeZ);
        glVertex3f(-sizeX, -sizeY, sizeZ);

        // Right face
        glNormal3f(1.0f, 0.0f, 0.0f);
        glVertex3f(sizeX, -sizeY, -sizeZ);
        glVertex3f(sizeX, sizeY, -sizeZ);
        glVertex3f(sizeX, sizeY, sizeZ);
        glVertex3f(sizeX, -sizeY, sizeZ);

        // Left Face
        glNormal3f(-1.0f, 0.0f, 0.0f);
        glVertex3f(-sizeX, -sizeY, -sizeZ);
        glVertex3f(-sizeX, -sizeY, sizeZ);
        glVertex3f(-sizeX, sizeY, sizeZ);
        glVertex3f(-sizeX, sizeY, -sizeZ);

        glEnd();
    }
}
