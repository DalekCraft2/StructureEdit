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
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
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
    private static final FileNameExtensionFilter TSCHM_FILTER = new FileNameExtensionFilter("TARDIS schematic file", "tschm");
    private static final FileNameExtensionFilter NBT_FILTER = new FileNameExtensionFilter("NBT file", "nbt");
    private File lastDirectory;
    private FileFilter lastFileFilter;
    private SquareButton selected;
    private int currentLayer;
    private Object schematic;
    private ListTag<CompoundTag> palette;
    private SchematicRenderer renderer;
    private JButton openButton;
    private JTextField fileTextField;
    private JButton editButton;
    private JButton saveButton;
    private JPanel panel;
    private JPanel editorPanel;
    private JPanel gridPanel;
    private JLabel blockLabel;
    private JComboBox<String> blockComboBox;
    private JLabel propertiesLabel;
    private JTextField propertiesTextField;
    private JButton plusButton;
    private JButton minusButton;
    private JLabel layerLabel;
    private JTextField blockPositionTextField;
    ActionListener actionListener = this::squareActionPerformed;
    private JLabel blockPositionLabel;
    private JTextField layerTextField;
    private JTextField nbtTextField;
    private JLabel nbtLabel;
    private JLabel paletteLabel;
    private JComboBox<Integer> paletteComboBox;
    private JComboBox<Integer> blockPaletteComboBox;
    private JLabel blockPaletteLabel;

    public UserInterface(SchematicRenderer renderer) {
        this.renderer = renderer;
        lastDirectory = new File(".");
        lastFileFilter = TSCHM_FILTER;
        palette = null;
        $$$setupUI$$$();
        gridPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (schematic != null) {
                    loadLayer(UserInterface.this.renderer.getPath());
                }
            }
        });
        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                choose(fileTextField);
            }
        });
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editorPanel.setVisible(!editorPanel.isVisible());
                UserInterface.this.renderer.setVisible(!editorPanel.isVisible());
                loadLayer(UserInterface.this.renderer.getPath());
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (schematic != null) {
                    if (UserInterface.this.renderer.getPath().endsWith(".tschm")) {
                        mouseClickedTschm(e);
                    } else if (UserInterface.this.renderer.getPath().endsWith(".nbt")) {
                        mouseClickedNbt(e);
                    } else {
                        System.err.println("Not a schematic file!");
                    }
                } else {
                    System.err.println("Schematic was null!");
                }
            }

            public void mouseClickedTschm(MouseEvent e) {
                String output = UserInterface.this.renderer.getPath();
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
            }

            public void mouseClickedNbt(MouseEvent e) {
                String output = UserInterface.this.renderer.getPath();
                try {
                    NBTUtil.write((NamedTag) schematic, output);
                    System.out.println("Schematic saved to \"" + output + "\" successfully.");
                } catch (IOException e1) {
                    System.err.println("Error saving schematic: " + e1.getMessage());
                }
            }
        });
        plusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentLayer < UserInterface.this.renderer.getMax() - 1) {
                    currentLayer++;
                    loadLayer(UserInterface.this.renderer.getPath());
                }
            }
        });
        minusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentLayer > 0) {
                    currentLayer--;
                    loadLayer(UserInterface.this.renderer.getPath());
                }
            }
        });
        blockComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (selected != null) {
                    if (UserInterface.this.renderer.getPath().endsWith(".tschm")) {
                        itemStateChangedTschm(e);
                    } else if (UserInterface.this.renderer.getPath().endsWith(".nbt")) {
                        itemStateChangedNbt(e);
                    } else {
                        System.err.println("Not a schematic file!");
                    }
                } else {
                    System.err.println("Schematic was null!");
                }
            }

            public void itemStateChangedTschm(ItemEvent e) {
                JSONArray input = ((JSONObject) schematic).getJSONArray("input");
                JSONArray level = input.getJSONArray(selected.getYCoord());
                JSONArray row = level.getJSONArray(selected.getXCoord());
                JSONObject column = row.getJSONObject(selected.getZCoord());
                String data = column.getString("data");
                String blockData = data.contains("[") ? data.substring(data.indexOf('[')) : "";
                data = "minecraft:" + blockComboBox.getSelectedItem().toString().toLowerCase() + blockData;
                column.put("data", data);
                row.put(selected.getZCoord(), column);
                level.put(selected.getXCoord(), row);
                input.put(selected.getYCoord(), level);
                ((JSONObject) schematic).put("input", input);
                UserInterface.this.renderer.setSchematic((JSONObject) schematic);
                loadLayer(UserInterface.this.renderer.getPath());
            }

            public void itemStateChangedNbt(ItemEvent e) {
                ListTag<CompoundTag> blocks = (ListTag<CompoundTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("blocks");
                int x = selected.getXCoord();
                int y = selected.getYCoord();
                int z = selected.getZCoord();
                for (CompoundTag blockTag : blocks) {
                    ListTag<IntTag> position = (ListTag<IntTag>) blockTag.getListTag("pos");
                    int x1 = position.get(0).asInt();
                    int y1 = position.get(1).asInt();
                    int z1 = position.get(2).asInt();
                    if (x == x1 && y == y1 && z == z1) {
                        CompoundTag paletteTag = palette.get(blockTag.getInt("state")).clone();
                        String name = "minecraft:" + blockComboBox.getSelectedItem().toString().toLowerCase();
                        paletteTag.putString("Name", name);
                        if (!palette.contains(paletteTag)) {
                            palette.add(paletteTag);
                        }
                        //                        for (int i = 0; i < palette.size(); i++) {
                        //                            boolean used = false;
                        //                            for (CompoundTag blockTag1 : blocks) {
                        //                                if (blockTag1.getInt("state") == i) {
                        //                                    used = true;
                        //                                }
                        //                            }
                        //                            if (!used) {
                        //                                palette.remove(i);
                        //                                i--;
                        //                            }
                        //                        }
                        blockTag.putInt("state", palette.indexOf(paletteTag));
                        CompoundTag compoundTag = (CompoundTag) ((NamedTag) schematic).getTag();
                        compoundTag.put("palette", palette);
                        ((NamedTag) schematic).setTag(compoundTag);
                        UserInterface.this.renderer.setSchematic((NamedTag) schematic);
                        loadLayer(UserInterface.this.renderer.getPath());
                        return;
                    }
                }
            }
        });
        propertiesTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (selected != null) {
                    if (UserInterface.this.renderer.getPath().endsWith(".tschm")) {
                        changedUpdateTschm(e);
                    } else if (UserInterface.this.renderer.getPath().endsWith(".nbt")) {
                        changedUpdateNbt(e);
                    } else {
                        System.err.println("Not a schematic file!");
                    }
                } else {
                    System.err.println("Schematic was null!");
                }
            }

            public void changedUpdateTschm(DocumentEvent e) {
                JSONArray input = ((JSONObject) schematic).getJSONArray("input");
                JSONArray level = input.getJSONArray(selected.getYCoord());
                JSONArray row = level.getJSONArray(selected.getXCoord());
                JSONObject column = row.getJSONObject(selected.getZCoord());
                String data = column.getString("data");
                int nameEndIndex = data.contains("[") ? data.indexOf('[') : data.length();
                String blockName = data.substring(data.indexOf(':') + 1, nameEndIndex).toUpperCase(Locale.ROOT);
                data = blockName + propertiesTextField.getText();
                column.put("data", data);
                row.put(selected.getZCoord(), column);
                level.put(selected.getXCoord(), row);
                input.put(selected.getYCoord(), level);
                ((JSONObject) schematic).put("input", input);
                UserInterface.this.renderer.setSchematic((JSONObject) schematic);
                loadLayer(UserInterface.this.renderer.getPath());
            }

            public void changedUpdateNbt(DocumentEvent e) {
                ListTag<CompoundTag> blocks = (ListTag<CompoundTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("blocks");
                int x = selected.getXCoord();
                int y = selected.getYCoord();
                int z = selected.getZCoord();
                for (CompoundTag blockTag : blocks) {
                    ListTag<IntTag> position = (ListTag<IntTag>) blockTag.getListTag("pos");
                    int x1 = position.get(0).asInt();
                    int y1 = position.get(1).asInt();
                    int z1 = position.get(2).asInt();
                    if (x == x1 && y == y1 && z == z1) {
                        CompoundTag paletteTag = palette.get(blockTag.getInt("state"));
                        CompoundTag properties = null;
                        try {
                            if (!propertiesTextField.getText().equals("") && propertiesTextField != null) {
                                properties = (CompoundTag) SNBTUtil.fromSNBT(propertiesTextField.getText());
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        if (properties != null) {
                            paletteTag.put("Properties", properties);
                        } else {
                            paletteTag.remove("Properties");
                        }
                        if (!palette.contains(paletteTag)) {
                            palette.add(paletteTag);
                        }
                        //                        for (int i = 0; i < palette.size(); i++) {
                        //                            boolean used = false;
                        //                            for (CompoundTag blockTag1 : blocks) {
                        //                                if (blockTag1.getInt("state") == i) {
                        //                                    used = true;
                        //                                }
                        //                            }
                        //                            if (!used) {
                        //                                palette.remove(i);
                        //                                i--;
                        //                            }
                        //                        }
                        CompoundTag compoundTag = (CompoundTag) ((NamedTag) schematic).getTag();
                        compoundTag.put("palette", palette);
                        ((NamedTag) schematic).setTag(compoundTag);
                        UserInterface.this.renderer.setSchematic((NamedTag) schematic);
                        loadLayer(UserInterface.this.renderer.getPath());
                        return;
                    }
                }
            }
        });
        nbtTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (selected != null) {
                    if (UserInterface.this.renderer.getPath().endsWith(".nbt")) {
                        changedUpdateNbt(e);
                    } else {
                        System.err.println("Not a schematic file!");
                    }
                } else {
                    System.err.println("Schematic was null!");
                }
            }

            public void changedUpdateNbt(DocumentEvent e) {
                ListTag<CompoundTag> blocks = (ListTag<CompoundTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("blocks");
                int x = selected.getXCoord();
                int y = selected.getYCoord();
                int z = selected.getZCoord();
                for (CompoundTag blockTag : blocks) {
                    ListTag<IntTag> position = (ListTag<IntTag>) blockTag.getListTag("pos");
                    int x1 = position.get(0).asInt();
                    int y1 = position.get(1).asInt();
                    int z1 = position.get(2).asInt();
                    if (x == x1 && y == y1 && z == z1) {
                        CompoundTag blockTagClone = blockTag.clone();
                        CompoundTag nbt = null;
                        try {
                            if (!nbtTextField.getText().equals("") && nbtTextField != null) {
                                nbt = (CompoundTag) SNBTUtil.fromSNBT(nbtTextField.getText());
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        if (nbt != null) {
                            blockTagClone.put("nbt", nbt);
                        } else {
                            blockTagClone.remove("nbt");
                        }
                        CompoundTag compoundTag = (CompoundTag) ((NamedTag) schematic).getTag();
                        blocks.set(blocks.indexOf(blockTag), blockTagClone);
                        compoundTag.put("blocks", blocks);
                        ((NamedTag) schematic).setTag(compoundTag);
                        UserInterface.this.renderer.setSchematic((NamedTag) schematic);
                        loadLayer(UserInterface.this.renderer.getPath());
                        return;
                    }
                }
            }
        });
        paletteComboBox.addItemListener(e -> {
            if (this.renderer.getPath().endsWith(".nbt")) {
                palette = (ListTag<CompoundTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("palettes").get(Integer.parseInt(paletteComboBox.getSelectedItem().toString()));
                this.renderer.setPalette(palette);
                loadLayer(this.renderer.getPath());
            } else {
                System.err.println("Not a schematic file!");
            }
        });
        blockPaletteComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (selected != null) {
                    if (UserInterface.this.renderer.getPath().endsWith(".tschm")) {
                        itemStateChangedTschm(e);
                    } else if (UserInterface.this.renderer.getPath().endsWith(".nbt")) {
                        itemStateChangedNbt(e);
                    } else {
                        System.err.println("Not a schematic file!");
                    }
                } else {
                    System.err.println("Schematic was null!");
                }
            }

            public void itemStateChangedTschm(ItemEvent e) {
                JSONArray input = ((JSONObject) schematic).getJSONArray("input");
                JSONArray level = input.getJSONArray(selected.getYCoord());
                JSONArray row = level.getJSONArray(selected.getXCoord());
                JSONObject column = row.getJSONObject(selected.getZCoord());
                String data = column.getString("data");
                String blockData = data.contains("[") ? data.substring(data.indexOf('[')) : "";
                data = "minecraft:" + blockComboBox.getSelectedItem().toString().toLowerCase() + blockData;
                column.put("data", data);
                row.put(selected.getZCoord(), column);
                level.put(selected.getXCoord(), row);
                input.put(selected.getYCoord(), level);
                ((JSONObject) schematic).put("input", input);
                UserInterface.this.renderer.setSchematic((JSONObject) schematic);
                loadLayer(UserInterface.this.renderer.getPath());
            }

            public void itemStateChangedNbt(ItemEvent e) {
                ListTag<CompoundTag> blocks = (ListTag<CompoundTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("blocks");
                int x = selected.getXCoord();
                int y = selected.getYCoord();
                int z = selected.getZCoord();
                for (CompoundTag blockTag : blocks) {
                    ListTag<IntTag> position = (ListTag<IntTag>) blockTag.getListTag("pos");
                    int x1 = position.get(0).asInt();
                    int y1 = position.get(1).asInt();
                    int z1 = position.get(2).asInt();
                    if (x == x1 && y == y1 && z == z1) {
                        CompoundTag paletteTag = palette.get(blockTag.getInt("state")).clone();
                        String name = "minecraft:" + blockComboBox.getSelectedItem().toString().toLowerCase();
                        paletteTag.putString("Name", name);
                        if (!palette.contains(paletteTag)) {
                            palette.add(paletteTag);
                        }
                        //                        for (int i = 0; i < palette.size(); i++) {
                        //                            boolean used = false;
                        //                            for (CompoundTag blockTag1 : blocks) {
                        //                                if (blockTag1.getInt("state") == i) {
                        //                                    used = true;
                        //                                }
                        //                            }
                        //                            if (!used) {
                        //                                palette.remove(i);
                        //                                i--;
                        //                            }
                        //                        }
                        blockTag.putInt("state", palette.indexOf(paletteTag));
                        CompoundTag compoundTag = (CompoundTag) ((NamedTag) schematic).getTag();
                        compoundTag.put("palette", palette);
                        ((NamedTag) schematic).setTag(compoundTag);
                        UserInterface.this.renderer.setSchematic((NamedTag) schematic);
                        loadLayer(UserInterface.this.renderer.getPath());
                        return;
                    }
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
     * @param box the text field to target
     */
    public void choose(JTextField box) {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        chooser.addChoosableFileFilter(TSCHM_FILTER);
        chooser.addChoosableFileFilter(NBT_FILTER);
        chooser.setFileFilter(lastFileFilter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showOpenDialog(panel);
        if (chooser.getSelectedFile() != null) {
            box.setText(chooser.getSelectedFile().getPath());
            lastDirectory = chooser.getCurrentDirectory();
            lastFileFilter = chooser.getFileFilter();
            choose(chooser.getSelectedFile().getPath());
        }
    }

    public void choose(String path) {
        if (!path.isEmpty()) {
            try {
                setPath(path);
                renderer.setPath(path);
                schematic = renderer.getSchematic();
                currentLayer = 0;
                if (path.endsWith(".nbt")) {
                    nbtLabel.setVisible(true);
                    nbtTextField.setVisible(true);
                    if (((CompoundTag) ((NamedTag) schematic).getTag()).containsKey("palettes")) {
                        int palettesSize = ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("palettes").size();
                        Integer[] palettes = new Integer[palettesSize];
                        for (int i = 0; i < palettesSize; i++) {
                            palettes[i] = i;
                        }
                        paletteComboBox.setModel(new DefaultComboBoxModel<>(palettes));
                        blockPaletteComboBox.setModel(new DefaultComboBoxModel<>(palettes));
                        paletteComboBox.setSelectedItem("0");
                        palette = (ListTag<CompoundTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("palettes").get(Integer.parseInt(paletteComboBox.getSelectedItem().toString()));
                        renderer.setPalette(palette);
                        paletteLabel.setVisible(true);
                        blockPaletteLabel.setVisible(true);
                        paletteComboBox.setVisible(true);
                        blockPaletteComboBox.setVisible(true);
                    } else {
                        palette = (ListTag<CompoundTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("palette");
                        renderer.setPalette(palette);
                        paletteLabel.setVisible(false);
                        blockPaletteLabel.setVisible(false);
                        paletteComboBox.setVisible(false);
                        blockPaletteComboBox.setVisible(false);
                    }
                }
                loadLayer(renderer.getPath());
            } catch (IOException | JSONException e1) {
                System.err.println("Error reading schematic: " + e1.getMessage());
            }
        } else {
            System.err.println("No file selected!");
        }
    }

    public void loadLayer(String path) {
        if (path.endsWith(".tschm")) {
            loadLayerTschm();
        } else if (path.endsWith(".nbt")) {
            loadLayerNbt();
        } else {
            System.err.println("Not a schematic file!");
        }
    }

    public void loadLayerTschm() {
        gridPanel.removeAll();
        gridPanel.setLayout(null);
        gridPanel.updateUI();
        if (schematic != null) {
            layerTextField.setText(String.valueOf(currentLayer));
            JSONObject dimensions = ((JSONObject) schematic).getJSONObject("dimensions");
            JSONArray level = ((JSONObject) schematic).getJSONArray("input").getJSONArray(currentLayer);
            int width = dimensions.getInt("width");
            int buttonSideLength = gridPanel.getWidth() / width;
            for (int i = 0; i < width; i++) {
                JSONArray row = level.getJSONArray(i);
                for (int j = 0; j < width; j++) {
                    JSONObject column = row.getJSONObject(j);
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

    public void loadLayerNbt() {
        gridPanel.removeAll();
        gridPanel.setLayout(null);
        gridPanel.updateUI();
        if (schematic != null) {
            layerTextField.setText(String.valueOf(currentLayer));
            ListTag<IntTag> size = (ListTag<IntTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("size");
            ListTag<CompoundTag> blocks = (ListTag<CompoundTag>) ((CompoundTag) ((NamedTag) schematic).getTag()).getListTag("blocks");
            int width = size.get(2).asInt();
            int buttonSideLength = gridPanel.getWidth() / width;
            for (CompoundTag blockTag : blocks) {
                ListTag<IntTag> position = (ListTag<IntTag>) blockTag.getListTag("pos");
                int x = position.get(0).asInt();
                int y = position.get(1).asInt();
                int z = position.get(2).asInt();
                String namespacedBlockName = palette.get(blockTag.getInt("state")).getString("Name");
                String blockName = namespacedBlockName.substring(namespacedBlockName.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                Block block = Block.valueOf(blockName);
                CompoundTag properties = palette.get(blockTag.getInt("state")).getCompoundTag("Properties");
                String propertiesString = null;
                try {
                    propertiesString = properties == null ? "" : SNBTUtil.toSNBT(properties);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (y == currentLayer) {
                    SquareButton squareButton = new SquareButton(buttonSideLength, block.getColor(), x, y, z);
                    squareButton.setText(blockName.substring(0, 1));
                    squareButton.setPreferredSize(new Dimension(buttonSideLength, buttonSideLength));
                    squareButton.setBounds(x * buttonSideLength, z * buttonSideLength, buttonSideLength, buttonSideLength);
                    squareButton.setBorder(new LineBorder(Color.BLACK));
                    squareButton.setToolTipText(namespacedBlockName + propertiesString);
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
        if (renderer.getPath().endsWith(".tschm")) {
            squareActionPerformedTschm(e);
        } else if (renderer.getPath().endsWith(".nbt")) {
            squareActionPerformedNbt(e);
        } else {
            System.err.println("Not a schematic file!");
        }
    }

    private void squareActionPerformedTschm(ActionEvent e) {
        String data = selected.getToolTipText();
        int nameEndIndex = data.contains("[") ? data.indexOf('[') : data.length();
        String blockName = data.substring(data.indexOf(':') + 1, nameEndIndex).toUpperCase(Locale.ROOT);
        String blockData = data.contains("[") ? data.substring(data.indexOf('[')) : "";
        selected.setBorder(new LineBorder(Color.RED));
        blockComboBox.setSelectedItem(blockName);
        propertiesTextField.setText(blockData);
        blockPositionTextField.setText(selected.getXCoord() + ", " + selected.getYCoord() + ", " + selected.getZCoord());
    }

    private void squareActionPerformedNbt(ActionEvent e) {

        String data = selected.getToolTipText();
        int nameEndIndex = data.contains("{") ? data.indexOf('{') : data.length();
        String blockName = data.substring(data.indexOf(':') + 1, nameEndIndex).toUpperCase(Locale.ROOT);
        String blockData = data.contains("{") ? data.substring(data.indexOf('{')) : "";
        selected.setBorder(new LineBorder(Color.RED));
        blockComboBox.setSelectedItem(blockName);
        propertiesTextField.setText(blockData);
        blockPositionTextField.setText(selected.getXCoord() + ", " + selected.getYCoord() + ", " + selected.getZCoord());
    }

    public Object getSchematic() {
        return schematic;
    }

    public void setSchematic(Object schematic) {
        this.schematic = schematic;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
    }

    public void setPath(String path) {
        fileTextField.setText(path);
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
        panel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel = new JPanel();
        editorPanel.setLayout(new GridLayoutManager(9, 5, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel.setVisible(true);
        panel.add(editorPanel, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        editorPanel.add(spacer1, new GridConstraints(8, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        editorPanel.add(spacer2, new GridConstraints(8, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        blockLabel = new JLabel();
        blockLabel.setText("Block:");
        editorPanel.add(blockLabel, new GridConstraints(4, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editorPanel.add(blockComboBox, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        propertiesLabel = new JLabel();
        propertiesLabel.setText("Properties:");
        editorPanel.add(propertiesLabel, new GridConstraints(5, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel.add(gridPanel, new GridConstraints(0, 0, 9, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(500, 500), new Dimension(600, 600), 0, false));
        gridPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        propertiesTextField = new JTextField();
        editorPanel.add(propertiesTextField, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        blockPositionLabel = new JLabel();
        blockPositionLabel.setText("Block Position:");
        editorPanel.add(blockPositionLabel, new GridConstraints(3, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(77, 33), null, 0, false));
        blockPositionTextField = new JTextField();
        blockPositionTextField.setEditable(false);
        editorPanel.add(blockPositionTextField, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 33), null, 0, false));
        layerLabel = new JLabel();
        layerLabel.setText("Layer:");
        editorPanel.add(layerLabel, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        plusButton = new JButton();
        plusButton.setText("+");
        editorPanel.add(plusButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minusButton = new JButton();
        minusButton.setText("-");
        editorPanel.add(minusButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        layerTextField = new JTextField();
        layerTextField.setEditable(false);
        editorPanel.add(layerTextField, new GridConstraints(0, 4, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nbtTextField = new JTextField();
        nbtTextField.setVisible(false);
        editorPanel.add(nbtTextField, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        paletteLabel = new JLabel();
        paletteLabel.setText("Palette:");
        paletteLabel.setVisible(false);
        editorPanel.add(paletteLabel, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paletteComboBox = new JComboBox();
        paletteComboBox.setVisible(false);
        editorPanel.add(paletteComboBox, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nbtLabel = new JLabel();
        nbtLabel.setText("NBT:");
        nbtLabel.setVisible(false);
        editorPanel.add(nbtLabel, new GridConstraints(6, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPaletteLabel = new JLabel();
        blockPaletteLabel.setText("Block Palette:");
        blockPaletteLabel.setVisible(false);
        editorPanel.add(blockPaletteLabel, new GridConstraints(7, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPaletteComboBox = new JComboBox();
        blockPaletteComboBox.setVisible(false);
        editorPanel.add(blockPaletteComboBox, new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileTextField = new JTextField();
        fileTextField.setEditable(false);
        panel.add(fileTextField, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        panel.add(openButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editButton = new JButton();
        editButton.setText("Edit");
        panel.add(editButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel.add(saveButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel.add(spacer3, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
