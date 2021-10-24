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
package me.dalekcraft.structureedit.ui;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.Texture;
import me.dalekcraft.structureedit.Main;
import me.dalekcraft.structureedit.drawing.BlockColor;
import me.dalekcraft.structureedit.exception.ValidationException;
import me.dalekcraft.structureedit.schematic.*;
import me.dalekcraft.structureedit.util.Assets;
import me.dalekcraft.structureedit.util.Configuration;
import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.StringTag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

import static com.jogamp.opengl.GL4bc.*;
import static me.dalekcraft.structureedit.schematic.Schematic.openFrom;

/**
 * @author eccentric_nz
 */
public class UserInterface {
    private static final Logger LOGGER = LogManager.getLogger(UserInterface.class);
    private static final FileNameExtensionFilter FILTER_NBT = new FileNameExtensionFilter(Configuration.LANGUAGE.getProperty("ui.file_chooser.extension.nbt"), NbtStructure.EXTENSION);
    private static final FileNameExtensionFilter FILTER_MCEDIT = new FileNameExtensionFilter(Configuration.LANGUAGE.getProperty("ui.file_chooser.extension.mcedit"), McEditSchematic.EXTENSION);
    private static final FileNameExtensionFilter FILTER_SPONGE = new FileNameExtensionFilter(Configuration.LANGUAGE.getProperty("ui.file_chooser.extension.sponge"), SpongeSchematic.EXTENSION);
    private static final FileNameExtensionFilter FILTER_TARDIS = new FileNameExtensionFilter(Configuration.LANGUAGE.getProperty("ui.file_chooser.extension.tardis"), TardisSchematic.EXTENSION);
    public final JFileChooser schematicChooser = new JFileChooser();
    public final JFileChooser assetsChooser = new JFileChooser();
    public JComboBox<String> blockIdComboBox;
    private JSplitPane splitPane;
    private int renderedHeight;
    private Animator animator;
    private BlockButton selected;
    private Schematic schematic;
    private JPanel panel;
    private JPanel rendererPanel;
    private JPanel editorPanel;
    private JPanel gridPanel;
    private JLabel blockIdLabel;
    private JLabel blockPropertiesLabel;
    private JFormattedTextField blockPropertiesTextField;
    private JLabel layerLabel;
    private JSpinner layerSpinner;
    private JLabel blockPositionLabel;
    private JTextField blockPositionTextField;
    private JLabel blockNbtLabel;
    private JFormattedTextField blockNbtTextField;
    private JLabel paletteLabel;
    private JSpinner paletteSpinner;
    private JLabel blockPaletteLabel;
    private JSpinner blockPaletteSpinner;
    private JLabel sizeLabel;
    private JTextField sizeTextField;
    private JTabbedPane tabbedPane;
    private JTextPane logPane;
    private JPanel logTab;

    {
        schematicChooser.addChoosableFileFilter(FILTER_NBT);
        schematicChooser.addChoosableFileFilter(FILTER_MCEDIT);
        schematicChooser.addChoosableFileFilter(FILTER_SPONGE);
        schematicChooser.addChoosableFileFilter(FILTER_TARDIS);
        assetsChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public UserInterface() {
        new AutoCompletion(blockIdComboBox);
        SchematicRenderer renderer = new SchematicRenderer();
        ((GLJPanel) rendererPanel).addGLEventListener(renderer);
        rendererPanel.addKeyListener(renderer);
        rendererPanel.addMouseListener(renderer);
        rendererPanel.addMouseMotionListener(renderer);
        rendererPanel.addMouseWheelListener(renderer);

        gridPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (schematic != null) {
                    loadLayer();
                }
            }
        });

        layerSpinner.addChangeListener(e -> {
            if (schematic != null) {
                loadLayer();
            }
        });

        blockIdComboBox.setModel(new DefaultComboBoxModel<>(Assets.getBlockStateArray()));
        blockIdComboBox.setSelectedItem(null);
        blockIdComboBox.addItemListener(e -> {
            if (schematic != null && selected != null && blockIdComboBox.getSelectedItem() != null) {
                Schematic.Block block = selected.getBlock();
                String blockId = blockIdComboBox.getSelectedItem().toString();
                block.setId(blockId);
                loadLayer();
            }
        });
        // TODO Perhaps change the properties and NBT text fields to JTrees, and create NBTExplorer-esque editors for them.
        blockPropertiesTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (schematic != null && selected != null) {
                    Schematic.Block block = selected.getBlock();
                    try {
                        block.setPropertiesAsString(blockPropertiesTextField.getText());
                        blockPropertiesTextField.setForeground(Color.BLACK);
                    } catch (IOException e1) {
                        blockPropertiesTextField.setForeground(Color.RED);
                    }
                    loadLayer();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        blockNbtTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (schematic != null && !(schematic instanceof TardisSchematic) && selected != null) {
                    Schematic.Block block = selected.getBlock();
                    try {
                        block.setSnbt(blockNbtTextField.getText());
                        blockNbtTextField.setForeground(Color.BLACK);
                    } catch (IOException e1) {
                        blockNbtTextField.setForeground(Color.RED);
                    }
                    loadLayer();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        paletteSpinner.addChangeListener(e -> {
            if (schematic != null) {
                if (schematic instanceof MultiPaletteSchematic multiPaletteSchematic && multiPaletteSchematic.hasPaletteList()) {
                    multiPaletteSchematic.setActivePalette((Integer) paletteSpinner.getValue());
                    loadLayer();
                    updateSelected();
                }
            }
        });
        // TODO Blockbench-style palette editor, with a list of palettes and palette IDs (This will also involve separating palette editing and block editing).
        blockPaletteSpinner.addChangeListener(e -> {
            if (schematic != null && schematic instanceof PaletteSchematic && selected != null) {
                Schematic.Block block = selected.getBlock();
                if (block instanceof PaletteSchematic.PaletteBlock paletteBlock) {
                    paletteBlock.setStateIndex((Integer) blockPaletteSpinner.getValue());
                    loadLayer();
                    updateSelected();
                }
            }
        });

        JTextPaneAppender.addLog4j2TextPaneAppender(logPane);
    }

    public void showControlsDialog() {
        animator.pause();
        JOptionPane.showMessageDialog(Main.frame, Configuration.LANGUAGE.getProperty("ui.menu_bar.help_menu.controls.dialog"), Configuration.LANGUAGE.getProperty("ui.menu_bar.help_menu.controls.title"), JOptionPane.INFORMATION_MESSAGE);
        animator.resume();
    }

    public void selectAssets() {
        animator.pause();
        assetsChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = assetsChooser.showOpenDialog(panel);
        if (result == JFileChooser.APPROVE_OPTION && assetsChooser.getSelectedFile() != null) {
            Path assets = assetsChooser.getSelectedFile().toPath();
            Assets.setAssets(assets);
            Object selectedItem = blockIdComboBox.getSelectedItem();
            blockIdComboBox.setModel(new DefaultComboBoxModel<>(Assets.getBlockStateArray()));
            blockIdComboBox.setSelectedItem(selectedItem);
        }
        updateSelected();
        animator.resume();
    }

    public void saveSchematic() {
        if (schematic != null) {
            animator.pause();
            schematicChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = schematicChooser.showSaveDialog(Main.frame);
            if (result == JFileChooser.APPROVE_OPTION && schematicChooser.getSelectedFile() != null) {
                try {
                    File file = schematicChooser.getSelectedFile();
                    LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.schematic.saving"), file);
                    schematic.saveTo(file);
                    LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.schematic.saved"), file);
                    Main.frame.setTitle(String.format(Configuration.LANGUAGE.getProperty("ui.window.title_with_file"), file.getName()));
                } catch (IOException e1) {
                    LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.error_saving"), e1.getMessage());
                }
            }
            animator.resume();
        } else {
            LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.null"));
        }
    }

    public void openSchematic() {
        animator.pause();
        int result = schematicChooser.showOpenDialog(Main.frame);
        if (result == JFileChooser.APPROVE_OPTION && schematicChooser.getSelectedFile() != null) {
            File file = schematicChooser.getSelectedFile();
            open(file);
        }
        animator.resume();
    }

    public void open(@NotNull File file) {
        LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.schematic.loading"), file);
        try {
            schematic = openFrom(file);
            selected = null;
        } catch (IOException | JSONException e) {
            LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.error_reading"), e.getMessage());
            Main.frame.setTitle(Configuration.LANGUAGE.getProperty("ui.window.title"));
            schematic = null;
        } catch (ValidationException e) {
            LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.invalid"), e.getMessage());
            Main.frame.setTitle(Configuration.LANGUAGE.getProperty("ui.window.title"));
            schematic = null;
        } catch (org.everit.json.schema.ValidationException e) {
            List<String> messages = e.getAllMessages();
            if (messages.size() > 1) {
                LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.invalid"), e.getViolationCount());
            }
            messages.forEach(message -> LOGGER.log(Level.ERROR, message));
            Main.frame.setTitle(Configuration.LANGUAGE.getProperty("ui.window.title"));
            schematic = null;
        } catch (UnsupportedOperationException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            sizeTextField.setText(null);
            paletteSpinner.setValue(0);
            paletteSpinner.setEnabled(false);
            blockPositionTextField.setText(null);
            blockPositionTextField.setEnabled(false);
            blockIdComboBox.setSelectedItem(null);
            blockIdComboBox.setEnabled(false);
            blockPropertiesTextField.setValue(null);
            blockPropertiesTextField.setEnabled(false);
            blockNbtTextField.setValue(null);
            blockNbtTextField.setEnabled(false);
            blockPaletteSpinner.setValue(0);
            blockPaletteSpinner.setEnabled(false);
            if (schematic != null) {
                sizeTextField.setEnabled(true);
                layerSpinner.setEnabled(true);
                int[] size = schematic.getSize();
                renderedHeight = size[1];
                SpinnerModel layerModel = new SpinnerNumberModel(0, 0, size[1] - 1, 1);
                layerSpinner.setModel(layerModel);
                if (schematic instanceof PaletteSchematic paletteSchematic) {
                    if (paletteSchematic instanceof MultiPaletteSchematic multiPaletteSchematic && multiPaletteSchematic.hasPaletteList()) {
                        int palettesSize = multiPaletteSchematic.getPaletteList().size();
                        SpinnerModel paletteModel = new SpinnerNumberModel(0, 0, palettesSize - 1, 1);
                        paletteSpinner.setEnabled(true);
                        paletteSpinner.setModel(paletteModel);
                        multiPaletteSchematic.setActivePalette(0);
                    }
                    int paletteSize = paletteSchematic.getPalette().size();
                    SpinnerModel blockPaletteModel = new SpinnerNumberModel(0, 0, paletteSize - 1, 1);
                    blockPaletteSpinner.setModel(blockPaletteModel);
                }
                LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.schematic.loaded"), file);
                Main.frame.setTitle(String.format(Configuration.LANGUAGE.getProperty("ui.window.title_with_file"), file.getName()));
            }
            loadLayer();
        });
    }

    // TODO Make the editor built into the 3D view instead of being a layer-by-layer editor.
    public void loadLayer() {
        gridPanel.removeAll();
        gridPanel.setLayout(null);
        gridPanel.updateUI();
        if (schematic != null) {
            int[] size = schematic.getSize();
            sizeTextField.setText(Arrays.toString(size));
            int currentLayer = (int) layerSpinner.getValue();
            int buttonSideLength = Math.min(gridPanel.getWidth() / size[0], gridPanel.getHeight() / size[2]);
            for (int x = 0; x < size[0]; x++) {
                for (int z = 0; z < size[2]; z++) {
                    Schematic.Block block = schematic.getBlock(x, currentLayer, z);
                    if (block != null) {
                        String blockId = block.getId();
                        String blockName = blockId.substring(blockId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                        Color color;
                        try {
                            color = BlockColor.valueOf(blockName).getColor();
                        } catch (IllegalArgumentException e) {
                            color = new Color(251, 64, 249);
                        }
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue());
                        BlockButton blockButton = new BlockButton(block);
                        blockButton.setBackground(color);
                        blockButton.setContentAreaFilled(false);
                        blockButton.setOpaque(true);
                        blockButton.setText(blockName.substring(0, 1));
                        blockButton.setToolTipText(blockId);
                        blockButton.setBorder(new LineBorder(Color.BLACK));
                        blockButton.setBounds(x * buttonSideLength, z * buttonSideLength, buttonSideLength, buttonSideLength);
                        Font font = blockButton.getFont();
                        blockButton.setFont(new Font(font.getFontName(), font.getStyle(), buttonSideLength));
                        blockButton.addActionListener(this::blockButtonPressed);
                        gridPanel.add(blockButton);
                        if (selected != null) {
                            int[] position = selected.getBlock().getPosition();
                            if (Arrays.equals(position, new int[]{x, currentLayer, z})) {
                                selected = blockButton;
                                // Set selected tile's border color to red
                                blockButton.setBorder(new LineBorder(Color.RED));
                            }
                        }
                    }
                }
            }
        }
    }

    private void blockButtonPressed(@NotNull ActionEvent e) {
        selected = (BlockButton) e.getSource();
        Schematic.Block block = selected.getBlock();

        if (block != null) {
            blockIdComboBox.setSelectedItem(block.getId());
            blockIdComboBox.setEnabled(true);

            blockPropertiesTextField.setValue(block.getPropertiesAsString());
            blockPropertiesTextField.setForeground(Color.BLACK);
            blockPropertiesTextField.setEnabled(true);

            try {
                blockNbtTextField.setValue(block.getSnbt());
                blockNbtTextField.setEnabled(true);
            } catch (UnsupportedOperationException e1) {
                blockNbtTextField.setValue(null);
                blockNbtTextField.setEnabled(false);
            }
            blockNbtTextField.setForeground(Color.BLACK);

            blockPositionTextField.setText(Arrays.toString(block.getPosition()));
            blockPositionTextField.setEnabled(true);

            if (block instanceof PaletteSchematic.PaletteBlock paletteBlock) {
                blockPaletteSpinner.setValue(paletteBlock.getStateIndex());
                blockPaletteSpinner.setEnabled(true);
            } else {
                blockPaletteSpinner.setValue(0);
                blockPaletteSpinner.setEnabled(false);
            }
        }
    }

    public void updateSelected() {
        if (selected != null) {
            Schematic.Block block = selected.getBlock();

            blockIdComboBox.setSelectedItem(block.getId());
            blockPropertiesTextField.setValue(block.getPropertiesAsString());

            try {
                blockNbtTextField.setValue(block.getSnbt());
                blockNbtTextField.setEnabled(true);
            } catch (UnsupportedOperationException e) {
                blockNbtTextField.setValue(null);
                blockNbtTextField.setEnabled(false);
            }
        }
    }

    private void createUIComponents() {
        rendererPanel = new GLJPanel(new GLCapabilities(GLProfile.getDefault()));
    }

    public JPanel getRootComponent() {
        return panel;
    }

    private class SchematicRenderer extends MouseAdapter implements GLEventListener, KeyListener {

        private static final float SCALE = 1.0f;
        private static final double MODEL_SIZE = 16.0;
        private static final long TICK_LENGTH = 50L;
        private static final float ROTATION_SENSITIVITY = 1.0f;
        private static final float MOTION_SENSITIVITY = 0.1f;
        private final Random random = new Random();
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
        private float cameraX;
        /**
         * Y location.
         */
        private float cameraY;
        /**
         * Z location.
         */
        private float cameraZ = -30.0f;
        private int mouseX;
        private int mouseY;

        public void drawAxes(@NotNull GL4bc gl, float sizeX, float sizeY, float sizeZ) {
            gl.glLineWidth(2.0f);

            int[] indices = { //
                    0, 1, // X axis (red)
                    2, 3, // Y axis (green)
                    4, 5 // Z axis (blue)
            };
            IntBuffer indexBuffer = GLBuffers.newDirectIntBuffer(indices);
            indexBuffer.rewind();
            // IntBuffer vertexBufferObject = GLBuffers.newDirectIntBuffer(1);
            // IntBuffer vertexArrayObject = GLBuffers.newDirectIntBuffer(1);

            float[] vertices = { //
                    0.0f, 0.0f, 0.0f, sizeX, 0.0f, 0.0f, // X-axis (red)
                    0.0f, 0.0f, 0.0f, 0.0f, sizeY, 0.0f, // Y-axis (green)
                    0.0f, 0.0f, 0.0f, 0.0f, 0.0f, sizeZ // Z-axis (blue)
            };
            FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(vertices);
            vertexBuffer.rewind();

            float[] colors = { //
                    1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // X axis (red)
                    0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Y axis (green)
                    0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f // Z axis (blue)
            };
            FloatBuffer colorBuffer = GLBuffers.newDirectFloatBuffer(colors);
            colorBuffer.rewind();

            /*gl.glGenBuffers(1, indexBuffer);

            gl.glBindBuffer(GL_ARRAY_BUFFER, indexBuffer.get(0));
            gl.glBufferData(GL_ARRAY_BUFFER, (long) vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL_STATIC_DRAW);
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

            gl.glGenVertexArrays(1, indexBuffer);
            gl.glBindVertexArray(indexBuffer.get(0));

            gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject.get(0));
            gl.glEnableVertexAttribArray(0); // Position
            gl.glEnableVertexAttribArray(1); // Color
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

            gl.glDrawArrays(GL_LINES, 0, 6);

            gl.glDisableVertexAttribArray(0); // Position
            gl.glDisableVertexAttribArray(1); // Color*/

            gl.glEnableClientState(GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL_COLOR_ARRAY);

            gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
            gl.glColorPointer(4, GL_FLOAT, 0, colorBuffer);

            gl.glDrawArrays(GL_LINES, 0, indices.length);

            gl.glDisableClientState(GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL_COLOR_ARRAY);
        }

        @Override
        public void init(@NotNull GLAutoDrawable drawable) {
            GL4bc gl = drawable.getGL().getGL4bc(); // get the OpenGL graphics context
            gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f); // set background color to gray
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

            animator = new Animator(drawable);
            animator.setRunAsFastAsPossible(true);

            animator.start();
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            animator.stop();
        }

        @Override
        public void display(@NotNull GLAutoDrawable drawable) {
            GL4bc gl = drawable.getGL().getGL4bc();
            gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity(); // reset the model-view matrix
            gl.glTranslatef(cameraX, cameraY, cameraZ); // translate into the screen
            gl.glRotatef(pitch, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
            gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
            if (schematic != null) {
                int[] size = schematic.getSize();
                // bottom-left-front corner of schematic is (0,0,0) so we need to center it at the origin
                gl.glTranslatef(-size[0] / 2.0f, -size[1] / 2.0f, -size[2] / 2.0f);
                // draw schematic border
                drawAxes(gl, size[0], size[1], size[2]);
                // draw a cube
                for (int x = 0; x < size[0]; x++) {
                    for (int y = 0; y < renderedHeight; y++) {
                        for (int z = 0; z < size[2]; z++) {
                            Schematic.Block block = schematic.getBlock(x, y, z);
                            if (block != null) {
                                long seed = x + ((long) y * size[2] * size[0]) + ((long) z * size[0]);
                                random.setSeed(seed);
                                List<JSONObject> modelList = getModelsFromBlockState(block);
                                Color tint = getTint(block);

                                gl.glPushMatrix();
                                gl.glTranslatef(x * SCALE, y * SCALE, z * SCALE);
                                for (JSONObject model : modelList) {
                                    drawModel(gl, model, tint);
                                }
                                gl.glPopMatrix();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void reshape(@NotNull GLAutoDrawable drawable, int x, int y, int width, int height) {
            GL4bc gl = drawable.getGL().getGL4bc(); // get the OpenGL graphics context
            gl.glViewport(0, 0, width, height);
            double fovY = 45.0;
            double aspect = (double) width / height;
            double zNear = 1.0;
            double zFar = 1000.0;
            double frustumHeight = Math.tan(fovY / 360 * Math.PI) * zNear;
            double frustumWidth = frustumHeight * aspect;
            // Setup perspective projection, with aspect ratio matches viewport
            gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
            gl.glLoadIdentity(); // reset projection matrix
            gl.glFrustum(-frustumWidth, frustumWidth, -frustumHeight, frustumHeight, zNear, zFar);
            // Enable the model-view transform
            gl.glMatrixMode(GL_MODELVIEW);
            gl.glLoadIdentity(); // reset
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(@NotNull KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_UP -> {
                    if (schematic != null) {
                        int[] size = schematic.getSize();
                        if (renderedHeight < size[1]) {
                            renderedHeight++;
                        } else {
                            rendererPanel.getToolkit().beep();
                        }
                        if (renderedHeight > size[1]) {
                            renderedHeight = size[1];
                        }
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (renderedHeight > 0) {
                        renderedHeight--;
                    } else {
                        rendererPanel.getToolkit().beep();
                    }
                    if (renderedHeight < 0) {
                        renderedHeight = 0;
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            rendererPanel.requestFocus();
        }

        @Override
        public void mouseWheelMoved(@NotNull MouseWheelEvent e) {
            cameraZ -= e.getPreciseWheelRotation();
        }

        @Override
        public void mouseDragged(@NotNull MouseEvent e) {
            rendererPanel.requestFocus();
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
                    cameraX += (e.getX() - mouseX) * MOTION_SENSITIVITY;
                }
                if (e.getY() < mouseY || e.getY() > mouseY) {
                    cameraY -= (e.getY() - mouseY) * MOTION_SENSITIVITY;
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

        // TODO Add a water model to this list if the block's "waterlogged" property is "true".
        @NotNull
        public List<JSONObject> getModelsFromBlockState(@NotNull Schematic.Block block) {
            List<JSONObject> modelList = new ArrayList<>();
            String namespacedId = block.getId();
            CompoundTag properties = block.getProperties().clone();
            JSONObject blockState = Assets.getBlockState(namespacedId);
            String propertiesString = "";
            try {
                propertiesString = SNBTUtil.toSNBT(PropertyUtils.byteToString(properties)).replace('{', '[').replace('}', ']').replace(':', '=').replace("\"", "");
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            if (blockState.has("variants")) {
                JSONObject variants = blockState.getJSONObject("variants");
                Set<String> keySet = variants.keySet();
                for (String variantName : keySet) {
                    String[] states = variantName.split(",");
                    boolean contains = true;
                    for (String state : states) {
                        if (!propertiesString.contains(state)) {
                            contains = false;
                            break;
                        }
                    }
                    if (contains) {
                        if (variants.has(variantName) && variants.get(variantName) instanceof JSONObject variant) {
                            modelList.add(variant);
                            return modelList;
                        } else if (variants.has(variantName) && variants.get(variantName) instanceof JSONArray variantArray) {
                            JSONObject variant = chooseRandomModel(variantArray);
                            modelList.add(variant);
                            return modelList;
                        }
                    }
                }
            } else if (blockState.has("multipart")) {
                JSONArray multipart = blockState.getJSONArray("multipart");
                for (Object partObject : multipart) {
                    JSONObject part = (JSONObject) partObject;
                    if (part.has("when")) {
                        JSONObject when = part.getJSONObject("when");
                        if (when.has("OR")) {
                            JSONArray or = when.getJSONArray("OR");
                            boolean contains = true;
                            for (Object orEntryObject : or) {
                                contains = true;
                                JSONObject orEntry = (JSONObject) orEntryObject;
                                Set<String> keySet = orEntry.keySet();
                                for (String state : keySet) {
                                    List<String> values = Arrays.asList(orEntry.getString(state).split("\\|"));
                                    if (properties.get(state) instanceof StringTag) {
                                        if (!values.contains(properties.getString(state))) {
                                            contains = false;
                                            break;
                                        }
                                    } else if (properties.get(state) instanceof IntTag) {
                                        if (!values.contains(String.valueOf(properties.getInt(state)))) {
                                            contains = false;
                                            break;
                                        }
                                    }
                                }
                                if (contains) {
                                    break;
                                }
                            }
                            if (contains) {
                                if (part.has("apply") && part.get("apply") instanceof JSONObject apply) {
                                    modelList.add(apply);
                                } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                                    JSONObject apply = chooseRandomModel(applyArray);
                                    modelList.add(apply);
                                }
                            }
                        } else {
                            Set<String> keySet = when.keySet();
                            boolean contains = true;
                            for (String state : keySet) {
                                List<String> values = Arrays.asList(when.getString(state).split("\\|"));
                                if (properties.get(state) instanceof StringTag) {
                                    if (!values.contains(properties.getString(state))) {
                                        contains = false;
                                        break;
                                    }
                                } else if (properties.get(state) instanceof IntTag) {
                                    if (!values.contains(String.valueOf(properties.getInt(state)))) {
                                        contains = false;
                                        break;
                                    }
                                }
                            }
                            if (contains) {
                                if (part.has("apply") && part.get("apply") instanceof JSONObject apply) {
                                    modelList.add(apply);
                                } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                                    JSONObject apply = chooseRandomModel(applyArray);
                                    modelList.add(apply);
                                }
                            }
                        }
                    } else {
                        if (part.has("apply") && part.get("apply") instanceof JSONObject apply) {
                            modelList.add(apply);
                        } else if (part.has("apply") && part.get("apply") instanceof JSONArray applyArray) {
                            JSONObject apply = chooseRandomModel(applyArray);
                            modelList.add(apply);
                        }
                    }
                }
            }
            return modelList;
        }

        private JSONObject chooseRandomModel(@NotNull JSONArray models) {
            int total = 0;
            NavigableMap<Integer, JSONObject> weightTree = new TreeMap<>();
            for (Object modelObject : models) {
                JSONObject model = (JSONObject) modelObject;
                int weight = model.optInt("weight", 1);
                if (weight <= 0) {
                    continue;
                }
                total += weight;
                weightTree.put(total, model);
            }
            int value = random.nextInt(0, total) + 1;
            return weightTree.ceilingEntry(value).getValue();
        }

        public void drawModel(@NotNull GL4bc gl, @NotNull JSONObject jsonObject, Color tint) {
            String modelPath = jsonObject.getString("model");
            JSONObject model = Assets.getModel(modelPath);
            int x = jsonObject.optInt("x", 0);
            int y = jsonObject.optInt("y", 0);
            boolean uvlock = jsonObject.optBoolean("uvlock", false);

            gl.glPushMatrix();

            gl.glTranslated(0.5, 0.5, 0.5);
            gl.glRotatef(-y, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(-x, 1.0f, 0.0f, 0.0f);
            gl.glTranslated(-0.5, -0.5, -0.5);

            Map<String, String> textures = getTextures(model, new HashMap<>());

            JSONArray elements = getElements(model);
            if (elements != null) {
                for (Object elementObject : elements) {
                    gl.glPushMatrix();

                    JSONObject element = (JSONObject) elementObject;
                    JSONArray from = element.getJSONArray("from");
                    JSONArray to = element.getJSONArray("to");
                    JSONObject rotation = element.optJSONObject("rotation");
                    JSONArray origin = null;
                    String axis = null;
                    float angle = 0.0f;
                    boolean rescale = false;
                    if (rotation != null) {
                        origin = rotation.getJSONArray("origin");
                        axis = rotation.getString("axis");
                        angle = rotation.optFloat("angle", 0.0f);
                        rescale = rotation.optBoolean("rescale", false);
                    }
                    boolean shade = element.optBoolean("shade", true);

                    double fromX = from.getDouble(0) / MODEL_SIZE;
                    double fromY = from.getDouble(1) / MODEL_SIZE;
                    double fromZ = from.getDouble(2) / MODEL_SIZE;
                    double toX = to.getDouble(0) / MODEL_SIZE;
                    double toY = to.getDouble(1) / MODEL_SIZE;
                    double toZ = to.getDouble(2) / MODEL_SIZE;

                    if (axis != null && origin != null) {
                        double originX = origin.getDouble(0) / MODEL_SIZE;
                        double originY = origin.getDouble(1) / MODEL_SIZE;
                        double originZ = origin.getDouble(2) / MODEL_SIZE;
                        gl.glTranslated(originX, originY, originZ);
                        double rescaleFactor = Math.hypot(MODEL_SIZE, MODEL_SIZE) / MODEL_SIZE; // TODO Do not assume that the angle is 45.0 degrees, nor that the cube is centered.
                        switch (axis) {
                            case "x" -> {
                                gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);
                                if (rescale) {
                                    gl.glScaled(1.0, rescaleFactor, rescaleFactor);
                                }
                            }
                            case "y" -> {
                                gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
                                if (rescale) {
                                    gl.glScaled(rescaleFactor, 1.0, rescaleFactor);
                                }
                            }
                            case "z" -> {
                                gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
                                if (rescale) {
                                    gl.glScaled(rescaleFactor, rescaleFactor, 1.0);
                                }
                            }
                        }
                        gl.glTranslated(-originX, -originY, -originZ);
                    }

                    if (shade) {
                        gl.glEnable(GL_LIGHTING); // enable lighting
                        gl.glEnable(GL_LIGHT1);
                    }

                    JSONObject faces = element.getJSONObject("faces");
                    Set<String> faceSet = faces.keySet();
                    for (String faceName : faceSet) {
                        JSONObject face = faces.getJSONObject(faceName);

                        JSONArray uv = face.optJSONArray("uv");
                        String faceTexture = face.has("texture") ? face.getString("texture").substring(1) : null;
                        String cullface = face.optString("cullface"); // TODO Implement culling.
                        int faceRotation = face.optInt("rotation", 0);
                        int tintIndex = face.optInt("tintindex", -1);

                        float[] components = new float[4];
                        if (tintIndex == -1) {
                            Color.WHITE.getComponents(components);
                        } else {
                            tint.getComponents(components);
                        }
                        gl.glColor4f(components[0], components[1], components[2], components[3]);

                        Texture texture = Assets.getTexture(textures.getOrDefault(faceTexture, "minecraft:missing"));

                        double textureLeft = uv != null ? uv.getDouble(0) / MODEL_SIZE : switch (faceName) {
                            case "up", "down", "north", "south" -> fromX;
                            default -> fromZ;
                        };
                        double textureTop = uv != null ? uv.getDouble(1) / MODEL_SIZE : switch (faceName) {
                            case "up" -> fromZ;
                            case "down" -> SCALE - toZ;
                            default -> SCALE - toY;
                        };
                        double textureRight = uv != null ? uv.getDouble(2) / MODEL_SIZE : switch (faceName) {
                            case "up", "down", "north", "south" -> toX;
                            default -> toZ;
                        };
                        double textureBottom = uv != null ? uv.getDouble(3) / MODEL_SIZE : switch (faceName) {
                            case "up" -> toZ;
                            case "down" -> SCALE - fromZ;
                            default -> SCALE - fromY;
                        };

                        JSONObject fullAnimation = Assets.getAnimation(textures.getOrDefault(faceTexture, "minecraft:missing"));
                        if (fullAnimation != null) {
                            JSONObject animation = fullAnimation.getJSONObject("animation");
                            boolean interpolate = animation.optBoolean("interpolate", false); // TODO Implement interpolation.
                            int width = animation.optInt("width", texture.getWidth());
                            int height = animation.optInt("height", texture.getWidth());
                            int frametime = animation.optInt("frametime", 1);

                            int widthFactor = Math.abs(texture.getWidth() / width);
                            int heightFactor = Math.abs(texture.getHeight() / height);

                            JSONArray frames;
                            if (animation.has("frames")) {
                                frames = animation.getJSONArray("frames");
                            } else {
                                frames = new JSONArray();
                                for (int i = 0; i < heightFactor; i++) {
                                    frames.put(i, i);
                                }
                            }

                            // Set all texture coordinates to the first frame
                            textureLeft /= widthFactor;
                            textureTop /= heightFactor;
                            textureRight /= widthFactor;
                            textureBottom /= heightFactor;

                            long currentTick = System.currentTimeMillis() / (TICK_LENGTH * frametime);
                            long index = (currentTick % (frames.length()));
                            Object frame = frames.get((int) index);
                            double frameDouble = 0.0;
                            if (frame instanceof Integer frameInt) {
                                frameDouble = (double) frameInt;
                            } else if (frame instanceof JSONObject frameObject) {
                                frameDouble = frameObject.getInt("index");
                                // TODO Implement the "time" tag.
                                int time = frameObject.optInt("time", frametime);
                            }

                            // Change to the current frame in the animation
                            textureTop += frameDouble / heightFactor;
                            textureBottom += frameDouble / heightFactor;
                        } else if (texture.getWidth() != texture.getHeight()) {
                            texture = Assets.getTexture("minecraft:missing");
                        }

                        for (int i = 0; i < faceRotation; i += 90) {
                            double temp = textureLeft;
                            textureLeft = SCALE - textureBottom;
                            textureBottom = textureRight;
                            textureRight = SCALE - textureTop;
                            textureTop = temp;
                        }

                        gl.glMatrixMode(GL_TEXTURE);
                        gl.glLoadIdentity();
                        gl.glTranslated(0.5, 0.5, 0.0);
                        gl.glRotated(faceRotation, 0.0, 0.0, 1.0);
                        if (uvlock) {
                            switch (faceName) {
                                case "up" -> {
                                    if (x == 180) {
                                        gl.glRotated(y, 0.0, 0.0, 1.0);
                                    } else {
                                        gl.glRotated(-y, 0.0, 0.0, 1.0);
                                    }
                                }
                                case "down" -> {
                                    if (x == 180) {
                                        gl.glRotated(-y, 0.0, 0.0, 1.0);
                                    } else {
                                        gl.glRotated(y, 0.0, 0.0, 1.0);
                                    }
                                }
                                default -> gl.glRotated(-x, 0.0, 0.0, 1.0);
                            }
                        }
                        gl.glScaled(1.0, -1.0, 1.0);
                        gl.glTranslated(-0.5, -0.5, 0.0);
                        gl.glMatrixMode(GL_MODELVIEW);

                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                        texture.enable(gl);
                        texture.bind(gl);

                        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                        gl.glEnable(GL_BLEND);

                        gl.glAlphaFunc(GL_GREATER, 0.0f);
                        gl.glEnable(GL_ALPHA_TEST);

                        /*int[] indices = { //
                                0, 1, // Bottom
                                1, 2, // Right
                                2, 3, // Top
                                3, 4 // Left
                        };
                        IntBuffer indexBuffer = GLBuffers.newDirectIntBuffer(indices);
                        indexBuffer.rewind();

                        double[] vertices = { //
                                fromX, fromY, toZ, // Bottom-left
                                toX, fromY, toZ, // Bottom-right
                                toX, toY, toZ, // Top-right
                                fromX, toY, toZ // Top-left
                        };
                        DoubleBuffer vertexBuffer = GLBuffers.newDirectDoubleBuffer(vertices);
                        vertexBuffer.rewind();

                        float[] colors = { //
                                components[0], components[1], components[2], components[3], //
                                components[0], components[1], components[2], components[3], //
                                components[0], components[1], components[2], components[3], //
                                components[0], components[1], components[2], components[3] //
                        };
                        FloatBuffer colorBuffer = GLBuffers.newDirectFloatBuffer(colors);
                        colorBuffer.rewind();

                        double[] textureCoords = { //
                                textureLeft, textureBottom, // Bottom-left
                                textureRight, textureBottom, // Bottom-right
                                textureRight, textureTop, // Top-right
                                textureLeft, textureTop // Top-left
                        };
                        DoubleBuffer textureBuffer = GLBuffers.newDirectDoubleBuffer(textureCoords);
                        textureBuffer.rewind();

                        gl.glGenBuffers(1, indexBuffer);

                        gl.glBindBuffer(GL_ARRAY_BUFFER, indexBuffer.get(0));
                        gl.glBufferData(GL_ARRAY_BUFFER, (long) vertexBuffer.capacity() * Double.BYTES, vertexBuffer, GL_STATIC_DRAW);
                        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

                        gl.glEnableClientState(GL_VERTEX_ARRAY);
                        gl.glEnableClientState(GL_COLOR_ARRAY);
                        // gl.glEnableClientState(GL_NORMAL_ARRAY);
                        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

                        gl.glVertexPointer(3, GL_DOUBLE, 0, vertexBuffer);
                        gl.glColorPointer(4, GL_FLOAT, 0, colorBuffer);
                        // gl.glNormalPointer(GL_FLOAT, 0, 0);
                        gl.glTexCoordPointer(2, GL_DOUBLE, 0, textureBuffer);

                        gl.glDrawArrays(GL_QUADS, 0, indices.length);

                        gl.glDisableClientState(GL_VERTEX_ARRAY);
                        gl.glDisableClientState(GL_COLOR_ARRAY);
                        // gl.glDisableClientState(GL_NORMAL_ARRAY);
                        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);*/

                        gl.glBegin(GL_QUADS);
                        switch (faceName) {
                            case "up" -> {
                                gl.glNormal3d(0.0f, 1.0f, 0.0f);
                                gl.glTexCoord2d(textureLeft, textureBottom);
                                gl.glVertex3d(fromX, toY, toZ);
                                gl.glTexCoord2d(textureRight, textureBottom);
                                gl.glVertex3d(toX, toY, toZ);
                                gl.glTexCoord2d(textureRight, textureTop);
                                gl.glVertex3d(toX, toY, fromZ);
                                gl.glTexCoord2d(textureLeft, textureTop);
                                gl.glVertex3d(fromX, toY, fromZ);
                            }
                            case "down" -> {
                                gl.glNormal3d(0.0f, -1.0f, 0.0f);
                                gl.glTexCoord2d(textureLeft, textureBottom);
                                gl.glVertex3d(fromX, fromY, fromZ);
                                gl.glTexCoord2d(textureRight, textureBottom);
                                gl.glVertex3d(toX, fromY, fromZ);
                                gl.glTexCoord2d(textureRight, textureTop);
                                gl.glVertex3d(toX, fromY, toZ);
                                gl.glTexCoord2d(textureLeft, textureTop);
                                gl.glVertex3d(fromX, fromY, toZ);
                            }
                            case "north" -> {
                                gl.glNormal3d(0.0f, 0.0f, -1.0f);
                                gl.glTexCoord2d(textureLeft, textureBottom);
                                gl.glVertex3d(toX, fromY, fromZ);
                                gl.glTexCoord2d(textureRight, textureBottom);
                                gl.glVertex3d(fromX, fromY, fromZ);
                                gl.glTexCoord2d(textureRight, textureTop);
                                gl.glVertex3d(fromX, toY, fromZ);
                                gl.glTexCoord2d(textureLeft, textureTop);
                                gl.glVertex3d(toX, toY, fromZ);
                            }
                            case "south" -> {
                                gl.glNormal3d(0.0f, 0.0f, 1.0f);
                                gl.glTexCoord2d(textureLeft, textureBottom);
                                gl.glVertex3d(fromX, fromY, toZ); // bottom-left of the quad
                                gl.glTexCoord2d(textureRight, textureBottom);
                                gl.glVertex3d(toX, fromY, toZ); // bottom-right of the quad
                                gl.glTexCoord2d(textureRight, textureTop);
                                gl.glVertex3d(toX, toY, toZ); // top-right of the quad
                                gl.glTexCoord2d(textureLeft, textureTop);
                                gl.glVertex3d(fromX, toY, toZ); // top-left of the quad

                            }
                            case "west" -> {
                                gl.glNormal3d(-1.0f, 0.0f, 0.0f);
                                gl.glTexCoord2d(textureLeft, textureBottom);
                                gl.glVertex3d(fromX, fromY, fromZ);
                                gl.glTexCoord2d(textureRight, textureBottom);
                                gl.glVertex3d(fromX, fromY, toZ);
                                gl.glTexCoord2d(textureRight, textureTop);
                                gl.glVertex3d(fromX, toY, toZ);
                                gl.glTexCoord2d(textureLeft, textureTop);
                                gl.glVertex3d(fromX, toY, fromZ);
                            }
                            case "east" -> {
                                gl.glNormal3d(1.0f, 0.0f, 0.0f);
                                gl.glTexCoord2d(textureLeft, textureBottom);
                                gl.glVertex3d(toX, fromY, toZ);
                                gl.glTexCoord2d(textureRight, textureBottom);
                                gl.glVertex3d(toX, fromY, fromZ);
                                gl.glTexCoord2d(textureRight, textureTop);
                                gl.glVertex3d(toX, toY, fromZ);
                                gl.glTexCoord2d(textureLeft, textureTop);
                                gl.glVertex3d(toX, toY, toZ);
                            }
                        }
                        gl.glEnd();
                        texture.disable(gl);
                        gl.glDisable(GL_ALPHA_TEST);
                        gl.glDisable(GL_BLEND);
                    }
                    gl.glPopMatrix();
                    gl.glDisable(GL_LIGHTING); // enable lighting
                    gl.glDisable(GL_LIGHT1);
                }
            }
            gl.glPopMatrix();
        }

        @Nullable
        private JSONArray getElements(@NotNull JSONObject model) {
            if (model.has("elements")) {
                return model.getJSONArray("elements");
            } else if (model.has("parent")) {
                return getElements(Assets.getModel(model.getString("parent")));
            } else {
                return null;
            }
        }

        private Map<String, String> getTextures(@NotNull JSONObject model, Map<String, String> textures) {
            if (model.has("textures")) {
                JSONObject json = model.getJSONObject("textures");
                Set<String> names = json.keySet();
                for (String name : names) {
                    getTextureFromId(model, textures, name);
                }
            }
            if (model.has("parent")) {
                getTextures(Assets.getModel(model.getString("parent")), textures);
            }
            return textures;
        }

        private void getTextureFromId(@NotNull JSONObject model, Map<String, String> textures, String name) {
            JSONObject parent = null;
            if (model.has("parent")) {
                parent = Assets.getModel(model.getString("parent"));
            }
            if (model.has("textures")) {
                JSONObject texturesJson = model.getJSONObject("textures");
                if (texturesJson.has(name)) {
                    String path = texturesJson.getString(name);
                    if (path.startsWith("#")) {
                        String substring = path.substring(1);
                        if (texturesJson.has(substring)) {
                            getTextureFromId(model, textures, substring);
                        } else if (textures.containsKey(substring)) {
                            textures.put(name, textures.get(substring));
                        } else if (parent != null) {
                            getTextureFromId(parent, textures, substring);
                        } else {
                            textures.put(substring, "minecraft:missing");
                        }
                    } else if (!textures.containsKey(name) || textures.get(name).equals("minecraft:missing")) {
                        textures.put(name, path);
                    }
                }
            }
            if (parent != null) {
                getTextureFromId(parent, textures, name);
            }
        }

        @NotNull
        public Color getTint(@NotNull Schematic.Block block) {
            String namespacedId = block.getId();
            CompoundTag properties = block.getProperties();
            switch (namespacedId) {
                case "minecraft:redstone_wire" -> {
                    int power = 0;
                    if (properties.containsKey("power") && properties.get("power") instanceof IntTag intTag) {
                        power = intTag.asInt();
                    } else if (properties.containsKey("power") && properties.get("power") instanceof StringTag stringTag) {
                        try {
                            power = Integer.parseInt(stringTag.getValue());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    switch (power) {
                        case 1 -> {
                            return Color.decode("#6F0000");
                        }
                        case 2 -> {
                            return Color.decode("#790000");
                        }
                        case 3 -> {
                            return Color.decode("#820000");
                        }
                        case 4 -> {
                            return Color.decode("#8C0000");
                        }
                        case 5 -> {
                            return Color.decode("#970000");
                        }
                        case 6 -> {
                            return Color.decode("#A10000");
                        }
                        case 7 -> {
                            return Color.decode("#AB0000");
                        }
                        case 8 -> {
                            return Color.decode("#B50000");
                        }
                        case 9 -> {
                            return Color.decode("#BF0000");
                        }
                        case 10 -> {
                            return Color.decode("#CA0000");
                        }
                        case 11 -> {
                            return Color.decode("#D30000");
                        }
                        case 12 -> {
                            return Color.decode("#DD0000");
                        }
                        case 13 -> {
                            return Color.decode("#E70600");
                        }
                        case 14 -> {
                            return Color.decode("#F11B00");
                        }
                        case 15 -> {
                            return Color.decode("#FC3100");
                        }
                        default -> { // 0
                            return Color.decode("#4B0000");
                        }
                    }
                }
                case "minecraft:grass_block", "minecraft:grass", "minecraft:tall_grass", "minecraft:fern", "minecraft:large_fern", "minecraft:potted_fern", "minecraft:sugar_cane" -> {
                    return Color.decode("#91BD59");
                }
                case "minecraft:oak_leaves", "minecraft:dark_oak_leaves", "minecraft:jungle_leaves", "minecraft:acacia_leaves", "minecraft:vine" -> {
                    return Color.decode("#77AB2F");
                }
                case "minecraft:water", "minecraft:water_cauldron" -> {
                    return Color.decode("#3F76E4");
                }
                case "minecraft:birch_leaves" -> {
                    return Color.decode("#80A755");
                }
                case "minecraft:spruce_leaves" -> {
                    return Color.decode("#619961");
                }
                case "minecraft:lily_pad" -> {
                    return Color.decode("#208030");
                }
                default -> {
                    return Color.WHITE;
                }
            }
        }
    }
}
