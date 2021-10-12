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

import me.dalekcraft.structureedit.ui.UserInterface;
import me.dalekcraft.structureedit.util.Assets;
import me.dalekcraft.structureedit.util.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static JFrame frame;

    @Contract(value = " -> fail", pure = true)
    private Main() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.starting"));

        ArrayList<String> argList = new ArrayList<>(List.of(args));
        String levelName = getArgument(argList, "-log_level");
        if (levelName != null) {
            try {
                Level level = Level.valueOf(levelName);
                LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.log_level.setting"), level);
                // TODO Figure out why this only works in the IDE.
                Configurator.setAllLevels(LogManager.ROOT_LOGGER_NAME, level);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.log_level.invalid"), levelName);
            }
        }

        frame = new JFrame(Configuration.LANGUAGE.getProperty("ui.window.title"));
        try {
            frame.setIconImage(ImageIO.read(Main.class.getClassLoader().getResourceAsStream("icon.png")).getScaledInstance(128, 128, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.stopping"));
            }
        });
        UserInterface userInterface = new UserInterface();
        frame.setContentPane(userInterface.$$$getRootComponent$$$());
        frame.pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);

        String assetsArg = getArgument(argList, "-assets");
        Path assets = null;
        if (assetsArg != null) {
            try {
                assets = new File(assetsArg).toPath().toRealPath();
                if (Files.exists(assets)) {
                    LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.assets.setting"), assets);
                } else {
                    LOGGER.log(Level.WARN, Configuration.LANGUAGE.getProperty("log.assets.invalid"), assets);
                }
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        } else {
            LOGGER.log(Level.WARN, Configuration.LANGUAGE.getProperty("log.assets.not_set"));
        }
        Assets.setAssets(assets);
        userInterface.assetsChooser.setCurrentDirectory(assets.toFile());
        userInterface.assetsChooser.setSelectedFile(assets.toFile());
        userInterface.blockIdComboBox.removeAllItems();
        for (String blockId : Assets.getBlockStateArray()) {
            userInterface.blockIdComboBox.addItem(blockId);
        }
        String path = getArgument(argList, "-path");
        if (path != null) {
            try {
                File file = new File(path).getCanonicalFile();
                userInterface.open(file);
                userInterface.schematicChooser.setCurrentDirectory(file);
                userInterface.schematicChooser.setSelectedFile(file);
            } catch (JSONException | IOException e) {
                LOGGER.log(Level.ERROR, Configuration.LANGUAGE.getProperty("log.schematic.error_reading"), e.getMessage());
            }
        }
    }

    @Nullable
    public static String getArgument(@NotNull ArrayList<String> argList, String argumentName) {
        if (argList.contains(argumentName)) {
            if (argList.size() > argList.indexOf(argumentName) + 1) {
                return argList.get(argList.indexOf(argumentName) + 1);
            }
        }
        return null;
    }
}
