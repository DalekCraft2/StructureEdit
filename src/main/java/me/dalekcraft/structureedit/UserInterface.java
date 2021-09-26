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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
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
    private JPanel rawRenderer;
    private File lastDirectory;
    private FileFilter lastFileFilter = FILTER_NBT;
    private SquareButton selected;
    private int currentLayer;
    private Schematic schematic;
    private ListTag<CompoundTag> palette;
    private JButton openButton;
    private JTextField fileTextField;
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
    private JLabel blockPositionLabel;
    private JTextField layerTextField;
    private JTextField nbtTextField;
    private JLabel nbtLabel;
    private JLabel paletteLabel;
    private JComboBox<Integer> paletteComboBox;
    private JComboBox<Integer> blockPaletteComboBox;
    private final ActionListener actionListener = this::squareActionPerformed;
    private JLabel blockPaletteLabel;
    private JLabel fileLabel;
    private JTextField sizeTextField;
    private JLabel sizeLabel;

    {
        try {
            lastDirectory = new File(".").getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                renderer.pause();
                JFileChooser chooser = new JFileChooser(lastDirectory);
                chooser.addChoosableFileFilter(FILTER_NBT);
                chooser.addChoosableFileFilter(FILTER_MCEDIT);
                chooser.addChoosableFileFilter(FILTER_SPONGE);
                chooser.addChoosableFileFilter(FILTER_TARDIS);
                chooser.setFileFilter(lastFileFilter);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = chooser.showOpenDialog(panel);
                if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                    File file;
                    try {
                        file = chooser.getSelectedFile().getCanonicalFile();
                        open(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    lastDirectory = chooser.getCurrentDirectory();
                    lastFileFilter = chooser.getFileFilter();
                }
                renderer.resume();
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (schematic != null) {
                    renderer.pause();
                    JFileChooser chooser = new JFileChooser(lastDirectory);
                    chooser.addChoosableFileFilter(FILTER_TARDIS);
                    chooser.addChoosableFileFilter(FILTER_NBT);
                    chooser.setFileFilter(lastFileFilter);
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int result = chooser.showSaveDialog(panel);
                    if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                        lastDirectory = chooser.getCurrentDirectory();
                        try {
                            File file = chooser.getSelectedFile().getCanonicalFile();
                            System.out.println("Saving " + file + "...");
                            schematic.saveTo(file);
                            System.out.println("Schematic saved to " + file + " successfully.");
                        } catch (IOException e1) {
                            System.err.println("Error saving schematic: " + e1.getMessage());
                        }
                    }
                    renderer.resume();
                } else {
                    System.err.println("Schematic was null!");
                }
            }
        });
        plusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentLayer < schematic.getSize()[1] - 1) {
                    currentLayer++;
                    loadLayer();
                }
            }
        });
        minusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentLayer > 0) {
                    currentLayer--;
                    loadLayer();
                }
            }
        });
        blockComboBox.addItemListener(e -> {
            if (selected != null) {
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
                if (selected != null) {
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
                if (schematic instanceof NbtStructure nbtStructure) {
                    if (selected != null) {
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
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        paletteComboBox.addItemListener(e -> {
            if (schematic instanceof NbtStructure nbtStructure) {
                if (nbtStructure.hasPaletteList()) {
                    palette = nbtStructure.getPaletteListEntry(Integer.parseInt(paletteComboBox.getSelectedItem().toString()));
                } else {
                    palette = nbtStructure.getPalette();
                }
            }
            this.renderer.setPalette(palette);
            updateSelected();
            loadLayer();
        });
        // TODO Blockbench-style palette editor, with a list of palettes and palette IDs?
        blockPaletteComboBox.addItemListener(e -> {
            if (schematic instanceof NbtStructure nbtStructure) {
                if (selected != null) {
                    int[] position = selected.getPosition();
                    CompoundTag block = nbtStructure.getBlock(position[0], position[1], position[2]);
                    if (block != null) {
                        nbtStructure.setBlockState(block, blockPaletteComboBox.getSelectedIndex());
                    }
                    updateSelected();
                    loadLayer();
                }
            }
        });
    }

    public void open(@NotNull File file) {
        System.out.println("Loading " + file + " ...");
        try {
            selected = null;
            fileTextField.setText(file.toString());
            sizeTextField.setText(null);
            paletteComboBox.setSelectedItem(null);
            paletteComboBox.setEnabled(false);
            blockPositionTextField.setText(null);
            blockPositionTextField.setEnabled(false);
            blockComboBox.setSelectedItem(null);
            blockComboBox.setEnabled(false);
            propertiesTextField.setText(null);
            propertiesTextField.setEnabled(false);
            nbtTextField.setText(null);
            nbtTextField.setEnabled(false);
            blockPaletteComboBox.setSelectedItem(null);
            blockPaletteComboBox.setEnabled(false);
            currentLayer = 0;
            Schematic schematic = Schematic.openFrom(file);
            if (schematic != null) {
                this.schematic = schematic;
                this.renderer.setSchematic(schematic);
                if (schematic instanceof NbtStructure nbtStructure) {
                    if (nbtStructure.hasPaletteList()) {
                        int palettesSize = nbtStructure.getPaletteList().size();
                        Integer[] palettes = new Integer[palettesSize];
                        for (int i = 0; i < palettesSize; i++) {
                            palettes[i] = i;
                        }
                        paletteComboBox.setEnabled(true);
                        paletteComboBox.setModel(new DefaultComboBoxModel<>(palettes));
                        paletteComboBox.setSelectedIndex(0);
                        palette = nbtStructure.getPaletteListEntry(Integer.parseInt(paletteComboBox.getSelectedItem().toString()));
                    } else {
                        palette = nbtStructure.getPalette();
                    }
                    this.renderer.setPalette(palette);
                    int paletteSize = palette.size();
                    Integer[] paletteIds = new Integer[paletteSize];
                    for (int i = 0; i < paletteSize; i++) {
                        paletteIds[i] = i;
                    }
                    blockPaletteComboBox.setModel(new DefaultComboBoxModel<>(paletteIds));
                } else {
                    paletteComboBox.setEnabled(false);
                }
                loadLayer();
                System.out.println("Loaded " + file + " successfully.");
            } else {
                System.err.println("Not a schematic file!");
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error reading schematic: " + e.getMessage());
        }
    }

    // TODO Make the editor built into the 3D view instead of being a layer-by-layer editor.
    public void loadLayer() {
        if (schematic != null) {
            gridPanel.removeAll();
            gridPanel.setLayout(null);
            gridPanel.updateUI();
            layerTextField.setText(String.valueOf(currentLayer));
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

    private void squareActionPerformed(ActionEvent e) {
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

        String blockName = blockId.substring(blockId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
        blockComboBox.setEnabled(true);
        blockComboBox.setSelectedItem(blockName);

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
            blockPaletteComboBox.setEnabled(true);
            blockPaletteComboBox.setSelectedIndex(blockState);
        } else {
            blockPaletteComboBox.setEnabled(false);
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

            String blockName = blockId.substring(blockId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
            blockComboBox.setSelectedItem(blockName);

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
        blockComboBox.setModel(new DefaultComboBoxModel<>(Block.strings()));
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
        panel.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel = new JPanel();
        editorPanel.setLayout(new GridLayoutManager(10, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(editorPanel, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        blockLabel = new JLabel();
        blockLabel.setText("Block:");
        editorPanel.add(blockLabel, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockComboBox.setToolTipText("The ID of the selected block");
        editorPanel.add(blockComboBox, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        propertiesLabel = new JLabel();
        propertiesLabel.setText("Properties:");
        editorPanel.add(propertiesLabel, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        propertiesTextField = new JTextField();
        propertiesTextField.setToolTipText("The properties of the selected block");
        editorPanel.add(propertiesTextField, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        blockPositionLabel = new JLabel();
        blockPositionLabel.setText("Block Position:");
        editorPanel.add(blockPositionLabel, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(77, 33), null, 0, false));
        blockPositionTextField = new JTextField();
        blockPositionTextField.setEditable(false);
        blockPositionTextField.setToolTipText("The position of the selected block, ordered as [x, y, z]");
        editorPanel.add(blockPositionTextField, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 33), null, 0, false));
        layerLabel = new JLabel();
        layerLabel.setText("Layer:");
        editorPanel.add(layerLabel, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        plusButton = new JButton();
        plusButton.setText("+");
        editorPanel.add(plusButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minusButton = new JButton();
        minusButton.setText("-");
        editorPanel.add(minusButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        layerTextField = new JTextField();
        layerTextField.setEditable(false);
        layerTextField.setToolTipText("The currently viewed layer");
        editorPanel.add(layerTextField, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nbtTextField = new JTextField();
        nbtTextField.setToolTipText("The NBT of the selected block");
        editorPanel.add(nbtTextField, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        paletteLabel = new JLabel();
        paletteLabel.setText("Palette:");
        editorPanel.add(paletteLabel, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paletteComboBox = new JComboBox();
        paletteComboBox.setToolTipText("The index of the active palette");
        editorPanel.add(paletteComboBox, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nbtLabel = new JLabel();
        nbtLabel.setText("NBT:");
        editorPanel.add(nbtLabel, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel.add(gridPanel, new GridConstraints(9, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        sizeLabel = new JLabel();
        sizeLabel.setText("Schematic Size:");
        editorPanel.add(sizeLabel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sizeTextField = new JTextField();
        sizeTextField.setEditable(false);
        sizeTextField.setToolTipText("The dimensions of the schematic, ordered as [sizeX, sizeY, sizeZ]");
        editorPanel.add(sizeTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 33), null, 0, false));
        blockPaletteLabel = new JLabel();
        blockPaletteLabel.setText("Block Palette:");
        editorPanel.add(blockPaletteLabel, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPaletteComboBox = new JComboBox();
        blockPaletteComboBox.setToolTipText("The index of the selected block's state in the active palette");
        editorPanel.add(blockPaletteComboBox, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileTextField = new JTextField();
        fileTextField.setEditable(false);
        fileTextField.setToolTipText("The path to the schematic file");
        panel.add(fileTextField, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        panel.add(openButton, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(rawRenderer, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel.add(saveButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileLabel = new JLabel();
        fileLabel.setText("File:");
        panel.add(fileLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockLabel.setLabelFor(blockComboBox);
        propertiesLabel.setLabelFor(propertiesTextField);
        blockPositionLabel.setLabelFor(blockPositionTextField);
        layerLabel.setLabelFor(layerTextField);
        paletteLabel.setLabelFor(paletteComboBox);
        nbtLabel.setLabelFor(nbtTextField);
        sizeLabel.setLabelFor(sizeTextField);
        blockPaletteLabel.setLabelFor(blockPaletteComboBox);
        fileLabel.setLabelFor(fileTextField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
