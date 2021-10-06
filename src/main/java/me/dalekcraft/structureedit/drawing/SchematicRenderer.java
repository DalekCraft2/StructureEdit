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
package me.dalekcraft.structureedit.drawing;

import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import me.dalekcraft.structureedit.Main;
import me.dalekcraft.structureedit.schematic.NbtStructure;
import me.dalekcraft.structureedit.schematic.Schematic;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.*;

import static com.jogamp.opengl.GL4bc.*;

/**
 * @author eccentric_nz
 */
public class SchematicRenderer extends GLJPanel {

    public static final float SCALE = 1.0f;
    private static final float ROTATION_SENSITIVITY = 1.0f;
    private static final float MOTION_SENSITIVITY = 0.1f;
    /**
     * Rotational angle for x-axis in degrees.
     **/
    private float pitch = 45.0f;
    /**
     * Rotational angle for y-axis in degrees.
     **/
    private float yaw = 45.0f;
    /**
     * X location.
     */
    private float x;
    /**
     * Y location.
     */
    private float y;
    /**
     * Z location.
     */
    private float z = -30.0f;
    private int mouseX = Main.FRAME_WIDTH / 2;
    private int mouseY = Main.FRAME_HEIGHT / 2;
    private int sizeX, sizeY, sizeZ, renderedHeight;
    private Schematic schematic;
    private ListTag<CompoundTag> palette;
    private Animator animator;

    public SchematicRenderer(GLCapabilitiesImmutable userCapsRequest) {
        super(userCapsRequest);

        addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable drawable) {
                GL4bc gl = drawable.getGL().getGL4bc(); // get the OpenGL graphics context
                gl.getContext().makeCurrent();
                gl.glClearColor(0.8f, 0.8f, 0.8f, 0.0f); // set background color to gray
                gl.glClearDepth(1.0f); // set clear depth value to farthest
                gl.glEnable(GL_DEPTH_TEST); // enables depth testing
                gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
                gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
                gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smooths out lighting
                gl.setSwapInterval(1);
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
                gl.glEnable(GL_CULL_FACE);

                animator = new Animator(SchematicRenderer.this);
                animator.setRunAsFastAsPossible(true);

                animator.start();
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
                animator.stop();
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
                    float translateX = sizeX / 2.0f;
                    float translateY = sizeY / 2.0f;
                    float translateZ = sizeZ / 2.0f;
                    // bottom-left-front corner of schematic is (0,0,0) so we need to center it at the origin
                    gl.glTranslatef(-translateX, -translateY, -translateZ);
                    // draw schematic border
                    SchematicBorder.draw(gl, sizeX, sizeY, sizeZ);
                    // draw a cube
                    for (int x = 0; x < sizeX; x++) {
                        for (int y = 0; y < renderedHeight; y++) {
                            for (int z = 0; z < sizeZ; z++) {
                                Object block = schematic.getBlock(x, y, z);
                                if (block != null) {
                                    String blockId;
                                    CompoundTag properties;
                                    if (schematic instanceof NbtStructure nbtStructure && nbtStructure.hasPaletteList()) {
                                        blockId = nbtStructure.getBlockId(block, palette);
                                        properties = nbtStructure.getBlockProperties(block, palette);
                                    } else {
                                        blockId = schematic.getBlockId(block);
                                        properties = schematic.getBlockProperties(block);
                                    }

                                    gl.glPushMatrix();
                                    gl.glTranslatef(x * SCALE, y * SCALE, z * SCALE);
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
                GLU glu = GLU.createGLU(gl);
                if (height == 0) {
                    height = 1; // prevent divide by zero
                }
                float aspect = (float) width / height;
                // Set the view port (display area) to cover the entire window
                gl.glViewport(0, 0, width, height);
                // Setup perspective projection, with aspect ratio matches viewport
                gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
                gl.glLoadIdentity(); // reset projection matrix
                glu.gluPerspective(45.0, aspect, 1.0, 1000.0); // fovy, aspect, zNear, zFar
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

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocus();
            }

            @Override
            public void mouseWheelMoved(@NotNull MouseWheelEvent e) {
                z -= e.getPreciseWheelRotation();
            }

            @Override
            public void mouseDragged(@NotNull MouseEvent e) {
                requestFocus();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Rotate the camera
                    if (e.getX() < mouseX || e.getX() > mouseX) {
                        yaw += (e.getX() - mouseX) * ROTATION_SENSITIVITY;
                    }
                    if (pitch + e.getY() - mouseY > 90) {
                        pitch = 90;
                    } else if (pitch + e.getY() - mouseY < -90) {
                        pitch = -90;
                    } else {
                        if (e.getY() < mouseY || e.getY() > mouseY) {
                            pitch += (e.getY() - mouseY) * ROTATION_SENSITIVITY;
                        }
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // TODO Make the camera drag translation more accurate.
                    // Translate the camera
                    if (e.getX() < mouseX || e.getX() > mouseX) {
                        x += (e.getX() - mouseX) * MOTION_SENSITIVITY;
                    }
                    if (e.getY() < mouseY || e.getY() > mouseY) {
                        y -= (e.getY() - mouseY) * MOTION_SENSITIVITY;
                    }
                }
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseMoved(@NotNull MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void pause() {
        animator.pause();
    }

    public void resume() {
        animator.resume();
    }

    public void setPalette(ListTag<CompoundTag> palette) {
        this.palette = palette;
    }

    public void setSchematic(Schematic schematic) {
        if (schematic != null) {
            this.schematic = schematic;
            // get dimensions
            int[] size = this.schematic.getSize();
            sizeX = size[0];
            sizeY = size[1];
            sizeZ = size[2];
            renderedHeight = sizeY;
        }
    }
}
