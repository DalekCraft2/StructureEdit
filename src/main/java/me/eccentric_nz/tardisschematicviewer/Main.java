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

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Main {

    public static Path assets = null;

    private Main() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchematicRenderer renderer = new SchematicRenderer();
            UserInterface userInterface = new UserInterface(renderer);

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

            renderer.run();
        });
    }
}
