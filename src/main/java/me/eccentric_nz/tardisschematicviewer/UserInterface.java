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

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author eccentric_nz
 */
public class UserInterface extends JPanel {

    @Serial
    private static final long serialVersionUID = -1098962567729971976L;
    private final TardisSchematicViewer viewer;
    private final List<SquareButton> buttons = new ArrayList<>();
    private File lastDirectory;
    private SquareButton selected;

    private JButton browseButton;
    private JButton loadButton;
    private JTextField fileTextField;
    private JButton editLayerButton;
    private JButton saveButton;
    private JPanel panel;
    private JLabel schematicLabel;
    private JPanel editorPanel;
    private JButton closeButton;
    private JPanel gridPanel;
    private JLabel blockLabel;
    private JComboBox<String> blockComboBox;
    private JLabel dataLabel;
    private JComboBox<String> dataComboBox;
    ActionListener actionListener = this::squareActionPerformed;

    public UserInterface(TardisSchematicViewer viewer) {
        this.viewer = viewer;
        lastDirectory = new File(".");
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
                loadLayer();
                editorPanel.setVisible(true);
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e); // TODO Finish this.
            }
        });
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                editorPanel.setVisible(false);
            }
        });
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Finish this.
            }
        });
    }

    private void createUIComponents() {
        panel = this;
        blockComboBox = new JComboBox<>();
        blockComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(Block.strings()));
    }

    /**
     * Opens a file chooser.
     *
     * @param box         the text field to target
     * @param description a String describing the file type
     * @param extension   the file extension
     */
    private void choose(JTextField box, String description, String extension) {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        chooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showOpenDialog(panel);

        if (chooser.getSelectedFile() != null) {
            box.setText(chooser.getSelectedFile().getPath());
            lastDirectory = chooser.getCurrentDirectory();
        }
    }

    public void loadLayer() {
        if (buttons.size() > 0) {
            buttons.forEach((button) -> gridPanel.remove(button));
            buttons.clear();
        }
        gridPanel.setLayout(null);
        JSONObject schematic = viewer.getSchematic();
        if (schematic != null) {
            JSONObject dimensions = (JSONObject) schematic.get("dimensions");
            int current = viewer.getHeight() - 1;
            JSONArray level = ((JSONArray) schematic.get("input")).getJSONArray(current);
            int width = dimensions.getInt("width");
            int gridWidth = gridPanel.getWidth() / width;
            for (int i = 0; i < width; i++) {
                JSONArray row = (JSONArray) level.get(i);
                for (int j = 0; j < width; j++) {
                    JSONObject column = (JSONObject) row.get(j);
                    String data = column.getString("data");
                    String blockName = data.split(":")[1].split("\\[")[0].toUpperCase(Locale.ROOT);
                    Block block = Block.valueOf(blockName);
                    SquareButton squareButton = new SquareButton(gridWidth, block.getColor());
                    squareButton.setText(blockName.substring(0, 1));
                    squareButton.setPreferredSize(new Dimension(gridWidth, gridWidth));
                    squareButton.setBounds(i * gridWidth, j * gridWidth, gridWidth, gridWidth);
                    squareButton.setBorder(new LineBorder(Color.BLACK));
                    squareButton.setToolTipText(data);
                    squareButton.addActionListener(actionListener);
                    gridPanel.add(squareButton);
                    buttons.add(squareButton);
                }
            }
        } else {
            System.out.println("schematic was null");
        }
    }

    private void squareActionPerformed(ActionEvent e) {
        if (selected != null) {
            // remove selected border
            selected.setBorder(new LineBorder(Color.BLACK));
        }
        selected = (SquareButton) e.getSource();
        int x = selected.getX() / 37;
        int z = selected.getY() / 37;
        System.out.println(x + "," + z);
        String[] split = selected.getToolTipText().split(":");
        selected.setBorder(new LineBorder(Color.RED));
        blockComboBox.setSelectedItem(split[0]);
        dataComboBox.setSelectedItem(split[1]);
    }
}
