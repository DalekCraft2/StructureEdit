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
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * @author eccentric_nz
 */
public class Slab {

    public static void draw(GL2 gl, Color color, float size, float depth, Object properties) {

        float[] components = color.getColorComponents(null);
        if (properties instanceof String) {
            if (((String) properties).contains("type=top") || ((String) properties).contains("half=top")) {
                gl.glRotatef(180.0f, 0.0f, 0.0f, 0.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
        } else if (properties instanceof CompoundTag) {
            if (((CompoundTag) properties).getString("type").equals("top") || ((CompoundTag) properties).getString("half").equals("top")) {
                gl.glRotatef(180.0f, 0.0f, 0.0f, 0.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
        }

        gl.glBegin(GL_QUADS);

        // Front Face
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, 0.0f, size);
        gl.glVertex3f(-size, -size, size); // bottom-left of the quad
        gl.glVertex3f(size, -size, size);  // bottom-right of the quad
        gl.glVertex3f(size, -depth, size);   // top-right of the quad
        gl.glVertex3f(-size, -depth, size);  // top-left of the quad

        // Back Face
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, 0.0f, -size);
        gl.glVertex3f(-size, -size, -size);
        gl.glVertex3f(-size, -depth, -size);
        gl.glVertex3f(size, -depth, -size);
        gl.glVertex3f(size, -size, -size);

        // Top Face
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, size, 0.0f);
        gl.glVertex3f(-size, -depth, -size);
        gl.glVertex3f(-size, -depth, size);
        gl.glVertex3f(size, -depth, size);
        gl.glVertex3f(size, -depth, -size);

        // Bottom Face
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(0.0f, -size, 0.0f);
        gl.glVertex3f(-size, -size, -size);
        gl.glVertex3f(size, -size, -size);
        gl.glVertex3f(size, -size, size);
        gl.glVertex3f(-size, -size, size);

        // Right face
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(size, 0.0f, 0.0f);
        gl.glVertex3f(size, -size, -size);
        gl.glVertex3f(size, -depth, -size);
        gl.glVertex3f(size, -depth, size);
        gl.glVertex3f(size, -size, size);

        // Left Face
        gl.glColor3f(components[0], components[1], components[2]);
        gl.glNormal3f(-size, 0.0f, 0.0f);
        gl.glVertex3f(-size, -size, -size);
        gl.glVertex3f(-size, -size, size);
        gl.glVertex3f(-size, -depth, size);
        gl.glVertex3f(-size, -depth, -size);

        gl.glEnd();
    }
}
