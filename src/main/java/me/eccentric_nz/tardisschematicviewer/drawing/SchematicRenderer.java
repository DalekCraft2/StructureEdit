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

import me.eccentric_nz.tardisschematicviewer.schematic.NbtSchematic;
import me.eccentric_nz.tardisschematicviewer.schematic.Schematic;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.json.JSONException;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author eccentric_nz
 */
public class SchematicRenderer { // TODO Possibly switch to GLFW, or find a way to render this inside the JFrame.

    public static final int FRAME_WIDTH = 1024;
    public static final int FRAME_HEIGHT = 600;
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
    private double mouseX = FRAME_WIDTH / 2.0;
    private double mouseY = FRAME_HEIGHT / 2.0;
    private int sizeX, sizeY, sizeZ, renderedHeight;
    private Schematic schematic;
    private ListTag<CompoundTag> palette;
    private String path;

    /**
     * The window handle.
     */
    private long window;

    public void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    // TODO Right-click could make the view moveable, and scrolling could zoom.
    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(FRAME_WIDTH, FRAME_HEIGHT, "TARDIS Schematic Viewer", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        //glfwSetWindowIcon(window, );

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                    case GLFW_KEY_W, GLFW_KEY_UP -> z++;
                    case GLFW_KEY_S, GLFW_KEY_DOWN -> z--;
                    case GLFW_KEY_A -> x++;
                    case GLFW_KEY_D -> x--;
                    case GLFW_KEY_LEFT_SHIFT -> y++;
                    case GLFW_KEY_SPACE -> y--;
                    case GLFW_KEY_LEFT -> {
                        if (renderedHeight > 0) {
                            renderedHeight--;
                        }
                        if (renderedHeight < 0) {
                            renderedHeight = 0;
                        }
                    }
                    case GLFW_KEY_RIGHT -> {
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

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            // requestFocus();
            int state = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1);
            if (state == GLFW_PRESS) {
                // change the camera angle
                final int buffer = 0;
                if (xpos < mouseX - buffer || xpos > mouseX + buffer) {
                    yaw += xpos - mouseX;
                }
                if (pitch + ypos - mouseY > 90.0) {
                    pitch = 90.0f;
                } else if (pitch + ypos - mouseY < -90.0) {
                    pitch = -90.0f;
                } else {
                    if (ypos < mouseY - buffer || ypos > mouseY + buffer) {
                        pitch += ypos - mouseY;
                    }
                }
            }
            mouseX = xpos;
            mouseY = ypos;
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            // requestFocus();
        });

        GLFWVidMode vidmode;

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        createCapabilities();

        glClearDepth(1.0f); // set clear depth value to farthest
        glEnable(GL_DEPTH_TEST); // enables depth testing
        glDepthFunc(GL_LEQUAL); // the type of depth test to do
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        glShadeModel(GL_SMOOTH); // blends colors nicely, and smooths out lighting
        // Set up the lighting for Light-1
        // Ambient light does not come from a particular direction. Need some ambient
        // light to light up the scene. Ambient's value in RGBA
        float[] lightAmbientValue = {0.2f, 0.2f, 0.2f, 1.0f};
        // Diffuse light comes from a particular location. Diffuse's value in RGBA
        float[] lightDiffuseValue = {0.75f, 0.75f, 0.75f, 1.0f};
        // Diffuse light location xyz (in front of the screen).
        float[] lightDiffusePosition = {8.0f, 0.0f, 8.0f, 1.0f};
        glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue);
        glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue);
        glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition);
        glEnable(GL_COLOR_MATERIAL); // allow color on faces
    }

    public void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        createCapabilities();

        // Set the clear color to gray
        glClearColor(0.8f, 0.8f, 0.8f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if (schematic != null) {
                IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
                IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
                glfwGetWindowSize(window, widthBuffer, heightBuffer);
                int width = widthBuffer.get(0);
                int height = heightBuffer.get(0);

                if (height == 0) {
                    height = 1; // prevent divide by zero
                }
                float aspect = (float) width / height;
                // Set the view port (display area) to cover the entire window
                glViewport(0, 0, width, height);
                // Setup perspective projection, with aspect ratio matches viewport
                glMatrixMode(GL_PROJECTION); // choose projection matrix
                glLoadIdentity(); // reset projection matrix
                gluPerspective(45.0f, aspect, 2.0f, 1000.0f); // fovy, aspect, zNear, zFar
                // Enable the model-view transform
                glMatrixMode(GL_MODELVIEW);
                glLoadIdentity(); // reset the model-view matrix

                glTranslatef(x, y, z); // translate into the screen
                glRotatef(pitch, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
                glRotatef(yaw, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
                // draw schematic border
                SchematicBorder.draw(sizeX, sizeY, sizeZ);
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
                                glPushMatrix();

                                // bottom-left-front corner of cube is (0,0,0) so we need to center it at the origin
                                glTranslatef((x - translateX) * SCALE, (y - translateY) * SCALE, (z - translateZ) * SCALE);
                                ModelRenderer.readBlockState(blockId, properties);
                                glPopMatrix();
                            }
                        }
                    }
                }
            }

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void gluPerspective(float fovy, float aspect, float near, float far) {
        float bottom = -near * (float) Math.tan(fovy / 2);
        float top = -bottom;
        float left = aspect * bottom;
        float right = -left;
        glFrustum(left, right, bottom, top, near, far);
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
