/*
 * Copyright (C) 2015 eccentric_nz
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

import com.jogamp.opengl.awt.GLJPanel;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import me.dalekcraft.structureedit.StructureEditApplication;
import me.dalekcraft.structureedit.assets.Registries;
import me.dalekcraft.structureedit.schematic.container.Schematic;
import me.dalekcraft.structureedit.schematic.io.*;
import me.dalekcraft.structureedit.ui.editor.*;
import me.dalekcraft.structureedit.util.Language;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.fxmisc.richtext.InlineCssTextArea;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * @author eccentric_nz
 */
public class MainController extends Node {
    /**
     * Color of the missing texture's purple.
     */
    public static final Color MISSING_COLOR = Color.rgb(251, 64, 249);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final FileChooser.ExtensionFilter FILTER_ALL = new FileChooser.ExtensionFilter(Language.LANGUAGE.getString("ui.file_chooser.extension.all"), "*.*");
    public final FileChooser schematicChooser = new FileChooser();
    public final DirectoryChooser assetsChooser = new DirectoryChooser();
    private Schematic schematic;
    private SchematicRenderer renderer;
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
        Path assets = Registries.getInstance().getPath();
        if (assets != null && !assets.toString().equals("")) {
            assetsChooser.setInitialDirectory(assets.toFile());
        }
    }

    @FXML
    public void initialize() {

        // FIXME The GLEventListener only initializes when the window is resized or moved.
        SwingUtilities.invokeLater(() -> {
            renderer = new SchematicRenderer();
            renderer.injectBlockStateEditorController(blockStateEditorController);
            GLJPanel rendererPanel = new GLJPanel();
            rendererPanel.addGLEventListener(renderer);
            /*rendererNode.addEventHandler(KeyEvent.KEY_PRESSED, event -> renderer.keyPressed(event));
            rendererNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> renderer.mouseDragged(event));
            rendererNode.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> renderer.mousePressed(event));
            rendererNode.addEventHandler(ScrollEvent.ANY, event -> renderer.mouseWheelMoved(event));*/
            rendererPanel.addMouseListener(renderer);
            rendererPanel.addMouseMotionListener(renderer);
            rendererPanel.addMouseWheelListener(renderer);
            rendererPanel.addKeyListener(renderer);
            rendererNode.setContent(rendererPanel);
        });

        blockEditorController.injectBlockStateEditorController(blockStateEditorController);
        blockStateEditorController.injectBlockEditorController(blockEditorController);

        biomeStateEditorController.injectBiomeEditorController(biomeEditorController);

        InlineCssTextAreaAppender.addLog4j2TextAreaAppender(logArea);
    }

    @FXML
    public void showOpenDialog() {
        renderer.pause();
        File file = schematicChooser.showOpenDialog(StructureEditApplication.stage);
        if (file != null) {
            schematicChooser.setInitialDirectory(file.getParentFile());
            schematicChooser.setInitialFileName(file.getName());
            openSchematic(file);
        }
        renderer.resume();
    }

    public void openSchematic(@NotNull File file) {
        LOGGER.log(Level.INFO, Language.LANGUAGE.getString("log.schematic.loading"), file);
        schematic = null;
        renderer.schematic = null;
        SchematicFormat format = SchematicFormats.findByFile(file);
        if (format != null) {
            try (SchematicReader reader = format.getReader(new FileInputStream(file))) {
                schematic = reader.read();
                renderer.schematic = schematic;
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, Language.LANGUAGE.getString("log.schematic.error_reading"), e.getMessage());
                LOGGER.catching(Level.DEBUG, e);
                StructureEditApplication.stage.setTitle(Language.LANGUAGE.getString("ui.window.title"));
            } catch (ValidationException e) {
                LOGGER.log(Level.ERROR, Language.LANGUAGE.getString("log.schematic.invalid"), e.getMessage());
                LOGGER.catching(Level.DEBUG, e);
                StructureEditApplication.stage.setTitle(Language.LANGUAGE.getString("ui.window.title"));
            }
        } else {
            LOGGER.log(Level.ERROR, Language.LANGUAGE.getString("log.schematic.not_schematic"));
            StructureEditApplication.stage.setTitle(Language.LANGUAGE.getString("ui.window.title"));
        }

        disableEditors();
        if (schematic != null) {
            enableEditors();
            int[] size = schematic.getSize();
            renderer.renderedHeight = size[1];
            LOGGER.log(Level.INFO, Language.LANGUAGE.getString("log.schematic.loaded"), file);
            StructureEditApplication.stage.setTitle(String.format(Language.LANGUAGE.getString("ui.window.title_with_file"), file.getName()));
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
            renderer.pause();
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
            renderer.resume();
        } else {
            LOGGER.log(Level.ERROR, Language.LANGUAGE.getString("log.schematic.not_loaded"));
        }
    }

    public void saveSchematic(File file, SchematicFormat format) {
        try {
            LOGGER.log(Level.INFO, Language.LANGUAGE.getString("log.schematic.saving"), file);
            if (format != null) {
                try (SchematicWriter reader = format.getWriter(new FileOutputStream(file))) {
                    reader.write(schematic);
                }
            }
            LOGGER.log(Level.INFO, Language.LANGUAGE.getString("log.schematic.saved"), file);
            StructureEditApplication.stage.setTitle(String.format(Language.LANGUAGE.getString("ui.window.title_with_file"), file.getName()));
        } catch (IOException | UnsupportedOperationException e1) {
            LOGGER.log(Level.ERROR, Language.LANGUAGE.getString("log.schematic.error_writing"), e1.getMessage());
        }
    }

    @FXML
    public void showAssetsChooser() {
        renderer.pause();
        File file = assetsChooser.showDialog(StructureEditApplication.stage);
        if (file != null) {
            setAssets(file);
        }
        blockEditorController.updateSelectedBlock();
        renderer.resume();
    }

    public void setAssets(File file) {
        file = Objects.requireNonNullElse(file, new File(""));
        assetsChooser.setInitialDirectory(file.getParentFile());
        Path assets = file.toPath();
        Registries.getInstance().setPath(assets);
        renderer.shouldReloadTextures = true;
    }

    public void setAssets(Path path) {
        path = Objects.requireNonNullElse(path, Path.of(""));
        File file = path.toFile();
        assetsChooser.setInitialDirectory(file.getParentFile());
        Registries.getInstance().setPath(path);
        blockStateEditorController.reloadBlockStates();
        renderer.shouldReloadTextures = true;
    }

    @FXML
    public void selectLogLevel() {
        ChoiceDialog<Level> dialog = new ChoiceDialog<>(LogManager.getRootLogger().getLevel(), Level.values());
        dialog.setTitle(Language.LANGUAGE.getString("ui.menu_bar.settings_menu.log_level.title"));
        dialog.setContentText(Language.LANGUAGE.getString("ui.menu_bar.settings_menu.log_level.label"));
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> dialog.close());
        Optional<Level> level = dialog.showAndWait();
        if (level.isPresent()) {
            LOGGER.log(Level.INFO, Language.LANGUAGE.getString("log.log_level.setting"), level.get());
            Configurator.setAllLevels(LogManager.ROOT_LOGGER_NAME, level.get());
        }
    }

    @FXML
    public void showControlsDialog() {
        renderer.pause();
        javafx.scene.control.Dialog<?> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle(Language.LANGUAGE.getString("ui.menu_bar.help_menu.controls.title"));
        dialog.setContentText(Language.LANGUAGE.getString("ui.menu_bar.help_menu.controls.dialog"));
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> dialog.close());
        dialog.show();
        renderer.resume();
    }
}
