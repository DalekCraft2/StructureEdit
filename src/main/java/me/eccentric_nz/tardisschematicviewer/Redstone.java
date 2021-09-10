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
import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * @author eccentric_nz
 */
public class Redstone {

    private static final List<Float[]> translations;

    static {
        translations = new ArrayList<>();
        translations.add(new Float[]{0.0f, 0.0f, 0.0f});
        translations.add(new Float[]{0.0f, 0.0f, -0.75f});
        translations.add(new Float[]{0.75f, 0.0f, 0.75f});
        translations.add(new Float[]{-0.75f, 0.0f, 0.75f});
        translations.add(new Float[]{-0.75f, 0.0f, -0.75f});
    }

    public static void draw(GL2 gl, Color color, float size) {

        float quarter = size / 4;
        float height = -size + quarter / 2;
        float[] components = color.getColorComponents(null);

        gl.glPushMatrix();

        for (Float[] translation : translations) {

            gl.glTranslatef(translation[0], translation[1], translation[2]);

            gl.glBegin(GL_QUADS);

            // Front Face
            gl.glColor3f(components[0], components[1], components[2]);
            gl.glNormal3f(0.0f, 0.0f, size);
            gl.glVertex3f(-quarter, -size, quarter); // bottom-left of the quad
            gl.glVertex3f(quarter, -size, quarter);  // bottom-right of the quad
            gl.glVertex3f(quarter, height, quarter);   // top-right of the quad
            gl.glVertex3f(-quarter, height, quarter);  // top-left of the quad

            // Back Face
            gl.glColor3f(components[0], components[1], components[2]);
            gl.glNormal3f(0.0f, 0.0f, -size);
            gl.glVertex3f(-quarter, -size, -quarter);
            gl.glVertex3f(-quarter, height, -quarter);
            gl.glVertex3f(quarter, height, -quarter);
            gl.glVertex3f(quarter, -size, -quarter);

            // Top Face
            gl.glColor3f(components[0], components[1], components[2]);
            gl.glNormal3f(0.0f, size, 0.0f);
            gl.glVertex3f(-quarter, height, -quarter);
            gl.glVertex3f(-quarter, height, quarter);
            gl.glVertex3f(quarter, height, quarter);
            gl.glVertex3f(quarter, height, -quarter);

            // Bottom Face
            gl.glColor3f(components[0], components[1], components[2]);
            gl.glNormal3f(0.0f, -size, 0.0f);
            gl.glVertex3f(-quarter, -size, -quarter);
            gl.glVertex3f(quarter, -size, -quarter);
            gl.glVertex3f(quarter, -size, quarter);
            gl.glVertex3f(-quarter, -size, quarter);

            // Right face
            gl.glColor3f(components[0], components[1], components[2]);
            gl.glNormal3f(size, 0.0f, 0.0f);
            gl.glVertex3f(quarter, -size, -quarter);
            gl.glVertex3f(quarter, height, -quarter);
            gl.glVertex3f(quarter, height, quarter);
            gl.glVertex3f(quarter, -size, quarter);

            // Left Face
            gl.glColor3f(components[0], components[1], components[2]);
            gl.glNormal3f(-size, 0.0f, 0.0f);
            gl.glVertex3f(-quarter, -size, -quarter);
            gl.glVertex3f(-quarter, -size, quarter);
            gl.glVertex3f(-quarter, height, quarter);
            gl.glVertex3f(-quarter, height, -quarter);

            gl.glEnd();
        }
        gl.glPopMatrix();
    }
}
