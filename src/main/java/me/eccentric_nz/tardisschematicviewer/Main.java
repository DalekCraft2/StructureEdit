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

import me.eccentric_nz.tardisschematicviewer.drawing.SchematicRenderer;
import org.json.JSONException;
import org.lwjgl.opengl.awt.GLData;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final int FRAME_WIDTH = 1024;
    public static final int FRAME_HEIGHT = 600;
    public static Path assets = null;
    public static JFrame frame;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchematicRenderer renderer = new SchematicRenderer();
            UserInterface userInterface = new UserInterface(renderer);
            userInterface.setSize(1024, 85);
            frame = new JFrame("TARDIS Schematic Viewer");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            GLData data = new GLData();
            data.majorVersion = 4;
            data.minorVersion = 6;
            data.profile = GLData.Profile.CORE;
            data.samples = 4;
            try {
                frame.setIconImage(ImageIO.read(Main.class.getClassLoader().getResourceAsStream("icon.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            frame.getContentPane().add(userInterface, BorderLayout.PAGE_START);
            frame.getContentPane().add(renderer, BorderLayout.CENTER);
            frame.setVisible(true);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Use a dedicated thread to run the stop() to ensure that the
                    // animator stops before program exits.
                    // TODO ^^^ Make that happen.
                    new Thread(() -> {
                        renderer.disposeCanvas();
                        frame.dispose();
                        System.exit(0);
                    }).start();
                }
            });

            renderer.setFocusable(true);
            renderer.requestFocus();
            renderer.setVisible(true); // TODO Get anything to render. At all.

            ArrayList<String> argList = new ArrayList<>(List.of(args));
            if (argList.contains("-assets")) {
                if (argList.size() > argList.indexOf("-assets") + 1) {
                    String assetsArg = argList.get(argList.indexOf("-assets") + 1);
                    assets = Path.of(assetsArg);
                }
            }
            if (argList.contains("-path")) {
                if (argList.size() > argList.indexOf("-path") + 1) {
                    try {
                        String path = argList.get(argList.indexOf("-path") + 1);
                        File file = new File(path);
                        userInterface.open(file);
                    } catch (JSONException e) {
                        System.err.println("Error reading schematic: " + e.getMessage());
                    }
                }
            }
        });
    }
}
