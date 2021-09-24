package me.dalekcraft.structureedit.drawing;

import com.jogamp.opengl.GL4bc;

import static com.jogamp.opengl.GL4bc.GL_CURRENT_BIT;
import static com.jogamp.opengl.GL4bc.GL_LINES;

public class SchematicBorder {

    public static void draw(GL4bc gl, float sizeX, float sizeY, float sizeZ) {
        // save the current color
        gl.glPushAttrib(GL_CURRENT_BIT);

        gl.glLineWidth(2.0f);
        gl.glBegin(GL_LINES);

        // X-axis (red)
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(sizeX, 0.0f, 0.0f);

        // Y-axis (green)
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, sizeY, 0.0f);

        // Z-axis (blue)
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, sizeZ);

        gl.glEnd();

        // reset the color
        gl.glPopAttrib();
    }
}
