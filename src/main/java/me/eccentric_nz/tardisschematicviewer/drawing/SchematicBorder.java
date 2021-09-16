package me.eccentric_nz.tardisschematicviewer.drawing;

import static org.lwjgl.opengl.GL46.*;

public class SchematicBorder {

    public static void draw(float sizeX, float sizeY, float sizeZ) {
        glBegin(GL_LINES);

        // TODO Make this fullbright, regardless of camera angle.

        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(-sizeX, -sizeY, -sizeZ);
        glVertex3f(sizeX, -sizeY, -sizeZ);

        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(-sizeX, -sizeY, -sizeZ);
        glVertex3f(-sizeX, sizeY, -sizeZ);

        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(-sizeX, -sizeY, -sizeZ);
        glVertex3f(-sizeX, -sizeY, sizeZ);

        glEnd();
    }
}
