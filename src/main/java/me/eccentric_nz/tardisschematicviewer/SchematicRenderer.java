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
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Locale;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.*;

/**
 * @author eccentric_nz
 */
public class SchematicRenderer implements GLEventListener, KeyListener, MouseMotionListener {

    private static final float ZERO_F = 0.0f;
    private static final float ONE_F = 1.0f;
    private static final float CUBE_TRANSLATION_FACTOR = 2.0f;
    /**
     * Rotational angle for x-axis in degrees.
     **/
    private static float angleX = 45.0f;
    /**
     * Rotational angle for y-axis in degrees.
     **/
    private static float angleY = 45.0f;
    private final List<Block> notThese = List.of();
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
    private float z = -60.0f;
    private int mouseX = TardisSchematicViewer.FRAME_WIDTH / 2;
    private int mouseY = TardisSchematicViewer.FRAME_HEIGHT / 2;
    private int height, width, length, max;
    private JSONObject schematic;
    private JSONArray input;
    private float[] columnAnglesX;
    private float[] rowAnglesY;
    private float[] faceAnglesZ;
    private String path;
    private boolean pathSet = false;
    private boolean schematicParsed = false;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
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
        float[] lightAmbientValue = {0.1f, 0.1f, 0.1f, 1.0f};
        // Diffuse light comes from a particular location. Diffuse's value in RGBA
        float[] lightDiffuseValue = {0.75f, 0.75f, 0.75f, 1.0f};
        // Diffuse light location xyz (in front of the screen).
        float[] lightDiffusePosition = {8.0f, 0.0f, 8.0f, 1.0f};

        gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue, 0);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
        gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
        gl.glEnable(GL_LIGHTING); // enable lighting
        gl.glEnable(GL_LIGHT1); // Enable Light-1
        gl.glEnable(GL_COLOR_MATERIAL); // allow color on faces
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if (!schematicParsed) {
            if (pathSet) {
                setSchematic(path);
                schematicParsed = true;
            }
        } else {
            GL2 gl = drawable.getGL().getGL2();
            gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity(); // reset the model-view matrix
            gl.glTranslatef(x, y, z); // translate into the screen
            gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
            gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
            // draw a cube
            int lastIndexX = width - 1;
            int lastIndexY = height - 1;
            int lastIndexZ = length - 1;
            for (int height = 0; height < this.height; height++) {
                JSONArray level = (JSONArray) input.get(height);
                for (int width = 0; width < this.width; width++) {
                    JSONArray row = (JSONArray) level.get(width);
                    for (int length = 0; length < this.length; length++) {
                        JSONObject column = (JSONObject) row.get(length);
                        String data = column.getString("data");
                        int nameEndIndex = data.contains("[") ? data.indexOf('[') : data.length();
                        String blockName = data.substring(data.indexOf(':') + 1, nameEndIndex).toUpperCase(Locale.ROOT);
                        Block block = Block.valueOf(blockName);
                        if (!notThese.contains(block)) {
                            gl.glPushMatrix();

                            gl.glRotatef(columnAnglesX[width], ONE_F, ZERO_F, ZERO_F);
                            gl.glRotatef(rowAnglesY[height], ZERO_F, ONE_F, ZERO_F);
                            gl.glRotatef(faceAnglesZ[length], ZERO_F, ZERO_F, ONE_F);

                            // bottom-left-front corner of cube is (0,0,0) so we need to center it at the origin
                            float translateX = (float) lastIndexX / 2.0f;
                            float translateY = (float) lastIndexY / 2.0f;
                            float translateZ = (float) lastIndexZ / 2.0f;
                            gl.glTranslatef((width - translateX) * CUBE_TRANSLATION_FACTOR, (height - translateY) * CUBE_TRANSLATION_FACTOR, -(length - translateZ) * CUBE_TRANSLATION_FACTOR);
                            Color color = block.getColor();
                            switch (block.getBlockShape()) {
                                case SLAB:
                                    if (data.contains("type=bottom")) {
                                        Slab.draw(gl, color, ONE_F, 0);
                                    } else if (data.contains("type=top")) {
                                        SlabUpper.draw(gl, color, ONE_F);
                                    } else {
                                        Cube.draw(gl, color, ONE_F, false);
                                    }
                                    break;
                                case FLAT:
                                    if (block.equals(Block.REDSTONE_WIRE)) {
                                        Redstone.draw(gl, ONE_F);
                                    } else {
                                        Slab.draw(gl, color, ONE_F, 0.8f);
                                    }
                                    break;
                                case STAIR:
                                    Stair.draw(gl, color, ONE_F, data);
                                    break;
                                case PLANT: {
                                    float thickness;
                                    float height1;
                                    switch (block) {
                                        case BROWN_MUSHROOM, RED_MUSHROOM, CARROTS, DEAD_BUSH, GRASS, NETHER_WART, POTATOES -> {
                                            thickness = 0.125f;
                                            height1 = 0.5f;
                                        }
                                        case WHEAT, POPPY, DANDELION -> {
                                            thickness = 0.125f;
                                            height1 = 0.8f;
                                        }
                                        default -> {
                                            thickness = 0.25f;
                                            height1 = ONE_F;
                                        }
                                    }
                                    Plant.draw(gl, color, ONE_F, thickness, height1);
                                    break;
                                }
                                case WALL:
                                    Fence.draw(gl, color, ONE_F, 0.5f, 1.9f, false);
                                    break;
                                case FENCE:
                                    Fence.draw(gl, color, ONE_F, 0.25f, 1.9f, false);
                                    break;
                                case FENCE_GATE:
                                    ThinCube.draw(gl, color, ONE_F, 0.25f, 1.7f, data, false);
                                    break;
                                case THIN:
                                    ThinCube.draw(gl, color, ONE_F, 0.125f, 2.0f, data, false);
                                    break;
                                case GLASS_PANE:
                                    ThinCube.draw(gl, color, ONE_F, 0.125f, 2.0f, data, true);
                                    break;
                                case GLASS:
                                    Cube.draw(gl, color, ONE_F, true);
                                    break;
                                case SMALL:
                                    Cube.draw(gl, color, 0.5f, false);
                                    break;
                                case STICK:
                                case CUBE:
                                    Cube.draw(gl, color, ONE_F, false);
                                    break;
                                case VOID:
                                    break;
                            }
                            gl.glPopMatrix();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
        if (height == 0) {
            height = 1; // prevent divide by zero
        }
        float aspect = (float) width / height;
        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);
        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); // choose projection matrix
        gl.glLoadIdentity(); // reset projection matrix
        glu.gluPerspective(45.0, aspect, 0.1, 200.0); // fovy, aspect, zNear, zFar
        // Enable the model-view transform
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity(); // reset
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_W -> z++;
            case KeyEvent.VK_S -> z--;
            case KeyEvent.VK_A -> x++;
            case KeyEvent.VK_D -> x--;
            case KeyEvent.VK_SPACE -> y++;
            case KeyEvent.VK_SHIFT -> y--;
            case KeyEvent.VK_UP -> z++;
            case KeyEvent.VK_DOWN -> z--;
            case KeyEvent.VK_LEFT -> {
                height--;
                if (height < 0) {
                    height = 0;
                }
            }
            case KeyEvent.VK_RIGHT -> {
                height++;
                if (height > max) {
                    height = max;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // change the camera angle
        final int buffer = 0;
        if (e.getX() < mouseX - buffer || e.getX() > mouseX + buffer) {
            angleY += e.getX() - mouseX;
        }
        if (angleX + e.getY() - mouseY > 90) {
            angleX = 90;
        } else if (angleX + e.getY() - mouseY < -90) {
            angleX = -90;
        } else {
            if (e.getY() < mouseY - buffer || e.getY() > mouseY + buffer) {
                angleX += e.getY() - mouseY;
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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMax() {
        return max;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        schematicParsed = false;
        pathSet = true;
        setSchematic(path);
    }

    public JSONObject getSchematic() {
        return schematic;
    }

    public void setSchematic(String path) {
        // Use URL so that can read from JAR and disk file.
        // Filename relative to the project root.
        schematic = Gzip.unzip(path);
        // get dimensions
        JSONObject dimensions = (JSONObject) schematic.get("dimensions");
        height = dimensions.getInt("height");
        max = height;
        width = dimensions.getInt("width");
        length = dimensions.getInt("length");
        columnAnglesX = new float[width];
        rowAnglesY = new float[height];
        faceAnglesZ = new float[length];
        input = (JSONArray) schematic.get("input");
    }

    public void setSchematic(JSONObject schematic) {
        this.schematic = schematic;
        // get dimensions
        JSONObject dimensions = (JSONObject) schematic.get("dimensions");
        height = dimensions.getInt("height");
        max = height;
        width = dimensions.getInt("width");
        length = dimensions.getInt("length");
        columnAnglesX = new float[width];
        rowAnglesY = new float[height];
        faceAnglesZ = new float[length];
        input = (JSONArray) schematic.get("input");
    }
}
