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

import java.awt.*;

import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * @author eccentric_nz
 */
public class Cube {

    // TODO Find a way to cull covered faces.
    public static void draw(GL4bc gl, Color color, float scale, float sizeX, float sizeY, float sizeZ, boolean transparent) {

        float[] components = color.getComponents(null);

        if (transparent) {
            gl.glLineWidth(scale * 2);
            gl.glBegin(GL_LINES);
        } else {
            gl.glBegin(GL_QUADS);
        }

        // Set color
        gl.glColor4f(components[0], components[1], components[2], components[3]);

        // Front Face
        gl.glNormal3f(0.0f, 0.0f, scale);
        gl.glVertex3f(-sizeX, -sizeY, sizeZ); // bottom-left of the quad
        gl.glVertex3f(sizeX, -sizeY, sizeZ);  // bottom-right of the quad
        gl.glVertex3f(sizeX, sizeY, sizeZ);   // top-right of the quad
        gl.glVertex3f(-sizeX, sizeY, sizeZ);  // top-left of the quad

        // Back Face
        gl.glNormal3f(0.0f, 0.0f, -scale);
        gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
        gl.glVertex3f(-sizeX, sizeY, -sizeZ);
        gl.glVertex3f(sizeX, sizeY, -sizeZ);
        gl.glVertex3f(sizeX, -sizeY, -sizeZ);

        // Top Face
        gl.glNormal3f(0.0f, scale, 0.0f);
        gl.glVertex3f(-sizeX, sizeY, -sizeZ);
        gl.glVertex3f(-sizeX, sizeY, sizeZ);
        gl.glVertex3f(sizeX, sizeY, sizeZ);
        gl.glVertex3f(sizeX, sizeY, -sizeZ);

        // Bottom Face
        gl.glNormal3f(0.0f, -scale, 0.0f);
        gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
        gl.glVertex3f(sizeX, -sizeY, -sizeZ);
        gl.glVertex3f(sizeX, -sizeY, sizeZ);
        gl.glVertex3f(-sizeX, -sizeY, sizeZ);

        // Right face
        gl.glNormal3f(scale, 0.0f, 0.0f);
        gl.glVertex3f(sizeX, -sizeY, -sizeZ);
        gl.glVertex3f(sizeX, sizeY, -sizeZ);
        gl.glVertex3f(sizeX, sizeY, sizeZ);
        gl.glVertex3f(sizeX, -sizeY, sizeZ);

        // Left Face
        gl.glNormal3f(-scale, 0.0f, 0.0f);
        gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
        gl.glVertex3f(-sizeX, -sizeY, sizeZ);
        gl.glVertex3f(-sizeX, sizeY, sizeZ);
        gl.glVertex3f(-sizeX, sizeY, -sizeZ);

        gl.glEnd();
    }
}
