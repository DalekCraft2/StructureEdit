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

import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * @author eccentric_nz
 */
public class Fence {

    public static void draw(GL2 gl, Color color, float scale, float thickness, float sizeX, float sizeY, float sizeZ, boolean transparent) {

        sizeX *= thickness;
        sizeZ *= thickness;
        float[] components = color.getComponents(null);

        if (transparent) {
            gl.glLineWidth(scale * 2);
            gl.glBegin(GL_LINES);
        } else {
            gl.glBegin(GL_QUADS);
        }

        // Front Face wide
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, 0.0f, scale);
        gl.glVertex3f(-scale, -scale, sizeZ); // bottom-left of the quad
        gl.glVertex3f(scale, -scale, sizeZ);  // bottom-right of the quad
        gl.glVertex3f(scale, sizeY, sizeZ);   // top-right of the quad
        gl.glVertex3f(-scale, sizeY, sizeZ);  // top-left of the quad

        // Back Face wide
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, 0.0f, -scale);
        gl.glVertex3f(-scale, -scale, -sizeZ);
        gl.glVertex3f(-scale, sizeY, -sizeZ);
        gl.glVertex3f(scale, sizeY, -sizeZ);
        gl.glVertex3f(scale, -scale, -sizeZ);

        // Front Face thin
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, 0.0f, scale);
        gl.glVertex3f(-sizeX, -scale, scale); // bottom-left of the quad
        gl.glVertex3f(sizeX, -scale, scale);  // bottom-right of the quad
        gl.glVertex3f(sizeX, sizeY, scale);   // top-right of the quad
        gl.glVertex3f(-sizeX, sizeY, scale);  // top-left of the quad

        // Back Face thin
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, 0.0f, -scale);
        gl.glVertex3f(-sizeX, -scale, -scale);
        gl.glVertex3f(-sizeX, sizeY, -scale);
        gl.glVertex3f(sizeX, sizeY, -scale);
        gl.glVertex3f(sizeX, -scale, -scale);

        // Top Face LR
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, scale, 0.0f);
        gl.glVertex3f(-scale, sizeY, -sizeZ);
        gl.glVertex3f(-scale, sizeY, sizeZ);
        gl.glVertex3f(scale, sizeY, sizeZ);
        gl.glVertex3f(scale, sizeY, -sizeZ);

        // Top Face FB
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, scale, 0.0f);
        gl.glVertex3f(-sizeX, sizeY, -scale);
        gl.glVertex3f(-sizeX, sizeY, scale);
        gl.glVertex3f(sizeX, sizeY, scale);
        gl.glVertex3f(sizeX, sizeY, -scale);

        // Bottom Face LR
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, -scale, 0.0f);
        gl.glVertex3f(-scale, -scale, -sizeZ);
        gl.glVertex3f(scale, -scale, -sizeZ);
        gl.glVertex3f(scale, -scale, sizeZ);
        gl.glVertex3f(-scale, -scale, sizeZ);

        // Bottom Face FB
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, -scale, 0.0f);
        gl.glVertex3f(-sizeX, -scale, -scale);
        gl.glVertex3f(sizeX, -scale, -scale);
        gl.glVertex3f(sizeX, -scale, scale);
        gl.glVertex3f(-sizeX, -scale, scale);

        // Right face LR
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(scale, 0.0f, 0.0f);
        gl.glVertex3f(scale, -scale, -sizeZ);
        gl.glVertex3f(scale, sizeY, -sizeZ);
        gl.glVertex3f(scale, sizeY, sizeZ);
        gl.glVertex3f(scale, -scale, sizeZ);

        // Right face FB
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(scale, 0.0f, 0.0f);
        gl.glVertex3f(sizeX, -scale, -scale);
        gl.glVertex3f(sizeX, sizeY, -scale);
        gl.glVertex3f(sizeX, sizeY, scale);
        gl.glVertex3f(sizeX, -scale, scale);

        // Left Face LR
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(-scale, 0.0f, 0.0f);
        gl.glVertex3f(-scale, -scale, -sizeZ);
        gl.glVertex3f(-scale, -scale, sizeZ);
        gl.glVertex3f(-scale, sizeY, sizeZ);
        gl.glVertex3f(-scale, sizeY, -sizeZ);

        // Left Face FB
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(-scale, 0.0f, 0.0f);
        gl.glVertex3f(-sizeX, -scale, -scale);
        gl.glVertex3f(-sizeX, -scale, scale);
        gl.glVertex3f(-sizeX, sizeY, scale);
        gl.glVertex3f(-sizeX, sizeY, -scale);

        gl.glEnd();
    }
}
