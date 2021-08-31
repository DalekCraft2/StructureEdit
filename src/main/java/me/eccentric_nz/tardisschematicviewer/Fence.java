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
package me.eccentric_nz.tardisschematicviewer;

import com.jogamp.opengl.GL2;

import java.awt.*;

import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * @author eccentric_nz
 */
public class Fence {

    public static void draw(GL2 gl, Color color, float size, float thickness, float height, boolean glass) {

        float height1 = -size + height;
        float[] components = color.getColorComponents(null);

        if (glass) {
            gl.glLineWidth(size * 2);
            gl.glBegin(GL_LINES);
        } else {
            gl.glBegin(GL_QUADS);
        }

        // Front Face wide
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, 0.0f, size);
        gl.glVertex3f(-size, -size, thickness); // bottom-left of the quad
        gl.glVertex3f(size, -size, thickness);  // bottom-right of the quad
        gl.glVertex3f(size, height1, thickness);   // top-right of the quad
        gl.glVertex3f(-size, height1, thickness);  // top-left of the quad

        // Back Face wide
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, 0.0f, -size);
        gl.glVertex3f(-size, -size, -thickness);
        gl.glVertex3f(-size, height1, -thickness);
        gl.glVertex3f(size, height1, -thickness);
        gl.glVertex3f(size, -size, -thickness);

        // Front Face thin
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, 0.0f, size);
        gl.glVertex3f(-thickness, -size, size); // bottom-left of the quad
        gl.glVertex3f(thickness, -size, size);  // bottom-right of the quad
        gl.glVertex3f(thickness, height1, size);   // top-right of the quad
        gl.glVertex3f(-thickness, height1, size);  // top-left of the quad

        // Back Face thin
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, 0.0f, -size);
        gl.glVertex3f(-thickness, -size, -size);
        gl.glVertex3f(-thickness, height1, -size);
        gl.glVertex3f(thickness, height1, -size);
        gl.glVertex3f(thickness, -size, -size);

        // Top Face LR
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, size, 0.0f);
        gl.glVertex3f(-size, height1, -thickness);
        gl.glVertex3f(-size, height1, thickness);
        gl.glVertex3f(size, height1, thickness);
        gl.glVertex3f(size, height1, -thickness);

        // Top Face FB
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, size, 0.0f);
        gl.glVertex3f(-thickness, height1, -size);
        gl.glVertex3f(-thickness, height1, size);
        gl.glVertex3f(thickness, height1, size);
        gl.glVertex3f(thickness, height1, -size);

        // Bottom Face LR
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, -size, 0.0f);
        gl.glVertex3f(-size, -size, -thickness);
        gl.glVertex3f(size, -size, -thickness);
        gl.glVertex3f(size, -size, thickness);
        gl.glVertex3f(-size, -size, thickness);

        // Bottom Face FB
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, -size, 0.0f);
        gl.glVertex3f(-thickness, -size, -size);
        gl.glVertex3f(thickness, -size, -size);
        gl.glVertex3f(thickness, -size, size);
        gl.glVertex3f(-thickness, -size, size);

        // Right face LR
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(size, 0.0f, 0.0f);
        gl.glVertex3f(size, -size, -thickness);
        gl.glVertex3f(size, height1, -thickness);
        gl.glVertex3f(size, height1, thickness);
        gl.glVertex3f(size, -size, thickness);

        // Right face FB
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(size, 0.0f, 0.0f);
        gl.glVertex3f(thickness, -size, -size);
        gl.glVertex3f(thickness, height1, -size);
        gl.glVertex3f(thickness, height1, size);
        gl.glVertex3f(thickness, -size, size);

        // Left Face LR
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(-size, 0.0f, 0.0f);
        gl.glVertex3f(-size, -size, -thickness);
        gl.glVertex3f(-size, -size, thickness);
        gl.glVertex3f(-size, height1, thickness);
        gl.glVertex3f(-size, height1, -thickness);

        // Left Face FB
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(-size, 0.0f, 0.0f);
        gl.glVertex3f(-thickness, -size, -size);
        gl.glVertex3f(-thickness, -size, size);
        gl.glVertex3f(-thickness, height1, size);
        gl.glVertex3f(-thickness, height1, -size);

        gl.glEnd();
    }
}
