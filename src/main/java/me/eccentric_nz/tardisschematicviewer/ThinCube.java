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
public class ThinCube {

    public static void draw(GL2 gl, Color color, float size, float thickness, float height, String data, boolean glass) {

        float height1 = -size + height;
        float[] components = color.getColorComponents(null);
        // rotate if necessary
        float angle = 0.0f;
        if (data.contains("facing=")) {
            if (data.contains("facing=north")) {
                angle = 0.0f;
            } else if (data.contains("facing=east")) {
                angle = 90.0f;
            } else if (data.contains("facing=south")) {
                angle = 180.0f;
            } else if (data.contains("facing=west")) {
                angle = -90.0f;
            }
        } else if (data.contains("rotation=")) {
            // TODO Sign rotation.
        }
        gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);

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
        gl.glVertex3f(size, -size, thickness); // bottom-right of the quad
        gl.glVertex3f(size, height1, thickness); // top-right of the quad
        gl.glVertex3f(-size, height1, thickness); // top-left of the quad

        // Back Face wide
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, 0.0f, -size);
        gl.glVertex3f(-size, -size, -thickness);
        gl.glVertex3f(-size, height1, -thickness);
        gl.glVertex3f(size, height1, -thickness);
        gl.glVertex3f(size, -size, -thickness);

        // Top Face LR
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, size, 0.0f);
        gl.glVertex3f(-size, height1, -thickness);
        gl.glVertex3f(-size, height1, thickness);
        gl.glVertex3f(size, height1, thickness);
        gl.glVertex3f(size, height1, -thickness);

        // Bottom Face LR
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, -size, 0.0f);
        gl.glVertex3f(-size, -size, -thickness);
        gl.glVertex3f(size, -size, -thickness);
        gl.glVertex3f(size, -size, thickness);
        gl.glVertex3f(-size, -size, thickness);

        // Right Face LR
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(size, 0.0f, 0.0f);
        gl.glVertex3f(size, -size, -thickness);
        gl.glVertex3f(size, height1, -thickness);
        gl.glVertex3f(size, height1, thickness);
        gl.glVertex3f(size, -size, thickness);

        // Left Face LR
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(-size, 0.0f, 0.0f);
        gl.glVertex3f(-size, -size, -thickness);
        gl.glVertex3f(-size, -size, thickness);
        gl.glVertex3f(-size, height1, thickness);
        gl.glVertex3f(-size, height1, -thickness);

        gl.glEnd();
    }
}
