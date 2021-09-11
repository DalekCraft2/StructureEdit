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

import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import me.eccentric_nz.tardisschematicviewer.drawing.*;
import me.eccentric_nz.tardisschematicviewer.util.GzipUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Locale;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.*;

/**
 * @author eccentric_nz
 */
public class SchematicRenderer extends GLJPanel {

    private static final float ZERO_F = 0.0f;
    private static final float ONE_F = 1.0f;
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
    private int sizeX, sizeY, sizeZ, renderedHeight;
    private Object schematic;
    private Object input;
    private ListTag<CompoundTag> palette;
    private String path;
    private boolean schematicParsed = false;

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

                gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                gl.glEnable(GL_BLEND);
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
            }

            @Override
            public void display(GLAutoDrawable drawable) {
                if (!schematicParsed) {
                    if (path != null) {
                        try {
                            setPath(path);
                        } catch (IOException | JSONException e) {
                            System.err.println("Error reading schematic: " + e.getMessage());
                            schematicParsed = false;
                        }
                    }
                } else {
                    GL4bc gl = drawable.getGL().getGL4bc();
                    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    gl.glLoadIdentity(); // reset the model-view matrix
                    gl.glTranslatef(x, y, z); // translate into the screen
                    gl.glRotatef(pitch, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
                    gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
                    // draw a cube
                    if (path.endsWith(".tschm")) {
                        displayTschm(gl);
                    } else if (path.endsWith(".nbt")) {
                        displayNbt(gl);
                    } else {
                        System.err.println("Not a schematic file!");
                        schematicParsed = false;
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
                gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); // choose projection matrix
                gl.glLoadIdentity(); // reset projection matrix
                glu.gluPerspective(45.0, aspect, 2.0, 1000.0); // fovy, aspect, zNear, zFar
                // Enable the model-view transform
                gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
                gl.glLoadIdentity(); // reset
            }

            public void displayTschm(GL4bc gl) {
                int lastIndexX = sizeZ - 1;
                int lastIndexY = sizeY - 1;
                int lastIndexZ = sizeX - 1;
                for (int y = 0; y < renderedHeight; y++) {
                    JSONArray level = ((JSONArray) input).getJSONArray(y);
                    for (int x = 0; x < sizeZ; x++) {
                        JSONArray row = level.getJSONArray(x);
                        for (int z = 0; z < sizeX; z++) {
                            JSONObject column = row.getJSONObject(z);
                            String data = column.getString("data");
                            int nameEndIndex = data.contains("[") ? data.indexOf('[') : data.length();
                            String blockName = data.substring(data.indexOf(':') + 1, nameEndIndex).toUpperCase(Locale.ROOT);
                            String blockData = data.contains("[") ? data.substring(data.indexOf('[')) : "";
                            Block block = Block.valueOf(blockName);
                            gl.glPushMatrix();

                            // bottom-left-front corner of cube is (0,0,0) so we need to center it at the origin
                            float translateX = (float) lastIndexX / 2.0f;
                            float translateY = (float) lastIndexY / 2.0f;
                            float translateZ = (float) lastIndexZ / 2.0f;
                            gl.glTranslatef((x - translateX) * CUBE_TRANSLATION_FACTOR, (y - translateY) * CUBE_TRANSLATION_FACTOR, (z - translateZ) * CUBE_TRANSLATION_FACTOR);
                            blockSwitch(gl, block, blockData);
                            gl.glPopMatrix();
                        }
                    }
                }
            }

            public void displayNbt(GL4bc gl) {
                int lastIndexX = sizeX - 1;
                int lastIndexY = sizeY - 1;
                int lastIndexZ = sizeZ - 1;
                CompoundTag schematicTag = ((CompoundTag) ((NamedTag) schematic).getTag());
                ListTag<CompoundTag> blocks = schematicTag.getListTag("blocks").asCompoundTagList();
                for (CompoundTag blockTag : blocks) {
                    ListTag<IntTag> position = blockTag.getListTag("pos").asIntTagList();
                    int x = position.get(0).asInt();
                    int y = position.get(1).asInt();
                    int z = position.get(2).asInt();
                    if (x < sizeX && y < renderedHeight && z < sizeZ) {
                        String namespacedBlockName = palette.get(blockTag.getInt("state")).getString("Name");
                        String blockName = namespacedBlockName.substring(namespacedBlockName.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                        Block block = Block.valueOf(blockName);
                        CompoundTag properties = palette.get(blockTag.getInt("state")).getCompoundTag("Properties");
                        gl.glPushMatrix();

                        // bottom-left-front corner of cube is (0,0,0) so we need to center it at the origin
                        float translateX = (float) lastIndexX / 2.0f;
                        float translateY = (float) lastIndexY / 2.0f;
                        float translateZ = (float) lastIndexZ / 2.0f;
                        gl.glTranslatef((x - translateX) * CUBE_TRANSLATION_FACTOR, (y - translateY) * CUBE_TRANSLATION_FACTOR, (z - translateZ) * CUBE_TRANSLATION_FACTOR);
                        blockSwitch(gl, block, properties);
                        gl.glPopMatrix();
                    }
                }
            }

            public void blockSwitch(GL4bc gl, Block block, Object properties) {
                Color color = block.getColor();
                switch (block.getBlockShape()) {
                    case SLAB:
                        Slab.draw(gl, color, ONE_F, 0.5f, ONE_F, properties);
                        break;
                    case FLAT:
                        if (block.equals(Block.REDSTONE_WIRE)) {
                            Redstone.draw(gl, color, 0.25f, ONE_F, 0.125f, ONE_F, properties);
                        } else if (block.equals(Block.TRIPWIRE)) {
                            Pane.draw(gl, color, 0.125f, ONE_F, 0.125f, ONE_F, properties);
                        } else {
                            Slab.draw(gl, color, ONE_F, 0.2f, ONE_F, properties);
                        }
                        break;
                    case STAIR:
                        Stair.draw(gl, color, ONE_F, ONE_F, ONE_F, properties);
                        break;
                    case PLANT: {
                        float thickness;
                        float sizeY;
                        switch (block) {
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
                                sizeY = ONE_F;
                            }
                        }
                        Plant.draw(gl, color, thickness, ONE_F, sizeY, ONE_F);
                        break;
                    }
                    case WALL:
                        Wall.draw(gl, color, 0.5f, ONE_F, ONE_F, ONE_F, properties);
                        break;
                    case FENCE:
                        Fence.draw(gl, color, 0.25f, ONE_F, ONE_F, ONE_F, properties);
                        break;
                    case FENCE_GATE:
                        Rotational.draw(gl, color, ONE_F, 0.7f, 0.125f, properties);
                        break;
                    case THIN:
                        Rotational.draw(gl, color, ONE_F, ONE_F, 0.125f, properties);
                        break;
                    case PANE:
                        Pane.draw(gl, color, 0.125f, ONE_F, ONE_F, ONE_F, properties);
                        break;
                    case STICK:
                        Cube.draw(gl, color, 0.25f, 0.9f, 0.25f);
                        break;
                    case WALL_STICK:
                        WallStick.draw(gl, color, 0.25f, 0.9f, 0.25f, properties);
                        break;
                    case SMALL:
                        Cube.draw(gl, color, 0.5f, 0.5f, 0.5f);
                        break;
                    case CUBE:
                        Cube.draw(gl, color, ONE_F, ONE_F, ONE_F);
                        break;
                    case VOID:
                        break;
                }
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
        if (path.endsWith(".tschm")) {
            setSchematic(new JSONObject(GzipUtils.unzip(path)));
            renderedHeight = sizeY;
            schematicParsed = true;
        } else if (path.endsWith(".nbt")) {
            setSchematic(NBTUtil.read(path));
            renderedHeight = sizeY;
            schematicParsed = true;
        } else {
            System.err.println("Not a schematic file!");
            schematicParsed = false;
        }
    }

    public Object getSchematic() {
        return schematic;
    }

    public void setSchematic(JSONObject schematic) {
        this.schematic = schematic;
        // get dimensions
        JSONObject dimensions = schematic.getJSONObject("dimensions");
        sizeX = dimensions.getInt("length");
        sizeY = dimensions.getInt("height");
        sizeZ = dimensions.getInt("width");
        input = schematic.getJSONArray("input");
    }

    public void setSchematic(NamedTag schematic) {
        this.schematic = schematic;
        // get dimensions
        ListTag<IntTag> size = ((CompoundTag) schematic.getTag()).getListTag("size").asIntTagList();
        sizeX = size.get(0).asInt();
        sizeY = size.get(1).asInt();
        sizeZ = size.get(2).asInt();
        input = ((CompoundTag) schematic.getTag()).getListTag("blocks");
    }
}
