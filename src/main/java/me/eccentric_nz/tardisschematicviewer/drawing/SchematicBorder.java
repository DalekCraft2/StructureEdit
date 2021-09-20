package me.eccentric_nz.tardisschematicviewer.drawing;

import static org.lwjgl.opengl.GL46.*;

public class SchematicBorder {

    public static void draw(float sizeX, float sizeY, float sizeZ) {
        glTranslatef(-sizeX / 2.0f, -sizeY / 2.0f, -sizeZ / 2.0f);

        glLineWidth(2.0f);
        glBegin(GL_LINES);

        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(sizeX, 0.0f, 0.0f);

        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, sizeY, 0.0f);

        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, sizeZ);

        glEnd();

        glTranslatef(sizeX / 2.0f, sizeY / 2.0f, sizeZ / 2.0f);
    }
}
