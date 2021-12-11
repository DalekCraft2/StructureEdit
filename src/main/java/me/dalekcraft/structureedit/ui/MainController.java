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

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import me.dalekcraft.structureedit.StructureEditApplication;
import me.dalekcraft.structureedit.drawing.FoliageColor;
import me.dalekcraft.structureedit.drawing.GrassColor;
import me.dalekcraft.structureedit.drawing.WaterColor;
import me.dalekcraft.structureedit.schematic.container.*;
import me.dalekcraft.structureedit.schematic.io.*;
import me.dalekcraft.structureedit.util.Assets;
import me.dalekcraft.structureedit.util.Configuration;
import me.dalekcraft.structureedit.util.InternalUtils;
import me.dalekcraft.structureedit.util.Semantic;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.fxmisc.richtext.InlineCssTextArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

import static com.jogamp.opengl.GL4.*;

/**
 * @author eccentric_nz
 */
public class MainController extends Node {
    /**
     * Color of the missing texture's purple.
     */
    public static final Color MISSING_COLOR = Color.rgb(251, 64, 249);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Color DEFAULT_TINT = Color.WHITE;
    private static final FileChooser.ExtensionFilter FILTER_ALL = new FileChooser.ExtensionFilter(Configuration.LANGUAGE.getString("ui.file_chooser.extension.all"), "*.*");
    public final FileChooser schematicChooser = new FileChooser();
    public final DirectoryChooser assetsChooser = new DirectoryChooser();
    private int renderedHeight;
    private Schematic schematic;
    private boolean shouldReloadTextures = true;
    private SchematicRenderer renderer;
    private GLJPanel rendererPanel;
    @FXML
    private SwingNode rendererNode;
    @FXML
    private SchematicInfoController schematicInfoController;
    @FXML
    private BlockEditorController blockEditorController;
    @FXML
    private BlockStateEditorController blockStateEditorController;
    @FXML
    private EntityEditorController entityEditorController;
    @FXML
    private BiomeEditorController biomeEditorController;
    @FXML
    private BiomeStateEditorController biomeStateEditorController;
    @FXML
    private InlineCssTextArea logArea;

    {
        schematicChooser.getExtensionFilters().addAll(SchematicFormats.getFileExtensionFilterMap().keySet());
        schematicChooser.getExtensionFilters().sort(Comparator.comparing(FileChooser.ExtensionFilter::getDescription));
        schematicChooser.getExtensionFilters().add(0, FILTER_ALL);
        Path assets = Assets.getPath();
        if (assets != null && !assets.toString().equals("")) {
            assetsChooser.setInitialDirectory(assets.toFile());
        }
    }

    @FXML
    public void initialize() {
        // FIXME The GLEventListener only initializes when the window is resized or moved.
        SwingUtilities.invokeLater(() -> {
            rendererPanel = new GLJPanel();
            renderer = new SchematicRenderer();
            rendererPanel.addGLEventListener(renderer);
            rendererPanel.addKeyListener(renderer);
            rendererPanel.addMouseListener(renderer);
            rendererPanel.addMouseMotionListener(renderer);
            rendererPanel.addMouseWheelListener(renderer);
            /*rendererNode.addEventHandler(KeyEvent.KEY_PRESSED, event -> renderer.keyPressed(event));
            rendererNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> renderer.mouseDragged(event));
            rendererNode.addEventHandler(MouseEvent.MOUSE_MOVED, event -> renderer.mouseMoved(event));
            rendererNode.addEventHandler(ScrollEvent.ANY, event -> renderer.mouseWheelMoved(event));*/
            rendererNode.setContent(rendererPanel);
        });

        blockEditorController.injectBlockStateEditorController(blockStateEditorController);
        blockStateEditorController.injectBlockEditorController(blockEditorController);

        biomeStateEditorController.injectBiomeEditorController(biomeEditorController);

        InlineCssTextAreaAppender.addLog4j2TextAreaAppender(logArea);
    }

    @FXML
    public void showOpenDialog() {
        renderer.animator.pause();
        File file = schematicChooser.showOpenDialog(StructureEditApplication.stage);
        if (file != null) {
            schematicChooser.setInitialDirectory(file.getParentFile());
            schematicChooser.setInitialFileName(file.getName());
            openSchematic(file);
        }
        renderer.animator.resume();
    }

    public void openSchematic(@NotNull File file) {
        LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.schematic.loading"), file);
        schematic = null;
        SchematicFormat format = SchematicFormats.findByFile(file);
        if (format != null) {
            try (SchematicReader reader = format.getReader(new FileInputStream(file))) {
                schematic = reader.read();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getString("log.schematic.error_reading"), e.getMessage());
                LOGGER.catching(Level.DEBUG, e);
                StructureEditApplication.stage.setTitle(Configuration.LANGUAGE.getString("ui.window.title"));
            } catch (ValidationException e) {
                LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getString("log.schematic.invalid"), e.getMessage());
                LOGGER.catching(Level.DEBUG, e);
                StructureEditApplication.stage.setTitle(Configuration.LANGUAGE.getString("ui.window.title"));
            }
        } else {
            LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getString("log.schematic.not_schematic"));
            StructureEditApplication.stage.setTitle(Configuration.LANGUAGE.getString("ui.window.title"));
        }

        disableEditors();
        if (schematic != null) {
            enableEditors();
            int[] size = schematic.getSize();
            renderedHeight = size[1];
            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.schematic.loaded"), file);
            StructureEditApplication.stage.setTitle(String.format(Configuration.LANGUAGE.getString("ui.window.title_with_file"), file.getName()));
        }
    }

    private void disableEditors() {
        schematicInfoController.disableComponents();
        blockEditorController.disableComponents();
        blockStateEditorController.disableComponents();
        entityEditorController.disableComponents();
        biomeEditorController.disableComponents();
        biomeStateEditorController.disableComponents();
    }

    private void enableEditors() {
        schematicInfoController.enableComponents();
        schematicInfoController.setSchematic(schematic);
        blockEditorController.enableComponents();
        blockEditorController.setSchematic(schematic);
        blockStateEditorController.enableComponents();
        blockStateEditorController.setSchematic(schematic);
        entityEditorController.enableComponents();
        entityEditorController.setSchematic(schematic);
        biomeEditorController.enableComponents();
        biomeEditorController.setSchematic(schematic);
        biomeStateEditorController.enableComponents();
        biomeStateEditorController.setSchematic(schematic);
    }

    @FXML
    public void showSaveDialog() {
        if (schematic != null) {
            renderer.animator.pause();
            schematicChooser.getExtensionFilters().remove(FILTER_ALL);
            File file = schematicChooser.showSaveDialog(StructureEditApplication.stage);
            if (file != null) {
                schematicChooser.setInitialDirectory(file.getParentFile());
                schematicChooser.setInitialFileName(file.getName());
                FileChooser.ExtensionFilter filter = schematicChooser.getSelectedExtensionFilter();
                SchematicFormat format = SchematicFormats.getFileExtensionFilterMap().get(filter);
                saveSchematic(file, format);
            }
            schematicChooser.getExtensionFilters().add(0, FILTER_ALL);
            renderer.animator.resume();
        } else {
            LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getString("log.schematic.not_loaded"));
        }
    }

    public void saveSchematic(File file, SchematicFormat format) {
        try {
            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.schematic.saving"), file);
            if (format != null) {
                try (SchematicWriter reader = format.getWriter(new FileOutputStream(file))) {
                    reader.write(schematic);
                }
            }
            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.schematic.saved"), file);
            StructureEditApplication.stage.setTitle(String.format(Configuration.LANGUAGE.getString("ui.window.title_with_file"), file.getName()));
        } catch (IOException | UnsupportedOperationException e1) {
            LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getString("log.schematic.error_writing"), e1.getMessage());
        }
    }

    @FXML
    public void showAssetsChooser() {
        renderer.animator.pause();
        File file = assetsChooser.showDialog(StructureEditApplication.stage);
        if (file != null) {
            setAssets(file);
        }
        blockEditorController.updateSelectedBlock();
        renderer.animator.resume();
    }

    public void setAssets(File file) {
        file = Objects.requireNonNullElse(file, new File(""));
        assetsChooser.setInitialDirectory(file.getParentFile());
        Path assets = file.toPath();
        Assets.setPath(assets);
        shouldReloadTextures = true;
    }

    public void setAssets(Path path) {
        path = Objects.requireNonNullElse(path, Path.of(""));
        File file = path.toFile();
        assetsChooser.setInitialDirectory(file.getParentFile());
        Assets.setPath(path);
        shouldReloadTextures = true;
    }

    @FXML
    public void selectLogLevel() {
        ChoiceDialog<Level> dialog = new ChoiceDialog<>(LogManager.getRootLogger().getLevel(), Level.values());
        dialog.setTitle(Configuration.LANGUAGE.getString("ui.menu_bar.settings_menu.log_level.title"));
        dialog.setContentText(Configuration.LANGUAGE.getString("ui.menu_bar.settings_menu.log_level.label"));
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> dialog.close());
        Optional<Level> level = dialog.showAndWait();
        if (level.isPresent()) {
            LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.log_level.setting"), level.get());
            Configurator.setAllLevels(LogManager.ROOT_LOGGER_NAME, level.get());
        }
    }

    @FXML
    public void showControlsDialog() {
        renderer.animator.pause();
        javafx.scene.control.Dialog<?> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle(Configuration.LANGUAGE.getString("ui.menu_bar.help_menu.controls.title"));
        dialog.setContentText(Configuration.LANGUAGE.getString("ui.menu_bar.help_menu.controls.dialog"));
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> dialog.close());
        dialog.show();
        renderer.animator.resume();
    }

    // TODO Replace this with a JavaFX 3D implementation to make things easier. (Only do this if JavaFX 3D can do everything required to emulate Minecraft rendering)
    private class SchematicRenderer extends MouseAdapter implements GLEventListener, KeyListener {

        private static final Logger LOGGER = LogManager.getLogger();
        private static final float SCALE = 1.0f;
        private static final float RESCALE_22_5 = 1.0f / (float) Math.cos(Math.toRadians(22.5f));
        private static final float RESCALE_45 = 1.0f / (float) Math.cos(Math.toRadians(45.0f));
        private static final float MODEL_SIZE = 16.0f;
        private static final long TICK_LENGTH = 50L;
        private static final float ROTATION_SENSITIVITY = 1.0f;
        private static final float TRANSLATION_SENSITIVITY = 0.1f;
        private final Random random = new Random();
        private final FloatBuffer tempMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
        private final FloatBuffer tempVectorBuffer = GLBuffers.newDirectFloatBuffer(4);
        // private final FloatBuffer screenDepth = GLBuffers.newDirectFloatBuffer(1);
        private final IntBuffer bufferObject = GLBuffers.newDirectIntBuffer(6);
        private final IntBuffer vertexArrayObject = GLBuffers.newDirectIntBuffer(2);
        private final Matrix4f projectionMatrix = new Matrix4f();
        private final Matrix4f viewMatrix = new Matrix4f();
        private final Matrix4fStack modelMatrix = new Matrix4fStack(5);
        private final Matrix4f textureMatrix = new Matrix4f();
        private final Matrix3f normalMatrix = new Matrix3f();
        private final Map<String, Texture> textures = new HashMap<>();
        private final Animator animator = new Animator();
        private final Camera camera = new Camera();
        private int vertexShader;
        private int fragmentShader;
        private int shaderProgram;
        private int axisVertexShader;
        private int axisFragmentShader;
        private int axisShaderProgram;
        private int lightPositionLocation;
        private int lightAmbientLocation;
        private int lightDiffuseLocation;
        private int lightSpecularLocation;
        private int materialAmbientLocation;
        private int materialDiffuseLocation;
        private int materialSpecularLocation;
        private int materialShininessLocation;
        private int textureLocation;
        private int mixFactorLocation;
        private Point mousePoint;
        // private Point2D mousePoint;

        @Nullable
        private static JSONArray getElements(@NotNull JSONObject model) {
            if (model.has("elements")) {
                return model.getJSONArray("elements");
            } else if (model.has("parent")) {
                return getElements(Assets.getModel(model.getString("parent")));
            } else {
                return null;
            }
        }

        private static Map<String, String> getTextures(@NotNull JSONObject model, Map<String, String> textures) {
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

        private static void getTextureFromId(@NotNull JSONObject model, Map<? super String, String> textures, String name) {
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
        public static Color getTint(@NotNull BlockState blockState, BiomeState biomeState) {
            String namespacedId = blockState.getId();
            Map<String, String> properties = blockState.getProperties();
            switch (namespacedId) {
                case "minecraft:redstone_wire" -> {
                    int power = 0;
                    if (properties.containsKey("power")) {
                        try {
                            power = Integer.parseInt(properties.get("power"));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    switch (power) {
                        case 1 -> {
                            return Color.valueOf("#6F0000");
                        }
                        case 2 -> {
                            return Color.valueOf("#790000");
                        }
                        case 3 -> {
                            return Color.valueOf("#820000");
                        }
                        case 4 -> {
                            return Color.valueOf("#8C0000");
                        }
                        case 5 -> {
                            return Color.valueOf("#970000");
                        }
                        case 6 -> {
                            return Color.valueOf("#A10000");
                        }
                        case 7 -> {
                            return Color.valueOf("#AB0000");
                        }
                        case 8 -> {
                            return Color.valueOf("#B50000");
                        }
                        case 9 -> {
                            return Color.valueOf("#BF0000");
                        }
                        case 10 -> {
                            return Color.valueOf("#CA0000");
                        }
                        case 11 -> {
                            return Color.valueOf("#D30000");
                        }
                        case 12 -> {
                            return Color.valueOf("#DD0000");
                        }
                        case 13 -> {
                            return Color.valueOf("#E70600");
                        }
                        case 14 -> {
                            return Color.valueOf("#F11B00");
                        }
                        case 15 -> {
                            return Color.valueOf("#FC3100");
                        }
                        default -> { // 0
                            return Color.valueOf("#4B0000");
                        }
                    }
                }
                case "minecraft:grass_block", "minecraft:grass", "minecraft:tall_grass", "minecraft:fern", "minecraft:large_fern", "minecraft:potted_fern", "minecraft:sugar_cane" -> {
                    String biomeId = biomeState.getId();
                    String biomeName = biomeId.substring(biomeId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                    try {
                        return GrassColor.valueOf(biomeName).getColor();
                    } catch (IllegalArgumentException e) {
                        return GrassColor.OCEAN.getColor();
                    }
                }
                case "minecraft:oak_leaves", "minecraft:dark_oak_leaves", "minecraft:jungle_leaves", "minecraft:acacia_leaves", "minecraft:vine" -> {
                    String biomeId = biomeState.getId();
                    String biomeName = biomeId.substring(biomeId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                    try {
                        return FoliageColor.valueOf(biomeName).getColor();
                    } catch (IllegalArgumentException e) {
                        return FoliageColor.OCEAN.getColor();
                    }
                }
                case "minecraft:water", "minecraft:water_cauldron" -> {
                    String biomeId = biomeState.getId();
                    String biomeName = biomeId.substring(biomeId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                    try {
                        return WaterColor.valueOf(biomeName).getColor();
                    } catch (IllegalArgumentException e) {
                        return WaterColor.DEFAULT.getColor();
                    }
                }
                case "minecraft:birch_leaves" -> {
                    return Color.valueOf("#80A755");
                }
                case "minecraft:spruce_leaves" -> {
                    return Color.valueOf("#619961");
                }
                case "minecraft:lily_pad" -> {
                    return Color.valueOf("#208030");
                }
                default -> {
                    return DEFAULT_TINT;
                }
            }
        }

        public static double simplifyAngle(double angle) {
            return simplifyAngle(angle, Math.PI);
        }

        public static double simplifyAngle(double angle, double center) {
            return angle - 2 * Math.PI * Math.floor((angle + Math.PI - center) / (2 * Math.PI));
        }

        @Override
        public void init(@NotNull GLAutoDrawable drawable) {
            GL4 gl = drawable.getGL().getGL4(); // get the OpenGL graphics context
            gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f); // set the clear color to gray
            gl.glClearDepth(1.0f); // set clear depth value to farthest
            gl.glEnable(GL_DEPTH_TEST); // enables depth testing
            gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
            gl.glLineWidth(2.0f);
            gl.setSwapInterval(1);
            gl.glEnable(GL_CULL_FACE);

            // Axis shader
            {
                axisVertexShader = gl.glCreateShader(GL_VERTEX_SHADER);
                axisFragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

                String vertexShaderSource = null;
                String fragmentShaderSource = null;
                try {
                    vertexShaderSource = InternalUtils.read("/axis_shader.vert");
                    fragmentShaderSource = InternalUtils.read("/axis_shader.frag");
                } catch (IOException e) {
                    LOGGER.log(Level.ERROR, e.getMessage());
                }

                String[] vertexLines = {vertexShaderSource};
                gl.glShaderSource(axisVertexShader, 1, vertexLines, null, 0);
                gl.glCompileShader(axisVertexShader);

                //Check compile status.
                int[] compiled = new int[1];
                gl.glGetShaderiv(axisVertexShader, GL_COMPILE_STATUS, compiled, 0);
                if (compiled[0] != 0) {
                    LOGGER.log(Level.DEBUG, "Compiled axis vertex shader");
                } else {
                    int[] logLength = new int[1];
                    gl.glGetShaderiv(axisVertexShader, GL_INFO_LOG_LENGTH, logLength, 0);

                    byte[] log = new byte[logLength[0]];
                    gl.glGetShaderInfoLog(axisVertexShader, logLength[0], null, 0, log, 0);

                    LOGGER.log(Level.ERROR, "Error compiling the axis vertex shader: " + new String(log));
                    System.exit(1);
                }

                String[] fragmentLines = {fragmentShaderSource};
                gl.glShaderSource(axisFragmentShader, 1, fragmentLines, null, 0);
                gl.glCompileShader(axisFragmentShader);

                //Check compile status.
                gl.glGetShaderiv(axisFragmentShader, GL_COMPILE_STATUS, compiled, 0);
                if (compiled[0] != 0) {
                    LOGGER.log(Level.DEBUG, "Compiled axis fragment shader");
                } else {
                    int[] logLength = new int[1];
                    gl.glGetShaderiv(axisFragmentShader, GL_INFO_LOG_LENGTH, logLength, 0);

                    byte[] log = new byte[logLength[0]];
                    gl.glGetShaderInfoLog(axisFragmentShader, logLength[0], null, 0, log, 0);

                    LOGGER.log(Level.ERROR, "Error compiling the axis fragment shader: " + new String(log));
                    System.exit(1);
                }

                axisShaderProgram = gl.glCreateProgram();
                gl.glAttachShader(axisShaderProgram, axisVertexShader);
                gl.glAttachShader(axisShaderProgram, axisFragmentShader);
                gl.glLinkProgram(axisShaderProgram);
                gl.glValidateProgram(axisShaderProgram);
            }

            // Lighting shader
            {
                vertexShader = gl.glCreateShader(GL_VERTEX_SHADER);
                fragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

                String vertexShaderSource = null;
                String fragmentShaderSource = null;
                try {
                    vertexShaderSource = InternalUtils.read("/shader.vert");
                    fragmentShaderSource = InternalUtils.read("/shader.frag");
                } catch (IOException e) {
                    LOGGER.log(Level.ERROR, e.getMessage());
                }

                String[] vertexLines = {vertexShaderSource};
                gl.glShaderSource(vertexShader, 1, vertexLines, null, 0);
                gl.glCompileShader(vertexShader);

                //Check compile status.
                int[] compiled = new int[1];
                gl.glGetShaderiv(vertexShader, GL_COMPILE_STATUS, compiled, 0);
                if (compiled[0] != 0) {
                    LOGGER.log(Level.DEBUG, "Compiled vertex shader");
                } else {
                    int[] logLength = new int[1];
                    gl.glGetShaderiv(vertexShader, GL_INFO_LOG_LENGTH, logLength, 0);

                    byte[] log = new byte[logLength[0]];
                    gl.glGetShaderInfoLog(vertexShader, logLength[0], null, 0, log, 0);

                    LOGGER.log(Level.ERROR, "Error compiling the vertex shader: " + new String(log));
                    System.exit(1);
                }

                String[] fragmentLines = {fragmentShaderSource};
                gl.glShaderSource(fragmentShader, 1, fragmentLines, null, 0);
                gl.glCompileShader(fragmentShader);

                //Check compile status.
                gl.glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, compiled, 0);
                if (compiled[0] != 0) {
                    LOGGER.log(Level.DEBUG, "Compiled fragment shader");
                } else {
                    int[] logLength = new int[1];
                    gl.glGetShaderiv(fragmentShader, GL_INFO_LOG_LENGTH, logLength, 0);

                    byte[] log = new byte[logLength[0]];
                    gl.glGetShaderInfoLog(fragmentShader, logLength[0], null, 0, log, 0);

                    LOGGER.log(Level.ERROR, "Error compiling the fragment shader: " + new String(log));
                    System.exit(1);
                }

                shaderProgram = gl.glCreateProgram();
                gl.glAttachShader(shaderProgram, vertexShader);
                gl.glAttachShader(shaderProgram, fragmentShader);
                gl.glLinkProgram(shaderProgram);
                gl.glValidateProgram(shaderProgram);

                lightPositionLocation = gl.glGetUniformLocation(shaderProgram, "lightPosition");
                lightAmbientLocation = gl.glGetUniformLocation(shaderProgram, "Ka");
                lightDiffuseLocation = gl.glGetUniformLocation(shaderProgram, "Kd");
                lightSpecularLocation = gl.glGetUniformLocation(shaderProgram, "Ks");
                materialAmbientLocation = gl.glGetUniformLocation(shaderProgram, "ambientColor");
                materialDiffuseLocation = gl.glGetUniformLocation(shaderProgram, "diffuseColor");
                materialSpecularLocation = gl.glGetUniformLocation(shaderProgram, "specularColor");
                materialShininessLocation = gl.glGetUniformLocation(shaderProgram, "shininess");
                textureLocation = gl.glGetUniformLocation(shaderProgram, "texture");
                mixFactorLocation = gl.glGetUniformLocation(shaderProgram, "mixFactor");
            }

            gl.glGenBuffers(bufferObject.capacity(), bufferObject);

            gl.glGenVertexArrays(vertexArrayObject.capacity(), vertexArrayObject);

            {
                gl.glBindVertexArray(vertexArrayObject.get(0));

                short[] indices = { //
                        0, 1, //
                        2, 3, //
                        4, 5 //
                };
                ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer(indices);
                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObject.get(0));
                gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) indexBuffer.capacity() * Short.BYTES, indexBuffer, GL_DYNAMIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(1));
                gl.glBufferData(GL_ARRAY_BUFFER, 18L * Float.BYTES, null, GL_DYNAMIC_DRAW);
                gl.glEnableVertexAttribArray(Semantic.Attribute.POSITION);
                gl.glVertexAttribPointer(Semantic.Attribute.POSITION, 3, GL_FLOAT, false, 0, 0);

                gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(2));
                gl.glBufferData(GL_ARRAY_BUFFER, 24L * Float.BYTES, null, GL_DYNAMIC_DRAW);
                gl.glEnableVertexAttribArray(Semantic.Attribute.COLOR);
                gl.glVertexAttribPointer(Semantic.Attribute.COLOR, 4, GL_FLOAT, false, 0, 0);
            }

            {
                gl.glBindVertexArray(vertexArrayObject.get(1));

                short[] indices = { //
                        0, 1, 2, //
                        2, 3, 0 //
                };
                ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer(indices);
                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObject.get(0));
                gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) indexBuffer.capacity() * Short.BYTES, indexBuffer, GL_DYNAMIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(1));
                gl.glBufferData(GL_ARRAY_BUFFER, 12L * Float.BYTES, null, GL_DYNAMIC_DRAW);
                gl.glEnableVertexAttribArray(Semantic.Attribute.POSITION);
                gl.glVertexAttribPointer(Semantic.Attribute.POSITION, 3, GL_FLOAT, false, 0, 0);

                gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(2));
                gl.glBufferData(GL_ARRAY_BUFFER, 16L * Float.BYTES, null, GL_DYNAMIC_DRAW);
                gl.glEnableVertexAttribArray(Semantic.Attribute.COLOR);
                gl.glVertexAttribPointer(Semantic.Attribute.COLOR, 4, GL_FLOAT, false, 0, 0);

                gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(3));
                gl.glBufferData(GL_ARRAY_BUFFER, 12L * Float.BYTES, null, GL_DYNAMIC_DRAW);
                gl.glEnableVertexAttribArray(Semantic.Attribute.NORMAL);
                gl.glVertexAttribPointer(Semantic.Attribute.NORMAL, 3, GL_FLOAT, false, 0, 0);

                gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(4));
                gl.glBufferData(GL_ARRAY_BUFFER, 8L * Float.BYTES, null, GL_DYNAMIC_DRAW);
                gl.glEnableVertexAttribArray(Semantic.Attribute.TEX_COORD);
                gl.glVertexAttribPointer(Semantic.Attribute.TEX_COORD, 2, GL_FLOAT, false, 0, 0);

                gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(5));
                gl.glBufferData(GL_ARRAY_BUFFER, 8L * Float.BYTES, null, GL_DYNAMIC_DRAW);
                gl.glEnableVertexAttribArray(Semantic.Attribute.TEX_COORD_2);
                gl.glVertexAttribPointer(Semantic.Attribute.TEX_COORD_2, 2, GL_FLOAT, false, 0, 0);
            }

            gl.glBindVertexArray(0);

            camera.reset();

            animator.add(drawable);
            animator.setRunAsFastAsPossible(true);

            animator.start();
        }

        @Override
        public void dispose(@NotNull GLAutoDrawable drawable) {
            GL4 gl = drawable.getGL().getGL4();
            animator.stop();
            animator.remove(drawable);
            gl.glUseProgram(0);
            gl.glDeleteBuffers(bufferObject.capacity(), bufferObject);
            gl.glDeleteVertexArrays(vertexArrayObject.capacity(), vertexArrayObject);
            gl.glDetachShader(shaderProgram, vertexShader);
            gl.glDeleteShader(vertexShader);
            gl.glDetachShader(shaderProgram, fragmentShader);
            gl.glDeleteShader(fragmentShader);
            gl.glDeleteProgram(shaderProgram);
            gl.glDetachShader(axisShaderProgram, axisVertexShader);
            gl.glDeleteShader(axisVertexShader);
            gl.glDetachShader(axisShaderProgram, axisFragmentShader);
            gl.glDeleteShader(axisFragmentShader);
            gl.glDeleteProgram(axisShaderProgram);
        }

        // TODO Implement order-independent transparency to fix the issues with rendering translucent colors/textures.
        @Override
        public void display(@NotNull GLAutoDrawable drawable) {
            GL4 gl = drawable.getGL().getGL4();
            if (shouldReloadTextures) {
                textures.forEach((s, texture) -> texture.destroy(gl));
                textures.clear();
                Assets.getTextureMap().forEach((s, textureData) -> textures.put(s, TextureIO.newTexture(textureData)));

                    /*ObservableList<String> items = FXCollections.observableArrayList(Assets.getBlockStateMap().keySet());
                    items.remove("minecraft:missing");
                    Collections.sort(items);
                    blockStateIdComboBox.setItems(items);
                    blockStateIdAutoComplete.setItems(items);*/

                shouldReloadTextures = false;
            }
            gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            modelMatrix.identity();
            if (schematic != null) {
                int[] size = schematic.getSize();
                // bottom-left-front corner of schematic is (0,0,0) so we need to center it at the origin
                modelMatrix.translate(-size[0] / 2.0f, -size[1] / 2.0f, -size[2] / 2.0f);
                // draw schematic border
                drawAxes(gl, size[0], size[1], size[2]);
                // draw a cube
                for (int x = 0; x < size[0]; x++) {
                    for (int y = 0; y < renderedHeight; y++) {
                        for (int z = 0; z < size[2]; z++) {
                            Block block = schematic.getBlock(x, y, z);
                            if (block != null) {
                                BlockState blockState = schematic.getBlockState(block.getBlockStateIndex(), blockStateEditorController.getPaletteIndex());

                                Biome biome = schematic.getBiome(x, y, z);
                                BiomeState biomeState = biome != null ? schematic.getBiomeState(biome.getBiomeStateIndex()) : new BiomeState("minecraft:ocean");
                                if (biomeState == null) {
                                    biomeState = new BiomeState("minecraft:ocean");
                                }

                                long seed = x + (long) y * size[2] * size[0] + (long) z * size[0];
                                random.setSeed(seed);
                                List<JSONObject> modelList = getModelsFromBlockState(blockState);
                                Color tint = getTint(blockState, biomeState);

                                try {
                                    for (JSONObject model : modelList) {
                                        modelMatrix.pushMatrix();
                                        modelMatrix.translate(x, y, z);
                                        drawModel(gl, model, tint);
                                        modelMatrix.popMatrix();
                                    }
                                } catch (IllegalStateException e) {
                                    System.exit(1);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void reshape(@NotNull GLAutoDrawable drawable, int x, int y, int width, int height) {
            GL4 gl = drawable.getGL().getGL4(); // get the OpenGL graphics context
            camera.perspective(x, y, width, height);
            gl.glViewport(x, y, width, height);
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
        }

        @Override
        public void mouseWheelMoved(@NotNull MouseWheelEvent e) {
            camera.zoom((float) e.getPreciseWheelRotation());
        }

        @Override
        public void mouseDragged(@NotNull MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                // Rotate the camera
                float dTheta = (float) Math.toRadians(mousePoint.x - e.getX()) * ROTATION_SENSITIVITY;
                float dPhi = (float) Math.toRadians(mousePoint.y - e.getY()) * ROTATION_SENSITIVITY;
                camera.rotate(dTheta, dPhi);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                // TODO Make the camera drag translation more accurate.
                // Translate the camera
                float dx = -(mousePoint.x - e.getX()) * TRANSLATION_SENSITIVITY;
                float dy = (mousePoint.y - e.getY()) * TRANSLATION_SENSITIVITY;
                // camera.pan(mousePoint.x, mousePoint.y, e.getX(), e.getY());
                camera.pan(dx, dy);
            }
            mousePoint = e.getPoint();
        }

        // FIXME "mousePoint" does not update if the mouse is moved whilst a file chooser is open.
        @Override
        public void mouseMoved(@NotNull MouseEvent e) {
            mousePoint = e.getPoint();
        }

        /*public void keyPressed(@NotNull KeyEvent e) {
            KeyCode keyCode = e.getCode();
            switch (keyCode) {
                case UP -> {
                    if (schematic != null) {
                        int[] size = schematic.getSize();
                        if (renderedHeight < size[1]) {
                            renderedHeight++;
                        } else {
                            // TODO How to make feedback sounds with JavaFX?
                        }
                        if (renderedHeight > size[1]) {
                            renderedHeight = size[1];
                        }
                    }
                }
                case DOWN -> {
                    if (renderedHeight > 0) {
                        renderedHeight--;
                    } else {
                        // TODO How to make feedback sounds with JavaFX?
                    }
                    if (renderedHeight < 0) {
                        renderedHeight = 0;
                    }
                }
            }
        }

        public void mouseWheelMoved(@NotNull ScrollEvent e) {
            camera.zoom((float) e.getDeltaY());
        }

        public void mouseDragged(@NotNull MouseEvent e) {
            if (e.isPrimaryButtonDown()) {
                // Rotate the camera
                float dTheta = (float) Math.toRadians(mousePoint.getX() - e.getX()) * ROTATION_SENSITIVITY;
                float dPhi = (float) Math.toRadians(mousePoint.getY() - e.getY()) * ROTATION_SENSITIVITY;
                camera.rotate(dTheta, dPhi);
            } else if (e.isSecondaryButtonDown()) {
                // TODO Make the camera drag translation more accurate.
                // Translate the camera
                float dx = (float) (-(mousePoint.getX() - e.getX()) * TRANSLATION_SENSITIVITY);
                float dy = (float) ((mousePoint.getY() - e.getY()) * TRANSLATION_SENSITIVITY);
                // camera.pan(mousePoint.x, mousePoint.y, e.getX(), e.getY());
                camera.pan(dx, dy);
            }
            mousePoint = new Point2D(e.getX(), e.getY());
        }

        // FIXME "mousePoint" does not update if the mouse is moved whilst a file chooser is open.
        public void mouseMoved(@NotNull MouseEvent e) {
            mousePoint = new Point2D(e.getX(), e.getY());
        }*/

        public Texture getTexture(@NotNull String namespacedId) {
            if (!namespacedId.contains(":")) {
                namespacedId = "minecraft:" + namespacedId;
            }
            if (textures.containsKey(namespacedId)) {
                return textures.get(namespacedId);
            }
            Texture texture = textures.getOrDefault(namespacedId, textures.get("minecraft:missing"));
            textures.put(namespacedId, texture);
            return texture;
        }

        public void drawAxes(@NotNull GL4 gl, float sizeX, float sizeY, float sizeZ) {
            gl.glUseProgram(axisShaderProgram);

            gl.glBindVertexArray(vertexArrayObject.get(0));

            projectionMatrix.get(tempMatrixBuffer);
            gl.glUniformMatrix4fv(Semantic.Uniform.PROJECTION_MATRIX, 1, false, tempMatrixBuffer);
            viewMatrix.get(tempMatrixBuffer);
            gl.glUniformMatrix4fv(Semantic.Uniform.VIEW_MATRIX, 1, false, tempMatrixBuffer);
            modelMatrix.get(tempMatrixBuffer);
            gl.glUniformMatrix4fv(Semantic.Uniform.MODEL_MATRIX, 1, false, tempMatrixBuffer);

            short[] indices = { //
                    0, 1, // X-axis (red)
                    2, 3, // Y-axis (green)
                    4, 5 // Z-axis (blue)
            };
            ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer(indices);
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObject.get(0));
            gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) indexBuffer.capacity() * Short.BYTES, indexBuffer, GL_DYNAMIC_DRAW);

            float[] positions = { //
                    0.0f, 0.0f, 0.0f, // X-axis (red)
                    sizeX, 0.0f, 0.0f, //
                    0.0f, 0.0f, 0.0f, // Y-axis (green)
                    0.0f, sizeY, 0.0f, //
                    0.0f, 0.0f, 0.0f, // Z-axis (blue)
                    0.0f, 0.0f, sizeZ //
            };
            FloatBuffer positionBuffer = GLBuffers.newDirectFloatBuffer(positions);
            gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(1));
            gl.glBufferData(GL_ARRAY_BUFFER, (long) positionBuffer.capacity() * Float.BYTES, positionBuffer, GL_DYNAMIC_DRAW);

            float[] colors = { //
                    1.0f, 0.0f, 0.0f, 1.0f, // X-axis (red)
                    1.0f, 0.0f, 0.0f, 1.0f, //
                    0.0f, 1.0f, 0.0f, 1.0f, // Y-axis (green)
                    0.0f, 1.0f, 0.0f, 1.0f, //
                    0.0f, 0.0f, 1.0f, 1.0f, // Z-axis (blue)
                    0.0f, 0.0f, 1.0f, 1.0f //
            };
            FloatBuffer colorBuffer = GLBuffers.newDirectFloatBuffer(colors);
            gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(2));
            gl.glBufferData(GL_ARRAY_BUFFER, (long) colorBuffer.capacity() * Float.BYTES, colorBuffer, GL_DYNAMIC_DRAW);

            gl.glDrawElements(GL_LINES, indices.length, GL_UNSIGNED_SHORT, 0);

            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            gl.glBindVertexArray(0);

            gl.glUseProgram(0);
        }

        @NotNull
        public List<JSONObject> getModelsFromBlockState(@NotNull BlockState block) {
            List<JSONObject> modelList = new ArrayList<>();
            String namespacedId = block.getId();
            Map<String, String> properties = block.getProperties();
            if (properties.containsKey("waterlogged") && properties.get("waterlogged").equals("true")) {
                JSONObject waterModel = new JSONObject();
                waterModel.put("model", "minecraft:block/water");
                modelList.add(waterModel);
            }
            JSONObject blockState = Assets.getBlockState(namespacedId);
            if (blockState.has("variants")) {
                JSONObject variants = blockState.getJSONObject("variants");
                Set<String> keySet = variants.keySet();
                for (String variantName : keySet) {
                    Set<Map.Entry<String, String>> states = BlockState.toPropertyMap(variantName, false).entrySet();
                    boolean contains = true;
                    for (Map.Entry<String, String> state : states) {
                        if (!state.getValue().equals(properties.get(state.getKey()))) {
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
                                    if (!values.contains(properties.get(state))) {
                                        contains = false;
                                        break;
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
                                if (properties.containsKey(state)) {
                                    if (!values.contains(properties.get(state))) {
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

        // TODO use object to contain these ten million model attributes
        public void drawModel(@NotNull GL4 gl, @NotNull JSONObject jsonObject, Color tint) {
            gl.glUseProgram(shaderProgram);

            String modelPath = jsonObject.getString("model");
            JSONObject model = Assets.getModel(modelPath);
            int x = jsonObject.optInt("x", 0);
            int y = jsonObject.optInt("y", 0);
            boolean uvlock = jsonObject.optBoolean("uvlock", false);

            modelMatrix.translate(0.5f, 0.5f, 0.5f);
            modelMatrix.rotateY((float) Math.toRadians(-y));
            modelMatrix.rotateX((float) Math.toRadians(-x));
            modelMatrix.translate(-0.5f, -0.5f, -0.5f);

            Map<String, String> textures = getTextures(model, new HashMap<>());

            JSONArray elements = getElements(model);
            if (elements != null) {
                for (Object elementObject : elements) {
                    modelMatrix.pushMatrix();

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

                    float fromX = (float) (from.getDouble(0) / MODEL_SIZE);
                    float fromY = (float) (from.getDouble(1) / MODEL_SIZE);
                    float fromZ = (float) (from.getDouble(2) / MODEL_SIZE);
                    float toX = (float) (to.getDouble(0) / MODEL_SIZE);
                    float toY = (float) (to.getDouble(1) / MODEL_SIZE);
                    float toZ = (float) (to.getDouble(2) / MODEL_SIZE);

                    Vector3f fromVec = new Vector3f(fromX, fromY, fromZ);
                    Vector3f toVec = new Vector3f(toX, toY, toZ);

                    if (axis != null && origin != null) {
                        float originX = (float) (origin.getDouble(0) / MODEL_SIZE);
                        float originY = (float) (origin.getDouble(1) / MODEL_SIZE);
                        float originZ = (float) (origin.getDouble(2) / MODEL_SIZE);
                        modelMatrix.translate(originX, originY, originZ);
                        float rescaleFactor = 1.0f;
                        if (Math.abs(angle) == 22.5f) {
                            rescaleFactor = RESCALE_22_5;
                        } else if (Math.abs(angle) == 45.0f) {
                            rescaleFactor = RESCALE_45;
                        }
                        switch (axis) {
                            case "x" -> {
                                modelMatrix.rotateX((float) Math.toRadians(angle));
                                if (rescale) {
                                    fromVec.mul(1.0f, rescaleFactor, rescaleFactor);
                                    toVec.mul(1.0f, rescaleFactor, rescaleFactor);
                                }
                            }
                            case "y" -> {
                                modelMatrix.rotateY((float) Math.toRadians(angle));
                                if (rescale) {
                                    fromVec.mul(rescaleFactor, 1.0f, rescaleFactor);
                                    toVec.mul(rescaleFactor, 1.0f, rescaleFactor);
                                }
                            }
                            case "z" -> {
                                modelMatrix.rotateZ((float) Math.toRadians(angle));
                                if (rescale) {
                                    fromVec.mul(rescaleFactor, rescaleFactor, 1.0f);
                                    toVec.mul(rescaleFactor, rescaleFactor, 1.0f);
                                }
                            }
                        }
                        fromX = fromVec.x;
                        fromY = fromVec.y;
                        fromZ = fromVec.z;
                        toX = toVec.x;
                        toY = toVec.y;
                        toZ = toVec.z;
                        if (rescale) {
                            switch (axis) {
                                case "x" -> modelMatrix.translate(-originX, -originY * rescaleFactor, -originZ * rescaleFactor);
                                case "y" -> modelMatrix.translate(-originX * rescaleFactor, -originY, -originZ * rescaleFactor);
                                case "z" -> modelMatrix.translate(-originX * rescaleFactor, -originY * rescaleFactor, -originZ);
                            }
                        } else {
                            modelMatrix.translate(-originX, -originY, -originZ);
                        }
                    }

                    gl.glUniform3fv(lightPositionLocation, 1, camera.eye.get(tempVectorBuffer));
                    gl.glUniform1f(lightAmbientLocation, 1.0f);
                    gl.glUniform1f(lightDiffuseLocation, 1.0f);
                    gl.glUniform1f(lightSpecularLocation, 0.1f);
                    if (shade) {
                        gl.glUniform3f(materialAmbientLocation, 0.2f, 0.2f, 0.2f);
                    } else {
                        gl.glUniform3f(materialAmbientLocation, 1.0f, 1.0f, 1.0f);
                    }
                    gl.glUniform3f(materialDiffuseLocation, 0.8f, 0.8f, 0.8f);
                    gl.glUniform3f(materialSpecularLocation, 1.0f, 1.0f, 1.0f);
                    gl.glUniform1f(materialShininessLocation, 1.0f);

                    JSONObject faces = element.getJSONObject("faces");
                    Set<String> faceSet = faces.keySet();
                    for (String faceName : faceSet) {
                        if (!Objects.equals(faceName, "east") && !Objects.equals(faceName, "west") && !Objects.equals(faceName, "up") && !Objects.equals(faceName, "down") && !Objects.equals(faceName, "south") && !Objects.equals(faceName, "north")) {
                            continue;
                        }

                        JSONObject face = faces.getJSONObject(faceName);

                        JSONArray uv = face.optJSONArray("uv");
                        String faceTexture = face.has("texture") ? face.getString("texture").substring(1) : null;
                        String cullface = face.optString("cullface"); // TODO Implement culling.
                        int faceRotation = face.optInt("rotation", 0);
                        int tintIndex = face.optInt("tintindex", -1);

                        float[] components = new float[4];
                        if (tintIndex == -1) {
                            components[0] = (float) DEFAULT_TINT.getRed();
                            components[1] = (float) DEFAULT_TINT.getGreen();
                            components[2] = (float) DEFAULT_TINT.getBlue();
                            components[3] = (float) DEFAULT_TINT.getOpacity();
                        } else {
                            components[0] = (float) tint.getRed();
                            components[1] = (float) tint.getGreen();
                            components[2] = (float) tint.getBlue();
                            components[3] = (float) tint.getOpacity();
                        }

                        Texture texture = getTexture(textures.getOrDefault(faceTexture, "minecraft:missing"));

                        float textureLeft = uv != null ? (float) (uv.getDouble(0) / MODEL_SIZE) : switch (faceName) {
                            case "up", "down", "north", "south" -> fromX;
                            default -> fromZ;
                        };
                        float textureTop = uv != null ? (float) (uv.getDouble(1) / MODEL_SIZE) : switch (faceName) {
                            case "up" -> fromZ;
                            case "down" -> SCALE - toZ;
                            default -> SCALE - toY;
                        };
                        float textureRight = uv != null ? (float) (uv.getDouble(2) / MODEL_SIZE) : switch (faceName) {
                            case "up", "down", "north", "south" -> toX;
                            default -> toZ;
                        };
                        float textureBottom = uv != null ? (float) (uv.getDouble(3) / MODEL_SIZE) : switch (faceName) {
                            case "up" -> toZ;
                            case "down" -> SCALE - fromZ;
                            default -> SCALE - fromY;
                        };

                        float textureLeft2 = textureLeft;
                        float textureTop2 = textureTop;
                        float textureRight2 = textureRight;
                        float textureBottom2 = textureBottom;
                        gl.glUniform1f(mixFactorLocation, 0.0f);
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

                            textureLeft2 /= widthFactor;
                            textureTop2 /= heightFactor;
                            textureRight2 /= widthFactor;
                            textureBottom2 /= heightFactor;

                            long currentTime = System.currentTimeMillis();
                            long currentTick = currentTime / (TICK_LENGTH * frametime);
                            int index = (int) (currentTick % frames.length());
                            /*The mix factor should be a value between 0.0f and 1.0f, representing the passage of time from the current frame to the next. 0.0f is the current frame, and 1.0f is the next frame.*/
                            /* TODO how the heck do i get this */
                            float mixFactor = 0.0f;

                            gl.glUniform1f(mixFactorLocation, mixFactor);

                            Object frame = frames.get(index);
                            double frameDouble = 0.0;
                            if (frame instanceof Integer frameInt) {
                                frameDouble = (double) frameInt;
                            } else if (frame instanceof JSONObject frameObject) {
                                frameDouble = frameObject.getInt("index");
                                // TODO Implement the "time" tag.
                                int time = frameObject.optInt("time", frametime);
                            }

                            int index2 = frames.length() > index + 1 ? index + 1 : 0;
                            Object frame2 = frames.get(index2);
                            double frameDouble2 = 0.0;
                            if (frame2 instanceof Integer frameInt) {
                                frameDouble2 = (double) frameInt;
                            } else if (frame2 instanceof JSONObject frameObject) {
                                frameDouble2 = frameObject.getInt("index");
                            }

                            // Change to the current frame in the animation
                            textureTop += frameDouble / heightFactor;
                            textureBottom += frameDouble / heightFactor;

                            textureTop2 += frameDouble2 / heightFactor;
                            textureBottom2 += frameDouble2 / heightFactor;
                        } else if (texture.getWidth() != texture.getHeight()) {
                            texture = getTexture("minecraft:missing");
                        }

                        for (int i = 0; i < faceRotation; i += 90) {
                            float temp = textureLeft;
                            textureLeft = SCALE - textureBottom;
                            textureBottom = textureRight;
                            textureRight = SCALE - textureTop;
                            textureTop = temp;

                            float temp2 = textureLeft2;
                            textureLeft2 = SCALE - textureBottom2;
                            textureBottom2 = textureRight2;
                            textureRight2 = SCALE - textureTop2;
                            textureTop2 = temp2;
                        }

                        textureMatrix.identity();
                        textureMatrix.translate(0.5f, 0.5f, 0.0f);
                        textureMatrix.rotateZ((float) Math.toRadians(faceRotation));
                        if (uvlock) {
                            switch (faceName) {
                                case "up" -> {
                                    if (x == 180) {
                                        textureMatrix.rotateZ((float) Math.toRadians(y));
                                    } else {
                                        textureMatrix.rotateZ((float) Math.toRadians(-y));
                                    }
                                }
                                case "down" -> {
                                    if (x == 180) {
                                        textureMatrix.rotateZ((float) Math.toRadians(-y));
                                    } else {
                                        textureMatrix.rotateZ((float) Math.toRadians(y));
                                    }
                                }
                                default -> textureMatrix.rotateZ((float) Math.toRadians(-x));
                            }
                        }
                        textureMatrix.scale(1.0f, -1.0f, 1.0f);
                        textureMatrix.translate(-0.5f, -0.5f, 0.0f);

                        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                        gl.glEnable(GL_BLEND);

                        gl.glBindVertexArray(vertexArrayObject.get(1));

                        gl.glUniformMatrix4fv(Semantic.Uniform.PROJECTION_MATRIX, 1, false, projectionMatrix.get(tempMatrixBuffer));
                        gl.glUniformMatrix4fv(Semantic.Uniform.VIEW_MATRIX, 1, false, viewMatrix.get(tempMatrixBuffer));
                        gl.glUniformMatrix4fv(Semantic.Uniform.MODEL_MATRIX, 1, false, modelMatrix.get(tempMatrixBuffer));
                        gl.glUniformMatrix4fv(Semantic.Uniform.TEXTURE_MATRIX, 1, false, textureMatrix.get(tempMatrixBuffer));

                        viewMatrix.mul(modelMatrix, new Matrix4f()).normal(normalMatrix);
                        gl.glUniformMatrix3fv(Semantic.Uniform.NORMAL_MATRIX, 1, false, normalMatrix.get(tempMatrixBuffer));

                        texture.setTexParameterf(gl, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                        texture.setTexParameterf(gl, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                        texture.setTexParameterf(gl, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                        texture.setTexParameterf(gl, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                        texture.setTexParameterf(gl, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
                        texture.bind(gl);
                        gl.glBindSampler(0, textureLocation);

                        short[] indices = { //
                                0, 1, 2, //
                                2, 3, 0 //
                        };
                        ShortBuffer indexBuffer = GLBuffers.newDirectShortBuffer(indices);
                        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObject.get(0));
                        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) indexBuffer.capacity() * Short.BYTES, indexBuffer, GL_DYNAMIC_DRAW);

                        float[] positions = switch (faceName) { //
                            case "east" -> new float[]{ //
                                    toX, fromY, toZ, //
                                    toX, fromY, fromZ, //
                                    toX, toY, fromZ, //
                                    toX, toY, toZ //
                            };
                            case "west" -> new float[]{ //
                                    fromX, fromY, fromZ, //
                                    fromX, fromY, toZ, //
                                    fromX, toY, toZ, //
                                    fromX, toY, fromZ //
                            };
                            case "up" -> new float[]{ //
                                    fromX, toY, toZ,//
                                    toX, toY, toZ, //
                                    toX, toY, fromZ, //
                                    fromX, toY, fromZ //
                            };
                            case "down" -> new float[]{ //
                                    fromX, fromY, fromZ, //
                                    toX, fromY, fromZ, //
                                    toX, fromY, toZ, //
                                    fromX, fromY, toZ //
                            };
                            case "south" -> new float[]{ //
                                    fromX, fromY, toZ, //
                                    toX, fromY, toZ, //
                                    toX, toY, toZ, //
                                    fromX, toY, toZ //
                            };
                            case "north" -> new float[]{ //
                                    toX, fromY, fromZ, //
                                    fromX, fromY, fromZ, //
                                    fromX, toY, fromZ, //
                                    toX, toY, fromZ //
                            };
                            default -> null;
                        };
                        assert positions != null;
                        FloatBuffer positionBuffer = GLBuffers.newDirectFloatBuffer(positions);
                        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(1));
                        gl.glBufferData(GL_ARRAY_BUFFER, (long) positionBuffer.capacity() * Float.BYTES, positionBuffer, GL_DYNAMIC_DRAW);

                        float[] colors = { //
                                components[0], components[1], components[2], components[3], //
                                components[0], components[1], components[2], components[3], //
                                components[0], components[1], components[2], components[3], //
                                components[0], components[1], components[2], components[3], //
                        };
                        FloatBuffer colorBuffer = GLBuffers.newDirectFloatBuffer(colors);
                        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(2));
                        gl.glBufferData(GL_ARRAY_BUFFER, (long) colorBuffer.capacity() * Float.BYTES, colorBuffer, GL_DYNAMIC_DRAW);

                        float[] normals = switch (faceName) { //
                            case "east" -> new float[]{ //
                                    1.0f, 0.0f, 0.0f, //
                                    1.0f, 0.0f, 0.0f, //
                                    1.0f, 0.0f, 0.0f, //
                                    1.0f, 0.0f, 0.0f //
                            };
                            case "west" -> new float[]{ //
                                    -1.0f, 0.0f, 0.0f, //
                                    -1.0f, 0.0f, 0.0f, //
                                    -1.0f, 0.0f, 0.0f, //
                                    -1.0f, 0.0f, 0.0f //
                            };
                            case "up" -> new float[]{ //
                                    0.0f, 1.0f, 0.0f, //
                                    0.0f, 1.0f, 0.0f, //
                                    0.0f, 1.0f, 0.0f, //
                                    0.0f, 1.0f, 0.0f //
                            };
                            case "down" -> new float[]{ //
                                    0.0f, -1.0f, 0.0f, //
                                    0.0f, -1.0f, 0.0f, //
                                    0.0f, -1.0f, 0.0f, //
                                    0.0f, -1.0f, 0.0f //
                            };
                            case "south" -> new float[]{ //
                                    0.0f, 0.0f, 1.0f, //
                                    0.0f, 0.0f, 1.0f, //
                                    0.0f, 0.0f, 1.0f, //
                                    0.0f, 0.0f, 1.0f //
                            };
                            case "north" -> new float[]{ //
                                    0.0f, 0.0f, -1.0f, //
                                    0.0f, 0.0f, -1.0f, //
                                    0.0f, 0.0f, -1.0f, //
                                    0.0f, 0.0f, -1.0f //
                            };
                            default -> null;
                        };
                        FloatBuffer normalBuffer = GLBuffers.newDirectFloatBuffer(normals);
                        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(3));
                        gl.glBufferData(GL_ARRAY_BUFFER, (long) normalBuffer.capacity() * Float.BYTES, normalBuffer, GL_DYNAMIC_DRAW);

                        float[] texCoords = { //
                                textureLeft, textureBottom, //
                                textureRight, textureBottom, //
                                textureRight, textureTop, //
                                textureLeft, textureTop //
                        };
                        FloatBuffer texCoordBuffer = GLBuffers.newDirectFloatBuffer(texCoords);
                        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(4));
                        gl.glBufferData(GL_ARRAY_BUFFER, (long) texCoordBuffer.capacity() * Float.BYTES, texCoordBuffer, GL_DYNAMIC_DRAW);

                        float[] texCoords2 = { //
                                textureLeft2, textureBottom2, //
                                textureRight2, textureBottom2, //
                                textureRight2, textureTop2, //
                                textureLeft2, textureTop2 //
                        };
                        FloatBuffer texCoordBuffer2 = GLBuffers.newDirectFloatBuffer(texCoords2);
                        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferObject.get(5));
                        gl.glBufferData(GL_ARRAY_BUFFER, (long) texCoordBuffer2.capacity() * Float.BYTES, texCoordBuffer2, GL_DYNAMIC_DRAW);

                        gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, 0);

                        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
                        gl.glBindVertexArray(0);
                        gl.glBindTexture(GL_TEXTURE_2D, 0);
                        gl.glBindSampler(0, 0);
                        gl.glDisable(GL_BLEND);
                    }
                    modelMatrix.popMatrix();
                }
            }
            gl.glUseProgram(0);
        }

        private class Camera {
            private static final float MINIMUM_ZOOM = 0.001f;
            private final Vector3f eye = new Vector3f();
            private final Vector3f center = new Vector3f();
            private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
            private final float fovY = (float) Math.toRadians(45.0f);
            private final float zNear = 0.5f;
            private final float zFar = 1000.0f;
            private final int[] viewport = new int[4];
            private final boolean orthographic = false;
            /**
             * X rotation angle in degrees.
             **/
            private float theta = (float) Math.PI / 4.0f;
            /**
             * Y rotation angle in degrees.
             **/
            private float phi = (float) Math.PI / 4.0f;
            private float radius = 30.0f;
            private float panX;
            private float panY;

            {
                update();
            }

            public static Vector3f toCartesian(float radius, float theta, float phi) {
                return toCartesian(radius, theta, phi, new Vector3f());
            }

            public static Vector3f toCartesian(float radius, float theta, float phi, @NotNull Vector3f dest) {
                float x = (float) (radius * Math.sin(phi) * Math.sin(theta));
                float y = (float) (radius * Math.cos(phi));
                float z = (float) (radius * Math.sin(phi) * Math.cos(theta));

                return dest.set(x, y, z);
            }

            public void reset() {
                viewport[0] = 0;
                viewport[1] = 0;
                viewport[2] = 0;
                viewport[3] = 0;

                theta = (float) Math.PI / 4.0f;
                phi = (float) Math.PI / 4.0f;

                radius = 30.0f;

                panX = 0;
                panY = 0;
            }

            public void rotate(float dTheta, float dPhi) {
                theta = (float) simplifyAngle(theta + dTheta);

                float newPhi = (float) simplifyAngle(phi + dPhi);
                if (newPhi > (float) Math.PI) {
                    if (dPhi < 0.0f) {
                        phi = (float) (2.0f * Math.PI);
                        dPhi = 0.0f;
                    } else if (dPhi > 0.0f) {
                        phi = (float) -Math.PI;
                        dPhi = 0.0f;
                    }
                }
                phi = (float) simplifyAngle(phi + dPhi);

                update();
            }

            public void zoom(float distance) {
                if (radius + distance < MINIMUM_ZOOM) {
                    radius = MINIMUM_ZOOM;
                } else {
                    radius += distance;
                }

                update();
            }

            // TODO Use Matrix4f.unproject() to convert mouse coordinates to world coordinates, for accurate panning.
            public void pan(/*float mouseX, float mouseY, float newMouseX, float newMouseY*/ float dx, float dy) {
                panX += dx;
                panY += dy;
                /*toCartesian(radius, theta, phi, eye);
                Vector3f direction = new Vector3f();
                center.sub(eye, direction);
                Vector3f right = new Vector3f();
                direction.cross(up, right);
                Vector3f realUp = new Vector3f();
                right.cross(direction, realUp);
                // center.add(right.mul(dx).add(up.mul(dy)));*/

                /*Vector3f originScreenCoords = projectionMatrix.project(0.0f, 0.0f, 0.0f, viewport, new Vector3f());
                System.out.println("Origin: " + originScreenCoords);
                Vector3f mouseWorldCoords = projectionMatrix.unproject(mouseX, mouseY, originScreenCoords.z, viewport, new Vector3f());
                System.out.println("Mouse: " + mouseWorldCoords);
                Vector3f newMouseWorldCoords = projectionMatrix.unproject(newMouseX, newMouseY, originScreenCoords.z, viewport, new Vector3f());
                System.out.println("New Mouse: " + newMouseWorldCoords);
                Vector3f offset = mouseWorldCoords.sub(newMouseWorldCoords, new Vector3f());
                System.out.println("Offset: " + offset);

                projectionMatrix.invert();
                Vector3f worldCoords = projectionMatrix.unproject(offset, viewport, new Vector3f());
                projectionMatrix.invert();

                viewMatrix.translate(worldCoords);*/

                update();
            }

            public void update() {
                toCartesian(radius, theta, phi, eye);

                viewMatrix.identity();
                if (orthographic) {
                    viewMatrix.scale(1 / radius);
                }
                viewMatrix.translate(panX, panY, 0.0f);
                viewMatrix.lookAt(eye, center, up);

            }

            public void perspective(int x, int y, int width, int height) {
                viewport[0] = x;
                viewport[1] = y;
                viewport[2] = width;
                viewport[3] = height;

                float aspect = (float) width / height;

                if (orthographic) {
                    float orthoHeight = zNear * (float) Math.tan(fovY / 2);
                    float orthoWidth = aspect * orthoHeight;
                    projectionMatrix.setOrtho(-orthoWidth, orthoWidth, -orthoHeight, orthoHeight, zNear, zFar);
                } else {
                    projectionMatrix.setPerspective(fovY, aspect, zNear, zFar);
                }
            }
        }
    }
}
