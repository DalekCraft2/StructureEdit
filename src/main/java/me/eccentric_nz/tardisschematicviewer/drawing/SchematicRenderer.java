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

import me.eccentric_nz.tardisschematicviewer.Main;
import me.eccentric_nz.tardisschematicviewer.schematic.NbtSchematic;
import me.eccentric_nz.tardisschematicviewer.schematic.Schematic;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.json.JSONException;
import org.lwjgl.opengl.awt.AWTGLCanvas;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Locale;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL46.*;

/**
 * @author eccentric_nz
 */
public class SchematicRenderer extends AWTGLCanvas { // TODO Possibly switch to GLFW, or find a way to render this inside the JFrame.

    private static final float CUBE_TRANSLATION_FACTOR = 2.0f;
    /**
     * Rotational angle for x-axis in degrees.
     **/
    private static float pitch = 45.0f;
    /**
     * Rotational angle for y-axis in degrees.
     **/
    private static float yaw = 45.0f;
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
    private float z = -60.0f;
    private int mouseX = Main.FRAME_WIDTH / 2;
    private int mouseY = Main.FRAME_HEIGHT / 2;
    private int sizeX, sizeY, sizeZ, renderedHeight;
    private Schematic schematic;
    private ListTag<CompoundTag> palette;
    private String path;

    public SchematicRenderer() {

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

    public void initGL() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        createCapabilities();

        glClearColor(0.8f, 0.8f, 0.8f, 0.0f); // set background color to gray
        glClearDepth(1.0f); // set clear depth value to farthest
        glEnable(GL_DEPTH_TEST); // enables depth testing
        glDepthFunc(GL_LEQUAL); // the type of depth test to do
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        glShadeModel(GL_SMOOTH); // blends colors nicely, and smooths out lighting
        // Set up the lighting for Light-1
        // Ambient light does not come from a particular direction. Need some ambient
        // light to light up the scene. Ambient's value in RGBA
        float[] lightAmbientValue = {0.1f, 0.1f, 0.1f, 1.0f};
        // Diffuse light comes from a particular location. Diffuse's value in RGBA
        float[] lightDiffuseValue = {0.75f, 0.75f, 0.75f, 1.0f};
        // Diffuse light location xyz (in front of the screen).
        float[] lightDiffusePosition = {8.0f, 0.0f, 8.0f, 1.0f};

        glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue);
        glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue);
        glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition);
        glEnable(GL_LIGHTING); // enable lighting
        glEnable(GL_LIGHT1); // Enable Light-1
        glEnable(GL_COLOR_MATERIAL); // allow color on faces

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        int width = getWidth();
        int height = getHeight();

        if (height == 0) {
            height = 1; // prevent divide by zero
        }
        float aspect = (float) width / height;
        // Set the view port (display area) to cover the entire window
        glViewport(0, 0, width, height);
        // Setup perspective projection, with aspect ratio matches viewport
        glMatrixMode(GL_PROJECTION); // choose projection matrix
        glLoadIdentity(); // reset projection matrix
        gluPerspective(45.0, aspect, 2.0, 1000.0); // fovy, aspect, zNear, zFar
        // Enable the model-view transform
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity(); // reset
    }

    public static void gluPerspective(double fovy, double aspect, double near, double far) {
        double bottom = -near * Math.tan(fovy / 2);
        double top = -bottom;
        double left = aspect * bottom;
        double right = -left;
        glFrustum(left, right, bottom, top, near, far);
    }

    public void paintGL() {

        if (schematic != null) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glLoadIdentity(); // reset the model-view matrix
            glTranslatef(x, y, z); // translate into the screen
            glRotatef(pitch, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
            glRotatef(yaw, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
            // draw schematic border
            SchematicBorder.draw(sizeX, sizeY, sizeZ);
            // draw a cube
            float translateX = (float) (sizeX - 1) / 2.0f;
            float translateY = (float) (sizeY - 1) / 2.0f;
            float translateZ = (float) (sizeZ - 1) / 2.0f;
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
                            String blockName = blockId.substring(blockId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                            Block blockEnum = Block.valueOf(blockName);
                            glPushMatrix();

                            // bottom-left-front corner of cube is (0,0,0) so we need to center it at the origin
                            glTranslatef((x - translateX) * CUBE_TRANSLATION_FACTOR, (y - translateY) * CUBE_TRANSLATION_FACTOR, (z - translateZ) * CUBE_TRANSLATION_FACTOR);
                            Color color = blockEnum.getColor();
                            switch (blockEnum.getBlockShape()) {
                                case CUBE:
                                    Cube.draw(color, 1.0f, 1.0f, 1.0f);
                                    break;
                                case FENCE:
                                    Fence.draw(color, 0.25f, 1.0f, 1.0f, 1.0f, properties);
                                    break;
                                case FENCE_GATE:
                                    Rotational.draw(color, 1.0f, 0.7f, 0.125f, properties);
                                    break;
                                case FLAT:
                                    if (blockEnum.equals(Block.REDSTONE_WIRE)) {
                                        Redstone.draw(color, 0.25f, 1.0f, 0.125f, 1.0f, properties);
                                    } else if (blockEnum.equals(Block.TRIPWIRE)) {
                                        Pane.draw(color, 0.125f, 1.0f, 0.125f, 1.0f, properties);
                                    } else {
                                        Slab.draw(color, 1.0f, 0.2f, 1.0f, properties);
                                    }
                                    break;
                                case PANE:
                                    Pane.draw(color, 0.125f, 1.0f, 1.0f, 1.0f, properties);
                                    break;
                                case PLANT: {
                                    float thickness;
                                    float sizeY;
                                    switch (blockEnum) {
                                        case BROWN_MUSHROOM, RED_MUSHROOM, CARROTS, DEAD_BUSH, GRASS, NETHER_WART, POTATOES -> {
                                            thickness = 0.125f;
                                            sizeY = 0.5f;
                                        }
                                        case WHEAT, POPPY, DANDELION -> {
                                            thickness = 0.125f;
                                            sizeY = 0.8f;
                                        }
                                        default -> {
                                            thickness = 0.25f;
                                            sizeY = 1.0f;
                                        }
                                    }
                                    Plant.draw(color, thickness, 1.0f, sizeY, 1.0f);
                                    break;
                                }
                                case SLAB:
                                    Slab.draw(color, 1.0f, 0.5f, 1.0f, properties);
                                    break;
                                case SMALL:
                                    Cube.draw(color, 0.5f, 0.5f, 0.5f);
                                    break;
                                case STAIR:
                                    Stair.draw(color, 1.0f, 1.0f, 1.0f, properties);
                                    break;
                                case STICK:
                                    Cube.draw(color, 0.25f, 0.9f, 0.25f);
                                    break;
                                case THIN:
                                    Rotational.draw(color, 1.0f, 1.0f, 0.125f, properties);
                                    break;
                                case WALL:
                                    Wall.draw(color, 0.5f, 1.0f, 1.0f, 1.0f, properties);
                                    break;
                                case WALL_STICK:
                                    WallStick.draw(color, 0.25f, 0.9f, 0.25f, properties);
                                    break;
                                case VOID:
                                    break;
                            }
                            glPopMatrix();
                        }
                    }
                }
            }
        }
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
