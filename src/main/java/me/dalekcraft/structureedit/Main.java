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
        if (!AnsiConsole.isInstalled()) {
            AnsiConsole.systemInstall();
        }
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

        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel to be similar to the user's OS, if possible
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            }

            frame = new JFrame(Configuration.LANGUAGE.getProperty("ui.window.title"));
            UserInterface userInterface = new UserInterface();
            frame.add(userInterface.$$$getRootComponent$$$());
            try {
                frame.setIconImage(ImageIO.read(Main.class.getClassLoader().getResourceAsStream("icon.png")).getScaledInstance(128, 128, Image.SCALE_SMOOTH));
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }

            JMenuBar menuBar = createMenuBar(userInterface);

            frame.setJMenuBar(menuBar);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.stopping"));
                }
            });
            frame.setLocationByPlatform(true);
            frame.pack();
            frame.setVisible(true);

            String assetsArg;
            String protocol = Main.class.getResource("").getProtocol();
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
                userInterface.assetsChooser.setSelectedFile(assets.toFile());
            }
            userInterface.blockIdComboBox.setModel(new DefaultComboBoxModel<>(Assets.getBlockStateArray()));
            frame.pack();

            String path;
            if (protocol.equals("jar") && args.length > 0) {
                path = args[args.length - 1];
            } else {
                path = getArgument(argList, "-path");
            }
            if (path != null) {
                File file = new File(path);
                userInterface.open(file);
                userInterface.schematicChooser.setSelectedFile(file);
            }
        });
    }

    @NotNull
    public static JMenuBar createMenuBar(UserInterface userInterface) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(Configuration.LANGUAGE.getProperty("ui.menu_bar.file_menu.text"));
        JPopupMenu filePopup = fileMenu.getPopupMenu();
        JMenuItem openButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.file_menu.open"));
        openButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        openButton.addActionListener(e -> userInterface.openSchematic());
        filePopup.add(openButton);
        JMenuItem saveButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.file_menu.save"));
        saveButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        saveButton.addActionListener(e -> userInterface.saveSchematic());
        filePopup.add(saveButton);
        menuBar.add(fileMenu);

        JMenu settingsMenu = new JMenu(Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.text"));
        JPopupMenu settingsPopup = settingsMenu.getPopupMenu();
        JMenuItem assetsPathButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.assets_path"));
        assetsPathButton.addActionListener(e -> userInterface.selectAssets());
        settingsPopup.add(assetsPathButton);
        JMenuItem logLevelButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.log_level"));
        logLevelButton.addActionListener(e -> {
            Level level = (Level) JOptionPane.showInputDialog(frame, Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.log_level.label"), Configuration.LANGUAGE.getProperty("ui.menu_bar.settings_menu.log_level.title"), JOptionPane.PLAIN_MESSAGE, null, Level.values(), LogManager.getRootLogger().getLevel());
            if (level != null) {
                LOGGER.log(Level.INFO, Configuration.LANGUAGE.getProperty("log.log_level.setting"), level);
                Configurator.setAllLevels(LogManager.ROOT_LOGGER_NAME, level);
            }
        });
        settingsPopup.add(logLevelButton);
        menuBar.add(settingsMenu);

        JMenu helpMenu = new JMenu(Configuration.LANGUAGE.getProperty("ui.menu_bar.help_menu.text"));
        JPopupMenu helpPopup = helpMenu.getPopupMenu();
        JMenuItem controlsButton = new JMenuItem(Configuration.LANGUAGE.getProperty("ui.menu_bar.help_menu.controls"));
        controlsButton.addActionListener(e -> userInterface.showControlsDialog());
        helpPopup.add(controlsButton);
        menuBar.add(helpMenu);
        return menuBar;
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
