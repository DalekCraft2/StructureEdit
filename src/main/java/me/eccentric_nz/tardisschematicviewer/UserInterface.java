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

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;

/**
 * @author eccentric_nz
 */
public class UserInterface extends JPanel {

    @Serial
    private static final long serialVersionUID = -1098962567729971976L;
    private final TardisSchematicViewer viewer;
    private File lastDir = new File(".");

    private JButton browseButton;
    private JButton loadButton;
    private JTextField fileTextField;
    private JButton editLayerButton;
    private JButton saveButton;
    private JPanel panel;

    public UserInterface(TardisSchematicViewer viewer) {
        this.viewer = viewer;
        browseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                choose(fileTextField, "TARDIS Schematic", "tschm");
            }
        });
        loadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                String path = fileTextField.getText();
                if (!path.isEmpty() && !path.equals("Select file")) {
                    viewer.setPath(fileTextField.getText());
                }
            }
        });
        editLayerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Editor editor = (Editor) TardisSchematicViewer.editor;
                editor.loadLayer();
                editor.setVisible(true);
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e); // TODO Finish this.
            }
        });
    }

    /**
     * Opens a file chooser.
     *
     * @param box         the text field to target
     * @param description a String describing the file type
     * @param extension   the file extension
     */
    private void choose(JTextField box, String description, String extension) {
        JFileChooser chooser = new JFileChooser(lastDir);
        chooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showOpenDialog(this);

        if (chooser.getSelectedFile() != null) {
            box.setText(chooser.getSelectedFile().getPath());
            lastDir = chooser.getCurrentDirectory();
        }
    }

    private void createUIComponents() {
        panel = this;
    }
}
