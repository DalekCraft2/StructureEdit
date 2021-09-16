package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4bc;

import static com.jogamp.opengl.GL2ES1.GL_LIGHT_MODEL_AMBIENT;

public class SchematicBorder {

    public static void draw(GL4bc gl, float sizeX, float sizeY, float sizeZ) {
        gl.glBegin(GL.GL_LINES);

        // TODO Make this fullbright, regardless of camera angle.

        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
        gl.glVertex3f(sizeX, -sizeY, -sizeZ);

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
        gl.glVertex3f(-sizeX, sizeY, -sizeZ);

        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(-sizeX, -sizeY, -sizeZ);
        gl.glVertex3f(-sizeX, -sizeY, sizeZ);

        gl.glEnd();
    }
}
