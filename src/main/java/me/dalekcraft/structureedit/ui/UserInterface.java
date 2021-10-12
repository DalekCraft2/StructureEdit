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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import me.dalekcraft.structureedit.Main;
import me.dalekcraft.structureedit.drawing.BlockColor;
import me.dalekcraft.structureedit.drawing.ModelRenderer;
import me.dalekcraft.structureedit.schematic.Block;
import me.dalekcraft.structureedit.schematic.NbtStructure;
import me.dalekcraft.structureedit.schematic.Schematic;
import me.dalekcraft.structureedit.schematic.TardisSchematic;
import me.dalekcraft.structureedit.util.Assets;
import me.dalekcraft.structureedit.util.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
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
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2.GL_CURRENT_BIT;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.*;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import static me.dalekcraft.structureedit.schematic.Schematic.*;

/**
 * @author eccentric_nz
 */
public class UserInterface {

    public static final float SCALE = 1.0f;
    private static final Logger LOGGER = LogManager.getLogger(UserInterface.class);
    private static final FileNameExtensionFilter FILTER_NBT = new FileNameExtensionFilter(Configuration.LANGUAGE.getProperty("ui.file_chooser.extension.nbt"), EXTENSION_NBT);
    private static final FileNameExtensionFilter FILTER_MCEDIT = new FileNameExtensionFilter(Configuration.LANGUAGE.getProperty("ui.file_chooser.extension.mcedit"), EXTENSION_MCEDIT);
    private static final FileNameExtensionFilter FILTER_SPONGE = new FileNameExtensionFilter(Configuration.LANGUAGE.getProperty("ui.file_chooser.extension.sponge"), EXTENSION_SPONGE);
    private static final FileNameExtensionFilter FILTER_TARDIS = new FileNameExtensionFilter(Configuration.LANGUAGE.getProperty("ui.file_chooser.extension.tardis"), EXTENSION_TARDIS);
    private static final float ROTATION_SENSITIVITY = 1.0f;
    private static final float MOTION_SENSITIVITY = 0.1f;
    public final JFileChooser schematicChooser = new JFileChooser();
    public final JFileChooser assetsChooser = new JFileChooser();
    public JComboBox<String> blockIdComboBox;
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
    private int renderedHeight;
    private Animator animator;
    private BlockButton selected;
    private Schematic schematic;
    private JPanel panel;
    private JPanel rendererPanel;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu settingsMenu;
    private JMenu helpMenu;
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

    {
        schematicChooser.addChoosableFileFilter(FILTER_NBT);
        schematicChooser.addChoosableFileFilter(FILTER_MCEDIT);
        schematicChooser.addChoosableFileFilter(FILTER_SPONGE);
        schematicChooser.addChoosableFileFilter(FILTER_TARDIS);
        assetsChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public UserInterface() {
        $$$setupUI$$$();
        new AutoCompletion(blockIdComboBox);
        ((GLJPanel) rendererPanel).addGLEventListener(new GLEventListener() {
            public static void drawAxes(@NotNull GL4bc gl, float sizeX, float sizeY, float sizeZ) {
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
            public void display(GLAutoDrawable drawable) {
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
                                Block block = schematic.getBlock(x, y, z);
                                if (block != null) {
                                    long seed = x + ((long) y * size[2] * size[0]) + ((long) z * size[0]);
                                    Random random = new Random(seed);
                                    List<JSONObject> modelList = ModelRenderer.getModelsFromBlockState(block, random);
                                    Color tint = ModelRenderer.getTint(block);

                                    gl.glPushMatrix();
                                    gl.glTranslatef(x * SCALE, y * SCALE, z * SCALE);
                                    for (JSONObject model : modelList) {
                                        ModelRenderer.drawModel(gl, model, tint);
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
        rendererPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@NotNull KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> cameraZ++;
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> cameraZ--;
                    case KeyEvent.VK_A -> cameraX++;
                    case KeyEvent.VK_D -> cameraX--;
                    case KeyEvent.VK_SHIFT -> cameraY++;
                    case KeyEvent.VK_SPACE -> cameraY--;
                    case KeyEvent.VK_LEFT -> {
                        if (renderedHeight > 0) {
                            renderedHeight--;
                        }
                        if (renderedHeight < 0) {
                            renderedHeight = 0;
                        }
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (schematic != null) {
                            int[] size = schematic.getSize();
                            if (renderedHeight < size[1]) {
                                renderedHeight++;
                            }
                            if (renderedHeight > size[1]) {
                                renderedHeight = size[1];
                            }
                        }
                    }
                }
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
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
        };

        rendererPanel.addMouseListener(mouseAdapter);
        rendererPanel.addMouseMotionListener(mouseAdapter);
        rendererPanel.addMouseWheelListener(mouseAdapter);

        gridPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (schematic != null) {
                    loadLayer();
                }
            }
        });

        JPopupMenu filePopup = fileMenu.getPopupMenu();
        JMenuItem openButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.file_menu.open"));
        openButton.addActionListener(e -> {
            animator.pause();
            int result = schematicChooser.showOpenDialog(Main.frame);
            if (result == JFileChooser.APPROVE_OPTION && schematicChooser.getSelectedFile() != null) {
                File file;
                try {
                    file = schematicChooser.getSelectedFile().getCanonicalFile();
                    open(file);
                } catch (IOException e1) {
                    LOGGER.log(Level.ERROR, e1.getMessage());
                }
            }
            animator.resume();
        });
        filePopup.add(openButton);
        JMenuItem saveButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.file_menu.save"));
        saveButton.addActionListener(e -> {
            if (schematic != null) {
                animator.pause();
                schematicChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = schematicChooser.showSaveDialog(Main.frame);
                if (result == JFileChooser.APPROVE_OPTION && schematicChooser.getSelectedFile() != null) {
                    try {
                        File file = schematicChooser.getSelectedFile().getCanonicalFile();
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
        });
        filePopup.add(saveButton);

        JPopupMenu settingsPopup = settingsMenu.getPopupMenu();
        JMenuItem assetsPathButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.assets_path"));
        assetsPathButton.addActionListener(e -> {
            animator.pause();
            assetsChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = assetsChooser.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION && assetsChooser.getSelectedFile() != null) {
                Path assets;
                try {
                    assets = assetsChooser.getSelectedFile().toPath().toRealPath();
                    LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.assets.setting"), assets);
                    Assets.setAssets(assets);
                    blockIdComboBox.removeAllItems();
                    for (String blockId : Assets.getBlockStateArray()) {
                        blockIdComboBox.addItem(blockId);
                    }
                    blockIdComboBox.setSelectedItem(null);
                } catch (IOException e1) {
                    LOGGER.log(Level.ERROR, e1.getMessage());
                }
            }
            updateSelected();
            animator.resume();
        });
        settingsPopup.add(assetsPathButton);
        JMenuItem logLevelButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.log_level"));
        logLevelButton.addActionListener(e -> {
            Level level = (Level) JOptionPane.showInputDialog(Main.frame, Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.log_level.label"), Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.log_level.title"), JOptionPane.PLAIN_MESSAGE, null, Level.values(), LogManager.getRootLogger().getLevel());
            if (level != null) {
                LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.log_level.setting"), level);
                Configurator.setAllLevels(LogManager.ROOT_LOGGER_NAME, level);
            }
        });
        settingsPopup.add(logLevelButton);

        JPopupMenu helpPopup = helpMenu.getPopupMenu();
        JMenuItem controlsButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.help_menu.controls"));
        controlsButton.addActionListener(e -> {
            animator.pause();
            JOptionPane.showMessageDialog(Main.frame, Configuration.LANGUAGE.getProperty("ui.menu_bar.help_menu.controls.dialog"), Configuration.LANGUAGE.getProperty("ui.menu_bar.help_menu.controls.title"), JOptionPane.INFORMATION_MESSAGE);
            animator.resume();
        });
        helpPopup.add(controlsButton);

        layerSpinner.addChangeListener(e -> {
            if (schematic != null) {
                loadLayer();
            }
        });

        blockIdComboBox.removeAllItems();
        for (String blockId : Assets.getBlockStateArray()) {
            blockIdComboBox.addItem(blockId);
        }
        blockIdComboBox.setSelectedItem(null);
        blockIdComboBox.addItemListener(e -> {
            if (schematic != null && selected != null && blockIdComboBox.getSelectedItem() != null) {
                Block block = selected.getBlock();
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
                    Block block = selected.getBlock();
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
                    Block block = selected.getBlock();
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
                if (schematic instanceof NbtStructure nbtStructure && nbtStructure.hasPaletteList()) {
                    nbtStructure.setActivePalette((Integer) paletteSpinner.getValue());
                }
            }
            loadLayer();
            updateSelected();
        });
        // TODO Blockbench-style palette editor, with a list of palettes and palette IDs?
        blockPaletteSpinner.addChangeListener(e -> {
            if (schematic != null && !(schematic instanceof TardisSchematic) && selected != null) {
                Block block = selected.getBlock();
                if (block != null) {
                    block.setState((Integer) blockPaletteSpinner.getValue());
                }
                loadLayer();
                updateSelected();
            }
        });
    }

    public void open(@NotNull File file) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.schematic.loading"), file);
            try {
                schematic = openFrom(file);
                selected = null;
                sizeTextField.setText(null);
                paletteSpinner.setValue(0);
                paletteSpinner.setEnabled(false);
                blockPositionTextField.setText(null);
                blockPositionTextField.setEnabled(false);
                blockIdComboBox.setSelectedItem(null);
                blockIdComboBox.setEnabled(false);
                blockPropertiesTextField.setText(null);
                blockPropertiesTextField.setEnabled(false);
                blockNbtTextField.setText(null);
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
                    if (!(schematic instanceof TardisSchematic)) {
                        if (schematic instanceof NbtStructure nbtStructure && nbtStructure.hasPaletteList()) {
                            int palettesSize = nbtStructure.getPaletteList().size();
                            SpinnerModel paletteModel = new SpinnerNumberModel(0, 0, palettesSize - 1, 1);
                            paletteSpinner.setEnabled(true);
                            paletteSpinner.setModel(paletteModel);
                            nbtStructure.setActivePalette(0);
                        }
                        int paletteSize = schematic.getPalette().size();
                        SpinnerModel blockPaletteModel = new SpinnerNumberModel(0, 0, paletteSize - 1, 1);
                        blockPaletteSpinner.setModel(blockPaletteModel);
                    } else {
                        paletteSpinner.setEnabled(false);
                    }
                    loadLayer();
                    LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.schematic.loaded"), file);
                    Main.frame.setTitle(String.format(Configuration.LANGUAGE.getProperty("ui.window.title_with_file"), file.getName()));
                } else {
                    LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.not_schematic"));
                }
            } catch (IOException | JSONException e) {
                LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.error_reading"), e.getMessage());
                Main.frame.setTitle(Configuration.LANGUAGE.getProperty("ui.window.title"));
            }
        });
    }

    // TODO Make the editor built into the 3D view instead of being a layer-by-layer editor.
    public void loadLayer() {
        if (schematic != null) {
            gridPanel.removeAll();
            gridPanel.setLayout(null);
            gridPanel.updateUI();
            int[] size = schematic.getSize();
            sizeTextField.setText(Arrays.toString(size));
            int currentLayer = (int) layerSpinner.getValue();
            int buttonSideLength = Math.min(gridPanel.getWidth() / size[0], gridPanel.getHeight() / size[2]);
            for (int x = 0; x < size[0]; x++) {
                for (int z = 0; z < size[2]; z++) {
                    Block block = schematic.getBlock(x, currentLayer, z);
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
        } else {
            LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.null"));
        }
    }

    private void blockButtonPressed(@NotNull ActionEvent e) {
        selected = (BlockButton) e.getSource();
        Block block = selected.getBlock();

        blockIdComboBox.setSelectedItem(block.getId());
        blockIdComboBox.setEnabled(true);

        blockPropertiesTextField.setText(block.getPropertiesAsString());
        blockPropertiesTextField.setForeground(Color.BLACK);
        blockPropertiesTextField.setEnabled(true);

        try {
            blockNbtTextField.setText(block.getSnbt());
            blockNbtTextField.setEnabled(true);
        } catch (UnsupportedOperationException e1) {
            blockNbtTextField.setText(null);
            blockNbtTextField.setEnabled(false);
        }
        blockNbtTextField.setForeground(Color.BLACK);

        blockPositionTextField.setText(Arrays.toString(block.getPosition()));
        blockPositionTextField.setEnabled(true);

        if (!(schematic instanceof TardisSchematic)) {
            blockPaletteSpinner.setValue(block.getState());
            blockPaletteSpinner.setEnabled(true);
        } else {
            blockPaletteSpinner.setEnabled(false);
        }
    }

    public void updateSelected() {
        if (selected != null) {
            Block block = selected.getBlock();

            blockIdComboBox.setSelectedItem(block.getId());
            blockPropertiesTextField.setText(block.getPropertiesAsString());

            try {
                blockNbtTextField.setText(block.getSnbt());
            } catch (UnsupportedOperationException e) {
                blockNbtTextField.setText(null);
            }
        }
    }

    private void createUIComponents() {
        rendererPanel = new GLJPanel(new GLCapabilities(GLProfile.getDefault()));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.setPreferredSize(new Dimension(1024, 600));
        editorPanel = new JPanel();
        editorPanel.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(editorPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        blockIdLabel = new JLabel();
        this.$$$loadLabelText$$$(blockIdLabel, this.$$$getMessageFromBundle$$$("language", "ui.editor.block_id.text"));
        editorPanel.add(blockIdLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockIdComboBox = new JComboBox();
        blockIdComboBox.setEditable(true);
        blockIdComboBox.setEnabled(false);
        blockIdComboBox.setToolTipText(this.$$$getMessageFromBundle$$$("language", "ui.editor.block_id.tooltip"));
        editorPanel.add(blockIdComboBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPropertiesLabel = new JLabel();
        this.$$$loadLabelText$$$(blockPropertiesLabel, this.$$$getMessageFromBundle$$$("language", "ui.editor.block_properties.text"));
        editorPanel.add(blockPropertiesLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPropertiesTextField = new JFormattedTextField();
        blockPropertiesTextField.setEnabled(false);
        blockPropertiesTextField.setToolTipText(this.$$$getMessageFromBundle$$$("language", "ui.editor.block_properties.tooltip"));
        editorPanel.add(blockPropertiesTextField, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        blockPositionLabel = new JLabel();
        this.$$$loadLabelText$$$(blockPositionLabel, this.$$$getMessageFromBundle$$$("language", "ui.editor.block_position.text"));
        editorPanel.add(blockPositionLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPositionTextField = new JTextField();
        blockPositionTextField.setEditable(false);
        blockPositionTextField.setEnabled(false);
        blockPositionTextField.setText("");
        blockPositionTextField.setToolTipText(this.$$$getMessageFromBundle$$$("language", "ui.editor.block_position.tooltip"));
        editorPanel.add(blockPositionTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        layerLabel = new JLabel();
        this.$$$loadLabelText$$$(layerLabel, this.$$$getMessageFromBundle$$$("language", "ui.editor.layer.text"));
        editorPanel.add(layerLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockNbtTextField = new JFormattedTextField();
        blockNbtTextField.setEnabled(false);
        blockNbtTextField.setToolTipText(this.$$$getMessageFromBundle$$$("language", "ui.editor.block_nbt.tooltip"));
        editorPanel.add(blockNbtTextField, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        paletteLabel = new JLabel();
        this.$$$loadLabelText$$$(paletteLabel, this.$$$getMessageFromBundle$$$("language", "ui.editor.palette.text"));
        editorPanel.add(paletteLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paletteSpinner = new JSpinner();
        paletteSpinner.setEnabled(false);
        paletteSpinner.setToolTipText(this.$$$getMessageFromBundle$$$("language", "ui.editor.palette.tooltip"));
        editorPanel.add(paletteSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockNbtLabel = new JLabel();
        this.$$$loadLabelText$$$(blockNbtLabel, this.$$$getMessageFromBundle$$$("language", "ui.editor.block_nbt.text"));
        editorPanel.add(blockNbtLabel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel.add(gridPanel, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sizeLabel = new JLabel();
        this.$$$loadLabelText$$$(sizeLabel, this.$$$getMessageFromBundle$$$("language", "ui.editor.schematic_size.text"));
        editorPanel.add(sizeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sizeTextField = new JTextField();
        sizeTextField.setEditable(false);
        sizeTextField.setEnabled(false);
        sizeTextField.setToolTipText(this.$$$getMessageFromBundle$$$("language", "ui.editor.schematic_size.tooltip"));
        editorPanel.add(sizeTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        blockPaletteLabel = new JLabel();
        this.$$$loadLabelText$$$(blockPaletteLabel, this.$$$getMessageFromBundle$$$("language", "ui.editor.block_palette.text"));
        editorPanel.add(blockPaletteLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPaletteSpinner = new JSpinner();
        blockPaletteSpinner.setEnabled(false);
        blockPaletteSpinner.setToolTipText(this.$$$getMessageFromBundle$$$("language", "ui.editor.block_palette.tooltip"));
        editorPanel.add(blockPaletteSpinner, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        layerSpinner = new JSpinner();
        layerSpinner.setEnabled(false);
        layerSpinner.setToolTipText(this.$$$getMessageFromBundle$$$("language", "ui.editor.layer.tooltip"));
        editorPanel.add(layerSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(rendererPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        menuBar = new JMenuBar();
        menuBar.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(menuBar, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fileMenu = new JMenu();
        this.$$$loadButtonText$$$(fileMenu, this.$$$getMessageFromBundle$$$("language", "ui.menu_bar.file_menu.text"));
        menuBar.add(fileMenu, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsMenu = new JMenu();
        this.$$$loadButtonText$$$(settingsMenu, this.$$$getMessageFromBundle$$$("language", "ui.menu_bar.settings_menu.text"));
        menuBar.add(settingsMenu, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        helpMenu = new JMenu();
        helpMenu.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        this.$$$loadButtonText$$$(helpMenu, this.$$$getMessageFromBundle$$$("language", "ui.menu_bar.help_menu.text"));
        menuBar.add(helpMenu, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        blockIdLabel.setLabelFor(blockIdComboBox);
        blockPropertiesLabel.setLabelFor(blockPropertiesTextField);
        blockPositionLabel.setLabelFor(blockPositionTextField);
        layerLabel.setLabelFor(layerSpinner);
        paletteLabel.setLabelFor(paletteSpinner);
        blockNbtLabel.setLabelFor(blockNbtTextField);
        sizeLabel.setLabelFor(sizeTextField);
        blockPaletteLabel.setLabelFor(blockPaletteSpinner);
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) {
                    break;
                }
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) {
                    break;
                }
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
