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

import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import me.eccentric_nz.tardisschematicviewer.Main;
import me.eccentric_nz.tardisschematicviewer.schematic.NbtSchematic;
import me.eccentric_nz.tardisschematicviewer.schematic.Schematic;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.json.JSONException;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static com.jogamp.opengl.GL4bc.*;

/**
 * @author eccentric_nz
 */
public class SchematicRenderer extends GLJPanel {

    public static final float SCALE = 1.0f;
    /**
     * Rotational angle for x-axis in degrees.
     **/
    private static float pitch = 45.0f;
    /**
     * Rotational angle for y-axis in degrees.
     **/
    private static float yaw = 45.0f;
    /**
     * The GL Utility.
     */
    private GLU glu;
    /**
     * X location.
     */
    private float x = 0.0f;
    /**
     * Y location.
     */
    private float y = 0.0f;
    /**
     * Z location.
     */
    private float z = -30.0f;
    private int mouseX = Main.FRAME_WIDTH / 2;
    private int mouseY = Main.FRAME_HEIGHT / 2;
    private int sizeX, sizeY, sizeZ, renderedHeight;
    private Schematic schematic;
    private ListTag<CompoundTag> palette;
    private String path;

    // TODO Right-click could make the view moveable, and scrolling could zoom.
    public SchematicRenderer(GLCapabilitiesImmutable userCapsRequest) {
        super(userCapsRequest);

        addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable drawable) {
                GL4bc gl = drawable.getGL().getGL4bc(); // get the OpenGL graphics context
                glu = new GLU(); // get GL Utilities
                gl.glClearColor(0.8f, 0.8f, 0.8f, 0.0f); // set background (grey) color
                gl.glClearDepth(1.0f); // set clear depth value to farthest
                gl.glEnable(GL_DEPTH_TEST); // enables depth testing
                gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
                gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
                gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smooths out lighting
                drawable.getGL().setSwapInterval(1);
                // Set up the lighting for Light-1
                // Ambient light does not come from a particular direction. Need some ambient
                // light to light up the scene. Ambient's value in RGBA
                float[] lightAmbientValue = {0.2f, 0.2f, 0.2f, 1.0f};
                // Diffuse light comes from a particular location. Diffuse's value in RGBA
                float[] lightDiffuseValue = {0.75f, 0.75f, 0.75f, 1.0f};
                // Diffuse light location xyz (in front of the screen).
                float[] lightDiffusePosition = {8.0f, 0.0f, 8.0f, 1.0f};
                gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue, 0);
                gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
                gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
                gl.glEnable(GL_COLOR_MATERIAL); // allow color on faces
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
            }

            @Override
            public void display(GLAutoDrawable drawable) {
                if (schematic != null) {
                    GL4bc gl = drawable.getGL().getGL4bc();
                    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    gl.glLoadIdentity(); // reset the model-view matrix
                    gl.glTranslatef(x, y, z); // translate into the screen
                    gl.glRotatef(pitch, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
                    gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
                    // draw schematic border
                    SchematicBorder.draw(gl, sizeX, sizeY, sizeZ);
                    // draw a cube
                    float translateX = (float) sizeX / 2.0f;
                    float translateY = (float) sizeY / 2.0f;
                    float translateZ = (float) sizeZ / 2.0f;
                    for (int x = 0; x < sizeX; x++) {
                        for (int y = 0; y < renderedHeight; y++) {
                            for (int z = 0; z < sizeZ; z++) {
                                Object block = schematic.getBlock(x, y, z);
                                if (block != null) {
                                    String blockId;
                                    CompoundTag properties;
                                    if (schematic instanceof NbtSchematic nbtSchematic && nbtSchematic.hasPaletteList()) {
                                        blockId = nbtSchematic.getBlockId(block, palette);
                                        properties = nbtSchematic.getBlockProperties(block, palette);
                                    } else {
                                        blockId = schematic.getBlockId(block);
                                        properties = schematic.getBlockProperties(block);
                                    }
                                    gl.glPushMatrix();

                                    // bottom-left-front corner of cube is (0,0,0) so we need to center it at the origin
                                    gl.glTranslatef((x - translateX) * SCALE, (y - translateY) * SCALE, (z - translateZ) * SCALE);
                                    ModelRenderer.readBlockState(gl, blockId, properties);
                                    gl.glPopMatrix();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                GL4bc gl = drawable.getGL().getGL4bc(); // get the OpenGL graphics context
                if (height == 0) {
                    height = 1; // prevent divide by zero
                }
                float aspect = (float) width / height;
                // Set the view port (display area) to cover the entire window
                gl.glViewport(0, 0, width, height);
                // Setup perspective projection, with aspect ratio matches viewport
                gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
                gl.glLoadIdentity(); // reset projection matrix
                glu.gluPerspective(45.0, aspect, 2.0, 1000.0); // fovy, aspect, zNear, zFar
                // Enable the model-view transform
                gl.glMatrixMode(GL_MODELVIEW);
                gl.glLoadIdentity(); // reset
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> z++;
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> z--;
                    case KeyEvent.VK_A -> x++;
                    case KeyEvent.VK_D -> x--;
                    case KeyEvent.VK_SHIFT -> y++;
                    case KeyEvent.VK_SPACE -> y--;
                    case KeyEvent.VK_LEFT -> {
                        if (renderedHeight > 0) {
                            renderedHeight--;
                        }
                        if (renderedHeight < 0) {
                            renderedHeight = 0;
                        }
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (renderedHeight < sizeY) {
                            renderedHeight++;
                        }
                        if (renderedHeight > sizeY) {
                            renderedHeight = sizeY;
                        }
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                requestFocus();
                // change the camera angle
                final int buffer = 0;
                if (e.getX() < mouseX - buffer || e.getX() > mouseX + buffer) {
                    yaw += e.getX() - mouseX;
                }
                if (pitch + e.getY() - mouseY > 90) {
                    pitch = 90;
                } else if (pitch + e.getY() - mouseY < -90) {
                    pitch = -90;
                } else {
                    if (e.getY() < mouseY - buffer || e.getY() > mouseY + buffer) {
                        pitch += e.getY() - mouseY;
                    }
                }
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocus();
            }
        });
    }

    public int getRenderedHeight() {
        return renderedHeight;
    }

    public void setPalette(ListTag<CompoundTag> palette) {
        this.palette = palette;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) throws IOException, JSONException {
        this.path = path;
        Schematic schematic = Schematic.openFrom(path);
        if (schematic != null) {
            setSchematic(schematic);
            renderedHeight = sizeY;
        } else {
            System.err.println("Not a schematic file!");
        }
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
        // get dimensions
        int[] size = this.schematic.getSize();
        sizeX = size[0];
        sizeY = size[1];
        sizeZ = size[2];
    }
}
