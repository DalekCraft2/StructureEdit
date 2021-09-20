package me.eccentric_nz.tardisschematicviewer.drawing;

import com.jogamp.opengl.GL4bc;

import static com.jogamp.opengl.GL4bc.GL_LINES;

public class SchematicBorder {

    public static void draw(GL4bc gl, float sizeX, float sizeY, float sizeZ) {
        gl.glTranslatef(-sizeX / 2.0f, -sizeY / 2.0f, -sizeZ / 2.0f);

        gl.glLineWidth(2.0f);
        gl.glBegin(GL_LINES);

        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(sizeX, 0.0f, 0.0f);

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, sizeY, 0.0f);

        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, sizeZ);

        gl.glEnd();

        gl.glTranslatef(sizeX / 2.0f, sizeY / 2.0f, sizeZ / 2.0f);
    }
}
