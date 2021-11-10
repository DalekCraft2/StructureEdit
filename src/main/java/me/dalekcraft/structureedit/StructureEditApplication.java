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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StructureEditApplication extends Application {

    private static final Logger LOGGER = LogManager.getLogger(StructureEditApplication.class);
    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;

        if (!AnsiConsole.isInstalled()) {
            AnsiConsole.systemInstall();
        }
        LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.starting"));

        Parameters parameters = getParameters();
        Map<String, String> named = parameters.getNamed();
        List<String> raw = parameters.getRaw();

        String levelName = named.get("log_level");
        if (levelName != null) {
            try {
                Level level = Level.valueOf(levelName);
                LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.log_level.setting"), level);
                // TODO Figure out why this only works in the IDE.
                Configurator.setAllLevels(LogManager.ROOT_LOGGER_NAME, level);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARN, Configuration.LANGUAGE.getString("log.log_level.invalid"), levelName);
            }
        }

        String protocol = Objects.requireNonNull(getClass().getResource("")).getProtocol();

        String assetsArg;
        if (protocol.equals("jar")) {
            assetsArg = Configuration.CONFIG.getProperty("assets_path");
        } else {
            assetsArg = named.get("assets");
        }
        Path assets = null;
        if (assetsArg != null && !assetsArg.equals("")) {
            try {
                assets = Path.of(assetsArg).toRealPath();
                if (!Files.exists(assets)) {
                    LOGGER.log(Level.WARN, Configuration.LANGUAGE.getString("log.assets.invalid"), assets);
                }
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        } else {
            LOGGER.log(Level.WARN, Configuration.LANGUAGE.getString("log.assets.not_set"));
        }
        Assets.setAssets(assets);

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")))); // Use Objects.requireNonNull() because it should never be null
        stage.setOnCloseRequest(event -> LOGGER.log(Level.INFO, Configuration.LANGUAGE.getString("log.stopping")));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene.fxml"));
        fxmlLoader.setResources(Configuration.LANGUAGE);
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle(Configuration.LANGUAGE.getString("ui.window.title"));
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller controller = fxmlLoader.getController();

        String path = null;
        if (protocol.equals("jar") && !raw.isEmpty()) {
            path = raw.get(raw.size() - 1);
        } else if (protocol.equals("file")) {
            path = named.get("path");
        }
        if (path != null) {
            File file = new File(path);
            controller.schematicChooser.setInitialDirectory(file.getParentFile());
            controller.schematicChooser.setInitialFileName(file.getName());
            controller.openSchematic(file);
        }
    }
}
