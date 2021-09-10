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
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * @author eccentric_nz
 */
public class Stair {

    public static void draw(GL2 gl, Color color, float scale, float sizeX, float sizeY, float sizeZ, Object properties, boolean transparent) {

        float[] components = color.getComponents(null);

        if (properties instanceof String) {
            if (((String) properties).contains("facing=south")) {
                gl.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
            } else if (((String) properties).contains("facing=east")) {
                gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            } else if (((String) properties).contains("facing=north")) {
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            } else if (((String) properties).contains("facing=west")) {
                gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            }
            if (((String) properties).contains("half=top")) {
                gl.glRotatef(180.0f, 0.0f, 0.0f, 0.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
        } else if (properties instanceof CompoundTag) {
            if (((CompoundTag) properties).getString("facing").equals("south")) {
                gl.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
            } else if (((CompoundTag) properties).getString("facing").equals("east")) {
                gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            } else if (((CompoundTag) properties).getString("facing").equals("north")) {
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            } else if (((CompoundTag) properties).getString("facing").equals("west")) {
                gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            }
            if (((CompoundTag) properties).getString("half").equals("top")) {
                gl.glRotatef(180.0f, 0.0f, 0.0f, 0.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
        }

        if (transparent) {
            gl.glLineWidth(scale * 2);
            gl.glBegin(GL_LINES);
        } else {
            gl.glBegin(GL_QUADS);
        }

        // Front Face same as cube
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, 0.0f, scale);
        gl.glVertex3f(-scale, -scale, scale); // bottom-left of the quad
        gl.glVertex3f(scale, -scale, scale);  // bottom-right of the quad
        gl.glVertex3f(scale, scale, scale);   // top-right of the quad
        gl.glVertex3f(-scale, scale, scale);  // top-left of the quad

        // Back Face top
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, 0.0f, -scale);
        gl.glVertex3f(-scale, scale, 0.0f); // bottom-left of the quad
        gl.glVertex3f(scale, scale, 0.0f);  // bottom-right of the quad
        gl.glVertex3f(scale, 0.0f, 0.0f);   // top-right of the quad
        gl.glVertex3f(-scale, 0.0f, 0.0f);  // top-left of the quad

        // Back Face same as slab
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, 0.0f, -scale);
        gl.glVertex3f(-scale, -scale, -scale);
        gl.glVertex3f(-scale, 0.0f, -scale);
        gl.glVertex3f(scale, 0.0f, -scale);
        gl.glVertex3f(scale, -scale, -scale);

        // Top Face back
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, scale, 0.0f);
        gl.glVertex3f(-scale, scale, 0.0f);
        gl.glVertex3f(-scale, scale, scale);
        gl.glVertex3f(scale, scale, scale);
        gl.glVertex3f(scale, scale, 0.0f);

        // Top Face front
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, scale, 0.0f);
        gl.glVertex3f(-scale, 0.0f, 0.0f);
        gl.glVertex3f(-scale, 0.0f, -scale);
        gl.glVertex3f(scale, 0.0f, -scale);
        gl.glVertex3f(scale, 0.0f, 0.0f);

        // Bottom Face same as cube
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(0.0f, -scale, 0.0f);
        gl.glVertex3f(-scale, -scale, -scale);
        gl.glVertex3f(scale, -scale, -scale);
        gl.glVertex3f(scale, -scale, scale);
        gl.glVertex3f(-scale, -scale, scale);

        // Right face front
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(scale, 0.0f, 0.0f);
        gl.glVertex3f(scale, -scale, -scale);
        gl.glVertex3f(scale, 0.0f, -scale);
        gl.glVertex3f(scale, 0.0f, 0.0f);
        gl.glVertex3f(scale, -scale, 0.0f);

        // Right face back
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(scale, 0.0f, 0.0f);
        gl.glVertex3f(scale, -scale, 0.0f);
        gl.glVertex3f(scale, scale, 0.0f);
        gl.glVertex3f(scale, scale, scale);
        gl.glVertex3f(scale, -scale, scale);

        // Left Face front
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(-scale, 0.0f, 0.0f);
        gl.glVertex3f(-scale, -scale, -scale);
        gl.glVertex3f(-scale, 0.0f, -scale);
        gl.glVertex3f(-scale, 0.0f, 0.0f);
        gl.glVertex3f(-scale, -scale, 0.0f);

        // Left Face back
        gl.glColor4f(components[0], components[1], components[2], components[3]);
        gl.glNormal3f(-scale, 0.0f, 0.0f);
        gl.glVertex3f(-scale, -scale, 0.0f);
        gl.glVertex3f(-scale, scale, 0.0f);
        gl.glVertex3f(-scale, scale, scale);
        gl.glVertex3f(-scale, -scale, scale);

        gl.glEnd();
    }
}
