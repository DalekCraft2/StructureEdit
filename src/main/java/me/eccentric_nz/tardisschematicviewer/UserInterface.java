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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Locale;

/**
 * @author eccentric_nz
 */
public class UserInterface extends JPanel {

    @Serial
    private static final long serialVersionUID = -1098962567729971976L;
    private File lastDirectory;
    private SquareButton selected;
    private int currentLayer;
    private JSONObject schematic;

    private JButton browseButton;
    private JButton loadButton;
    private JTextField fileTextField;
    private JButton editButton;
    private JButton saveButton;
    private JPanel panel;
    private JLabel schematicLabel;
    private JPanel editorPanel;
    private JPanel gridPanel;
    private JLabel blockLabel;
    private JComboBox<String> blockComboBox;
    private JLabel dataLabel;
    private JTextField dataTextField;
    private JButton plusButton;
    private JButton minusButton;
    private JLabel layerLabel;
    private JTextField blockPositionTextField;
    ActionListener actionListener = this::squareActionPerformed;
    private JLabel blockPositionLabel;
    private JTextField layerTextField;

    public UserInterface(SchematicRenderer renderer) {
        lastDirectory = new File(".");
        $$$setupUI$$$();
        gridPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (schematic != null) {
                    loadLayer();
                }
            }
        });
        browseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                choose(fileTextField, "TARDIS schematic file", "tschm");
            }
        });
        loadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                String path = fileTextField.getText();
                if (!path.isEmpty() && !path.equals("Select file")) {
                    renderer.setPath(fileTextField.getText());
                    schematic = renderer.getSchematic();
                    currentLayer = 0;
                    loadLayer();
                } else {
                    System.err.println("No file selected!");
                }
            }
        });
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                editorPanel.setVisible(!editorPanel.isVisible());
                loadLayer();
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (schematic != null) {
                    String output = renderer.getPath();
                    String input = output.substring(0, output.lastIndexOf(".tschm")) + ".json";
                    File file = new File(input);
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file), 16 * 1024)) {
                        bufferedWriter.write(schematic.toString());
                        Gzip.zip(input, output);
                        System.out.println("Schematic saved to \"" + output + "\" successfully.");
                    } catch (IOException e1) {
                        System.err.println("Error saving schematic: " + e1.getMessage());
                    } finally {
                        if (!file.delete()) {
                            System.err.println("Could not delete temporary JSON file!");
                        }
                    }
                } else {
                    System.err.println("Schematic was null!");
                }
            }
        });
        plusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentLayer < renderer.getMax() - 1) {
                    currentLayer++;
                    loadLayer();
                }
            }
        });
        minusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentLayer > 0) {
                    currentLayer--;
                    loadLayer();
                }
            }
        });
        blockComboBox.addItemListener(e -> {
            if (selected != null) {
                JSONArray input = (JSONArray) schematic.get("input");
                JSONArray level = input.getJSONArray(selected.getYCoord());
                JSONArray row = (JSONArray) level.get(selected.getXCoord());
                JSONObject column = (JSONObject) row.get(selected.getZCoord());
                String data = column.getString("data");
                String blockData = data.contains("[") ? data.substring(data.indexOf('[')) : "";
                data = "minecraft:" + blockComboBox.getSelectedItem().toString().toLowerCase() + blockData;
                column.put("data", data);
                row.put(selected.getZCoord(), column);
                level.put(selected.getXCoord(), row);
                input.put(selected.getYCoord(), level);
                schematic.put("input", input);
                renderer.setSchematic(schematic);
                loadLayer();
            } else {
                System.err.println("Schematic was null!");
            }
        });
        dataTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (selected != null) {
                    JSONArray input = (JSONArray) schematic.get("input");
                    JSONArray level = input.getJSONArray(selected.getYCoord());
                    JSONArray row = (JSONArray) level.get(selected.getXCoord());
                    JSONObject column = (JSONObject) row.get(selected.getZCoord());
                    String data = column.getString("data");
                    int nameEndIndex = data.contains("[") ? data.indexOf('[') : data.length();
                    String blockName = data.substring(data.indexOf(':') + 1, nameEndIndex).toUpperCase(Locale.ROOT);
                    data = blockName + dataTextField.getText();
                    column.put("data", data);
                    row.put(selected.getZCoord(), column);
                    level.put(selected.getXCoord(), row);
                    input.put(selected.getYCoord(), level);
                    schematic.put("input", input);
                    renderer.setSchematic(schematic);
                    loadLayer();
                } else {
                    System.err.println("Schematic was null!");
                }
            }
        });
    }

    private void createUIComponents() {
        panel = this;
        blockComboBox = new JComboBox<>();
        blockComboBox.setModel(new DefaultComboBoxModel<>(Block.strings()));
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
        gridPanel.removeAll();
        gridPanel.setLayout(null);
        gridPanel.updateUI();
        if (schematic != null) {
            layerTextField.setText(String.valueOf(currentLayer));
            JSONObject dimensions = (JSONObject) schematic.get("dimensions");
            JSONArray level = ((JSONArray) schematic.get("input")).getJSONArray(currentLayer);
            int width = dimensions.getInt("width");
            int buttonSideLength = gridPanel.getWidth() / width;
            for (int i = 0; i < width; i++) {
                JSONArray row = (JSONArray) level.get(i);
                for (int j = 0; j < width; j++) {
                    JSONObject column = (JSONObject) row.get(j);
                    String data = column.getString("data");
                    int nameEndIndex = data.contains("[") ? data.indexOf('[') : data.length();
                    String blockName = data.substring(data.indexOf(':') + 1, nameEndIndex).toUpperCase(Locale.ROOT);
                    Block block = Block.valueOf(blockName);
                    SquareButton squareButton = new SquareButton(buttonSideLength, block.getColor(), i, currentLayer, j);
                    squareButton.setText(blockName.substring(0, 1));
                    squareButton.setPreferredSize(new Dimension(buttonSideLength, buttonSideLength));
                    squareButton.setBounds(i * buttonSideLength, j * buttonSideLength, buttonSideLength, buttonSideLength);
                    squareButton.setBorder(new LineBorder(Color.BLACK));
                    squareButton.setToolTipText(data);
                    squareButton.addActionListener(actionListener);
                    gridPanel.add(squareButton);
                }
            }
        } else {
            System.err.println("Schematic was null!");
        }
    }

    private void squareActionPerformed(ActionEvent e) {
        if (selected != null) {
            // remove selected border
            selected.setBorder(new LineBorder(Color.BLACK));
        }
        selected = (SquareButton) e.getSource();
        String data = selected.getToolTipText();
        int nameEndIndex = data.contains("[") ? data.indexOf('[') : data.length();
        String blockName = data.substring(data.indexOf(':') + 1, nameEndIndex).toUpperCase(Locale.ROOT);
        String blockData = data.contains("[") ? data.substring(data.indexOf('[')) : "";
        selected.setBorder(new LineBorder(Color.RED));
        blockComboBox.setSelectedItem(blockName);
        dataTextField.setText(blockData);
        blockPositionTextField.setText(selected.getXCoord() + ", " + selected.getYCoord() + ", " + selected.getZCoord());
    }

    public JSONObject getSchematic() {
        return schematic;
    }

    public void setSchematic(JSONObject schematic) {
        this.schematic = schematic;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel.setLayout(new GridLayoutManager(3, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel.setFocusable(false);
        browseButton = new JButton();
        browseButton.setFocusable(false);
        browseButton.setText("Browse");
        panel.add(browseButton, new GridConstraints(0, 5, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loadButton = new JButton();
        loadButton.setFocusable(false);
        loadButton.setText("Load");
        panel.add(loadButton, new GridConstraints(1, 5, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileTextField = new JTextField();
        panel.add(fileTextField, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        schematicLabel = new JLabel();
        schematicLabel.setFocusable(false);
        schematicLabel.setText("TARDIS Schematic:");
        panel.add(schematicLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editButton = new JButton();
        editButton.setFocusable(false);
        editButton.setText("Edit");
        panel.add(editButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setFocusable(false);
        saveButton.setText("Save");
        panel.add(saveButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, new GridConstraints(1, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        editorPanel = new JPanel();
        editorPanel.setLayout(new GridLayoutManager(9, 6, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel.setFocusable(false);
        editorPanel.setVisible(false);
        panel.add(editorPanel, new GridConstraints(2, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        editorPanel.add(spacer2, new GridConstraints(5, 4, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        editorPanel.add(spacer3, new GridConstraints(5, 2, 4, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        blockLabel = new JLabel();
        blockLabel.setFocusable(false);
        blockLabel.setText("Block:");
        editorPanel.add(blockLabel, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockComboBox.setFocusable(false);
        editorPanel.add(blockComboBox, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataLabel = new JLabel();
        dataLabel.setFocusable(false);
        dataLabel.setText("Data:");
        editorPanel.add(dataLabel, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        gridPanel.setFocusable(false);
        editorPanel.add(gridPanel, new GridConstraints(0, 0, 9, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(500, 500), new Dimension(600, 600), 0, false));
        gridPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        dataTextField = new JTextField();
        editorPanel.add(dataTextField, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        blockPositionLabel = new JLabel();
        blockPositionLabel.setFocusable(false);
        blockPositionLabel.setText("Block Position:");
        editorPanel.add(blockPositionLabel, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPositionTextField = new JTextField();
        blockPositionTextField.setEditable(false);
        blockPositionTextField.setFocusable(false);
        editorPanel.add(blockPositionTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        plusButton = new JButton();
        plusButton.setFocusable(false);
        plusButton.setText("+");
        editorPanel.add(plusButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minusButton = new JButton();
        minusButton.setFocusable(false);
        minusButton.setText("-");
        editorPanel.add(minusButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        layerTextField = new JTextField();
        layerTextField.setEditable(false);
        layerTextField.setFocusable(false);
        editorPanel.add(layerTextField, new GridConstraints(3, 4, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        layerLabel = new JLabel();
        layerLabel.setFocusable(false);
        layerLabel.setText("Layer:");
        editorPanel.add(layerLabel, new GridConstraints(3, 2, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        editorPanel.add(spacer4, new GridConstraints(0, 5, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
