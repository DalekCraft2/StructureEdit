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
package me.dalekcraft.structureedit;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.dalekcraft.structureedit.ui.Controller;
import me.dalekcraft.structureedit.util.Assets;
import me.dalekcraft.structureedit.util.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class StructureEditApplication extends Application {

    private static final Logger LOGGER = LogManager.getLogger(StructureEditApplication.class);
    public static Stage stage;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(StructureEditApplication.class, args);
    }

    @Nullable
    public static String getArgument(@NotNull List<String> argList, String argumentName) {
        if (argList.contains(argumentName)) {
            if (argList.size() > argList.indexOf(argumentName) + 1) {
                return argList.get(argList.indexOf(argumentName) + 1);
            }
        }
        return null;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;

        if (!AnsiConsole.isInstalled()) {
            AnsiConsole.systemInstall();
        }
        LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.starting"));

        List<String> argList = getParameters().getRaw();

        String levelName = getArgument(argList, "-log_level");
        if (levelName != null) {
            try {
                Level level = Level.valueOf(levelName);
                LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.log_level.setting"), level);
                // TODO Figure out why this only works in the IDE.
                Configurator.setAllLevels(LogManager.ROOT_LOGGER_NAME, level);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARN, Configuration.LANGUAGE.getProperty("log.log_level.invalid"), levelName);
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(StructureEditApplication.class.getResource("/scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle(Configuration.LANGUAGE.getProperty("ui.window.title"));
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller controller = fxmlLoader.getController();

        stage.getIcons().add(new Image(Objects.requireNonNull(StructureEditApplication.class.getResourceAsStream("/icon.png")))); // Use Objects.requireNonNull() because it should never be null

        String assetsArg;
        String protocol = Objects.requireNonNull(StructureEditApplication.class.getResource("")).getProtocol();
        if (protocol.equals("jar")) {
            assetsArg = Configuration.CONFIG.getProperty("assets_path");
        } else {
            assetsArg = getArgument(argList, "-assets");
        }
        Path assets = null;
        if (assetsArg != null) {
            try {
                assets = Path.of(assetsArg).toRealPath();
                if (!Files.exists(assets)) {
                    LOGGER.log(Level.WARN, Configuration.LANGUAGE.getProperty("log.assets.invalid"), assets);
                }
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        } else {
            LOGGER.log(Level.WARN, Configuration.LANGUAGE.getProperty("log.assets.not_set"));
        }
        Assets.setAssets(assets);
        if (assets != null) {
            controller.assetsChooser.setInitialDirectory(assets.toFile());
        }
        controller.blockIdComboBox.setItems(FXCollections.observableArrayList(Assets.getBlockStateArray()));

        String path = null;
        if (protocol.equals("jar") && !argList.isEmpty()) {
            path = argList.get(argList.size() - 1);
        } else if (protocol.equals("file")) {
            path = getArgument(argList, "-path");
        }
        if (path != null) {
            File file = new File(path);
            controller.openSchematic(file);
            controller.schematicChooser.setInitialDirectory(file.getParentFile());
            controller.schematicChooser.setInitialFileName(file.getName());
        }
    }
}
