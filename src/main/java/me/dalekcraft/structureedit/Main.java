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

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import me.dalekcraft.structureedit.drawing.SchematicRenderer;
import me.dalekcraft.structureedit.ui.UserInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Main {

    public static final int FRAME_WIDTH = 1024;
    public static final int FRAME_HEIGHT = 600;
    public static File assets;
    public static boolean debug;
    public static JFrame frame;

    private Main() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GLProfile profile = GLProfile.getDefault();
            GLCapabilities capabilities = new GLCapabilities(profile);
            SchematicRenderer renderer = new SchematicRenderer(capabilities);
            UserInterface userInterface = new UserInterface(renderer);
            frame = new JFrame();
            frame.add(userInterface);
            frame.setTitle("StructureEdit");
            try {
                frame.setIconImage(ImageIO.read(Main.class.getClassLoader().getResourceAsStream("icon.png")).getScaledInstance(128, 128, Image.SCALE_SMOOTH));
            } catch (IOException e) {
                e.printStackTrace();
            }
            frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
            int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
            frame.setLocation(x, y);
            frame.setVisible(true);

            // by default, an AWT Frame doesn't do anything when you click
            // the close button; this bit of code will terminate the program when
            // the window is asked to close
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            renderer.requestFocus();

            ArrayList<String> argList = new ArrayList<>(List.of(args));
            debug = argList.contains("-debug");
            String assetsArg = getArgument(argList, "-assets");
            if (assetsArg != null) {
                try {
                    assets = new File(assetsArg).getCanonicalFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String path = getArgument(argList, "-path");
            if (path != null) {
                try {
                    File file = new File(path).getCanonicalFile();
                    userInterface.open(file);
                } catch (JSONException | IOException e) {
                    System.err.println("Error reading schematic: " + e.getMessage());
                }
            }
        });
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
