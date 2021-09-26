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
package me.dalekcraft.structureedit.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import me.dalekcraft.structureedit.Main;
import me.dalekcraft.structureedit.drawing.Block;
import me.dalekcraft.structureedit.drawing.SchematicRenderer;
import me.dalekcraft.structureedit.schematic.NbtStructure;
import me.dalekcraft.structureedit.schematic.Schematic;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.Arrays;
import java.util.Locale;

import static me.dalekcraft.structureedit.schematic.Schematic.*;

/**
 * @author eccentric_nz
 */
public class UserInterface extends JPanel {

    @Serial
    private static final long serialVersionUID = -1098962567729971976L;
    private static final FileNameExtensionFilter FILTER_NBT = new FileNameExtensionFilter("NBT structure file", EXTENSION_NBT);
    private static final FileNameExtensionFilter FILTER_MCEDIT = new FileNameExtensionFilter("MCEdit schematic file", EXTENSION_MCEDIT);
    private static final FileNameExtensionFilter FILTER_SPONGE = new FileNameExtensionFilter("Sponge schematic file", EXTENSION_SPONGE);
    private static final FileNameExtensionFilter FILTER_TARDIS = new FileNameExtensionFilter("TARDIS schematic file", EXTENSION_TARDIS);
    private final SchematicRenderer renderer;
    private final JFileChooser schematicChooser;
    private final JFileChooser assetsChooser;
    private SquareButton selected;
    private int currentLayer;
    private Schematic schematic;
    private ListTag<CompoundTag> palette;
    private JPanel panel;
    private JPanel rawRenderer;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu settingsMenu;
    private JPanel editorPanel;
    private JPanel gridPanel;
    private JLabel blockLabel;
    private JComboBox<Block> blockComboBox;
    private JLabel propertiesLabel;
    private JFormattedTextField propertiesTextField;
    private JLabel layerLabel;
    private JSpinner layerSpinner;
    private JLabel blockPositionLabel;
    private JTextField blockPositionTextField;
    private JLabel nbtLabel;
    private JFormattedTextField nbtTextField;
    private JLabel paletteLabel;
    private JSpinner paletteSpinner;
    private JLabel blockPaletteLabel;
    private JSpinner blockPaletteSpinner;
    private final ActionListener actionListener = this::squareActionPerformed;
    private JLabel sizeLabel;
    private JTextField sizeTextField;

    {
        schematicChooser = new JFileChooser();
        schematicChooser.addChoosableFileFilter(FILTER_NBT);
        schematicChooser.addChoosableFileFilter(FILTER_MCEDIT);
        schematicChooser.addChoosableFileFilter(FILTER_SPONGE);
        schematicChooser.addChoosableFileFilter(FILTER_TARDIS);
        schematicChooser.setFileFilter(FILTER_NBT);
        try {
            schematicChooser.setCurrentDirectory(new File(".").getCanonicalFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    {
        assetsChooser = new JFileChooser();
        //        assetsChooser.setCurrentDirectory(Assets.getAssets());
    }

    public UserInterface(SchematicRenderer renderer) {
        this.renderer = renderer;
        $$$setupUI$$$();
        gridPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (schematic != null) {
                    loadLayer();
                }
            }
        });

        JPopupMenu filePopup = fileMenu.getPopupMenu();
        JMenuItem openButton = new JMenuItem("Open");
        openButton.addActionListener(e -> {
            renderer.pause();
            schematicChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = schematicChooser.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION && schematicChooser.getSelectedFile() != null) {
                File file;
                try {
                    file = schematicChooser.getSelectedFile().getCanonicalFile();
                    open(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            renderer.resume();
        });
        filePopup.add(openButton);
        JMenuItem saveButton = new JMenuItem("Save");
        saveButton.addActionListener(e -> {
            if (schematic != null) {
                renderer.pause();
                schematicChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = schematicChooser.showSaveDialog(panel);
                if (result == JFileChooser.APPROVE_OPTION && schematicChooser.getSelectedFile() != null) {
                    try {
                        File file = schematicChooser.getSelectedFile().getCanonicalFile();
                        System.out.println("Saving " + file + " ...");
                        schematic.saveTo(file);
                        System.out.println("Schematic saved to " + file + " successfully.");
                        Main.frame.setTitle(file.getName() + " - StructureEdit");
                    } catch (IOException e1) {
                        System.err.println("Error saving schematic: " + e1.getMessage());
                    }
                }
                renderer.resume();
            } else {
                System.err.println("Schematic was null!");
            }
        });
        filePopup.add(saveButton);

        JPopupMenu settingsPopup = settingsMenu.getPopupMenu();
        JMenuItem assetsButton = new JMenuItem("Assets Path");
        assetsButton.addActionListener(e -> {
            renderer.pause();
            //            assetsChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //            int result = assetsChooser.showOpenDialog(panel);
            //            if (result == JFileChooser.APPROVE_OPTION && assetsChooser.getSelectedFile() != null) {
            //                File directory;
            //                try {
            //                    directory = assetsChooser.getSelectedFile().getCanonicalFile();
            //                    Assets.setAssets(directory);
            //                } catch (IOException e1) {
            //                    e1.printStackTrace();
            //                }
            //            }
            renderer.resume();
        });
        settingsPopup.add(assetsButton);

        layerSpinner.addChangeListener(e -> {
            if (schematic != null) {
                int value = (int) layerSpinner.getValue();
                if (value >= 0 && value < schematic.getSize()[1] - 1) {
                    currentLayer = value;
                } else if (value < 0) {
                    currentLayer = 0;
                    layerSpinner.setValue(currentLayer);
                } else if (value >= schematic.getSize()[1] - 1) {
                    currentLayer = schematic.getSize()[1] - 1;
                    layerSpinner.setValue(currentLayer);
                }
                loadLayer();
            }
        });
        blockComboBox.addItemListener(e -> {
            if (schematic != null && selected != null) {
                int[] position = selected.getPosition();
                Object block = schematic.getBlock(position[0], position[1], position[2]);
                String blockId = "minecraft:" + blockComboBox.getSelectedItem().toString().toLowerCase();
                if (schematic instanceof NbtStructure nbtStructure && nbtStructure.hasPaletteList()) {
                    nbtStructure.setBlockId(block, blockId, palette);
                } else {
                    schematic.setBlockId(block, blockId);
                }
                loadLayer();
            }
        });
        propertiesTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (schematic != null && selected != null) {
                    int[] position = selected.getPosition();
                    Object block = schematic.getBlock(position[0], position[1], position[2]);
                    String propertiesString = propertiesTextField.getText().isEmpty() || propertiesTextField.getText().equals("[]") ? "" : propertiesTextField.getText();
                    try {
                        if (schematic instanceof NbtStructure nbtStructure && nbtStructure.hasPaletteList()) {
                            nbtStructure.setBlockPropertiesAsString(block, propertiesString, palette);
                        } else {
                            schematic.setBlockPropertiesAsString(block, propertiesString);
                        }
                        propertiesTextField.setForeground(Color.BLACK);
                    } catch (IOException e1) {
                        propertiesTextField.setForeground(Color.RED);
                    }
                    loadLayer();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        nbtTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (schematic != null && schematic instanceof NbtStructure nbtStructure && selected != null) {
                    int[] position = selected.getPosition();
                    try {
                        nbtStructure.setBlockSnbt(nbtStructure.getBlock(position[0], position[1], position[2]), nbtTextField.getText());
                        nbtTextField.setForeground(Color.BLACK);
                    } catch (IOException e1) {
                        nbtTextField.setForeground(Color.RED);
                    }
                    loadLayer();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        paletteSpinner.addChangeListener(e -> {
            if (schematic != null && schematic instanceof NbtStructure nbtStructure) {
                if (nbtStructure.hasPaletteList()) {
                    palette = nbtStructure.getPaletteListEntry((Integer) paletteSpinner.getValue());
                } else {
                    palette = nbtStructure.getPalette();
                }
            }
            this.renderer.setPalette(palette);
            updateSelected();
            loadLayer();
        });
        // TODO Blockbench-style palette editor, with a list of palettes and palette IDs?
        blockPaletteSpinner.addChangeListener(e -> {
            if (schematic != null && schematic instanceof NbtStructure nbtStructure && selected != null) {
                int[] position = selected.getPosition();
                CompoundTag block = nbtStructure.getBlock(position[0], position[1], position[2]);
                if (block != null) {
                    nbtStructure.setBlockState(block, (Integer) blockPaletteSpinner.getValue());
                }
                updateSelected();
                loadLayer();
            }
        });
    }

    public void open(@NotNull File file) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Loading " + file + " ...");
            try {
                schematic = Schematic.openFrom(file);
                renderer.setSchematic(schematic);
                selected = null;
                sizeTextField.setText(null);
                paletteSpinner.setValue(0);
                paletteSpinner.setEnabled(false);
                blockPositionTextField.setText(null);
                blockPositionTextField.setEnabled(false);
                blockComboBox.setSelectedItem(null);
                blockComboBox.setEnabled(false);
                propertiesTextField.setText(null);
                propertiesTextField.setEnabled(false);
                nbtTextField.setText(null);
                nbtTextField.setEnabled(false);
                blockPaletteSpinner.setValue(0);
                blockPaletteSpinner.setEnabled(false);
                currentLayer = 0;
                if (schematic != null) {
                    int[] size = schematic.getSize();
                    SpinnerModel layerModel = new SpinnerNumberModel(0, 0, size[1], 1);
                    layerSpinner.setModel(layerModel);
                    if (schematic instanceof NbtStructure nbtStructure) {
                        if (nbtStructure.hasPaletteList()) {
                            int palettesSize = nbtStructure.getPaletteList().size();
                            SpinnerModel paletteModel = new SpinnerNumberModel(0, 0, palettesSize - 1, 1);
                            paletteSpinner.setEnabled(true);
                            paletteSpinner.setModel(paletteModel);
                            palette = nbtStructure.getPaletteListEntry(0);
                        } else {
                            palette = nbtStructure.getPalette();
                        }
                        renderer.setPalette(palette);
                        int paletteSize = palette.size();
                        SpinnerModel blockPaletteModel = new SpinnerNumberModel(0, 0, paletteSize - 1, 1);
                        blockPaletteSpinner.setModel(blockPaletteModel);
                    } else {
                        paletteSpinner.setEnabled(false);
                    }
                    loadLayer();
                    System.out.println("Loaded " + file + " successfully.");
                    Main.frame.setTitle(file.getName() + " - StructureEdit");
                } else {
                    System.err.println("Not a schematic file!");
                }
            } catch (IOException | JSONException e) {
                System.err.println("Error reading schematic: " + e.getMessage());
                Main.frame.setTitle("StructureEdit");
            }
        });
    }

    // TODO Make the editor built into the 3D view instead of being a layer-by-layer editor.
    public void loadLayer() {
        if (schematic != null) {
            gridPanel.removeAll();
            gridPanel.setLayout(null);
            gridPanel.updateUI();
            layerSpinner.setValue(currentLayer);
            int[] size = schematic.getSize();
            sizeTextField.setText(Arrays.toString(size));
            int buttonSideLength = Math.min(gridPanel.getWidth() / size[0], gridPanel.getHeight() / size[2]);
            for (int x = 0; x < size[0]; x++) {
                for (int z = 0; z < size[2]; z++) {
                    Object block = schematic.getBlock(x, currentLayer, z);
                    if (block != null) {
                        String blockId;
                        if (schematic instanceof NbtStructure nbtStructure && nbtStructure.hasPaletteList()) {
                            blockId = nbtStructure.getBlockId(block, palette);
                        } else {
                            blockId = schematic.getBlockId(block);
                        }
                        String blockName = blockId.substring(blockId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                        Block blockEnum = Block.valueOf(blockName);
                        SquareButton squareButton = new SquareButton(blockEnum, x, currentLayer, z);
                        squareButton.setBounds(x * buttonSideLength, z * buttonSideLength, buttonSideLength, buttonSideLength);
                        Font font = squareButton.getFont();
                        squareButton.setFont(new Font(font.getFontName(), font.getStyle(), buttonSideLength));
                        squareButton.addActionListener(actionListener);
                        gridPanel.add(squareButton);
                        if (selected != null) {
                            int[] position = selected.getPosition();
                            if (Arrays.equals(position, new int[]{x, currentLayer, z})) {
                                // Set selected tile's border color to red
                                squareButton.setBorder(new LineBorder(Color.RED));
                            }
                        }
                    }
                }
            }
        } else {
            System.err.println("Schematic was null!");
        }
    }

    private void squareActionPerformed(@NotNull ActionEvent e) {
        selected = (SquareButton) e.getSource();
        int[] position = selected.getPosition();

        Object block = schematic.getBlock(position[0], position[1], position[2]);

        String blockId;
        String properties;
        if (schematic instanceof NbtStructure nbtStructure && nbtStructure.hasPaletteList()) {
            blockId = nbtStructure.getBlockId(block, palette);
            properties = nbtStructure.getBlockPropertiesAsString(block, palette);
        } else {
            blockId = schematic.getBlockId(block);
            properties = schematic.getBlockPropertiesAsString(block);
        }

        Block blockEnum = Block.getFromId(blockId);
        blockComboBox.setEnabled(true);
        blockComboBox.setSelectedItem(blockEnum);

        propertiesTextField.setEnabled(true);
        propertiesTextField.setText(properties);
        propertiesTextField.setForeground(Color.BLACK);

        try {
            String snbt = schematic.getBlockSnbt(block);
            nbtTextField.setText(snbt);
            nbtTextField.setEnabled(true);
        } catch (UnsupportedOperationException e1) {
            nbtTextField.setEnabled(false);
            nbtTextField.setText(null);
        }
        nbtTextField.setForeground(Color.BLACK);

        blockPositionTextField.setEnabled(true);
        blockPositionTextField.setText(Arrays.toString(selected.getPosition()));

        if (schematic instanceof NbtStructure nbtStructure) {
            int blockState = nbtStructure.getBlockState((CompoundTag) block);
            blockPaletteSpinner.setEnabled(true);
            blockPaletteSpinner.setValue(blockState);
        } else {
            blockPaletteSpinner.setEnabled(false);
        }
    }

    public void updateSelected() {
        if (selected != null) {
            int[] position = selected.getPosition();

            Object block = schematic.getBlock(position[0], position[1], position[2]);

            String blockId;
            String properties;
            if (schematic instanceof NbtStructure nbtStructure && nbtStructure.hasPaletteList()) {
                blockId = nbtStructure.getBlockId(block, palette);
                properties = nbtStructure.getBlockPropertiesAsString(block, palette);
            } else {
                blockId = schematic.getBlockId(block);
                properties = schematic.getBlockPropertiesAsString(block);
            }

            Block blockEnum = Block.getFromId(blockId);
            blockComboBox.setSelectedItem(blockEnum);

            propertiesTextField.setText(properties);

            try {
                String snbt = schematic.getBlockSnbt(block);
                nbtTextField.setText(snbt);
            } catch (UnsupportedOperationException e) {
                nbtTextField.setText(null);
            }
        }
    }

    private void createUIComponents() {
        panel = this;
        rawRenderer = renderer;
        blockComboBox = new JComboBox<>();
        blockComboBox.setModel(new DefaultComboBoxModel<>(Block.values()));
        blockComboBox.setSelectedItem(null);
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
        panel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel = new JPanel();
        editorPanel.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(editorPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        blockLabel = new JLabel();
        blockLabel.setText("Block:");
        editorPanel.add(blockLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockComboBox.setToolTipText("The ID of the selected block");
        editorPanel.add(blockComboBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        propertiesLabel = new JLabel();
        propertiesLabel.setText("Properties:");
        editorPanel.add(propertiesLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        propertiesTextField = new JFormattedTextField();
        propertiesTextField.setToolTipText("The properties of the selected block");
        editorPanel.add(propertiesTextField, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        blockPositionLabel = new JLabel();
        blockPositionLabel.setText("Block Position:");
        editorPanel.add(blockPositionLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(77, 33), null, 0, false));
        blockPositionTextField = new JTextField();
        blockPositionTextField.setEditable(false);
        blockPositionTextField.setText("");
        blockPositionTextField.setToolTipText("The position of the selected block, ordered as [x, y, z]");
        editorPanel.add(blockPositionTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 33), null, 0, false));
        layerLabel = new JLabel();
        layerLabel.setText("Layer:");
        editorPanel.add(layerLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nbtTextField = new JFormattedTextField();
        nbtTextField.setToolTipText("The NBT of the selected block");
        editorPanel.add(nbtTextField, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        paletteLabel = new JLabel();
        paletteLabel.setText("Palette:");
        editorPanel.add(paletteLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paletteSpinner = new JSpinner();
        paletteSpinner.setToolTipText("The index of the active palette");
        editorPanel.add(paletteSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nbtLabel = new JLabel();
        nbtLabel.setText("NBT:");
        editorPanel.add(nbtLabel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel.add(gridPanel, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        sizeLabel = new JLabel();
        sizeLabel.setText("Schematic Size:");
        editorPanel.add(sizeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sizeTextField = new JTextField();
        sizeTextField.setEditable(false);
        sizeTextField.setToolTipText("The dimensions of the schematic, ordered as [sizeX, sizeY, sizeZ]");
        editorPanel.add(sizeTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 33), null, 0, false));
        blockPaletteLabel = new JLabel();
        blockPaletteLabel.setText("Block Palette:");
        editorPanel.add(blockPaletteLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPaletteSpinner = new JSpinner();
        blockPaletteSpinner.setToolTipText("The index of the selected block's state in the active palette");
        editorPanel.add(blockPaletteSpinner, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        layerSpinner = new JSpinner();
        layerSpinner.setToolTipText("The currently viewed layer");
        editorPanel.add(layerSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(rawRenderer, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        menuBar = new JMenuBar();
        menuBar.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(menuBar, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fileMenu = new JMenu();
        fileMenu.setText("File");
        menuBar.add(fileMenu, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsMenu = new JMenu();
        settingsMenu.setText("Settings");
        menuBar.add(settingsMenu, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockLabel.setLabelFor(blockComboBox);
        propertiesLabel.setLabelFor(propertiesTextField);
        blockPositionLabel.setLabelFor(blockPositionTextField);
        layerLabel.setLabelFor(layerSpinner);
        paletteLabel.setLabelFor(paletteSpinner);
        nbtLabel.setLabelFor(nbtTextField);
        sizeLabel.setLabelFor(sizeTextField);
        blockPaletteLabel.setLabelFor(blockPaletteSpinner);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
