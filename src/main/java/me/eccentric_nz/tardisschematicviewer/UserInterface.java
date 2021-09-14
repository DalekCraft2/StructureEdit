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
import me.eccentric_nz.tardisschematicviewer.drawing.Block;
import me.eccentric_nz.tardisschematicviewer.util.BlockStateUtils;
import me.eccentric_nz.tardisschematicviewer.util.GzipUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.json.JSONException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
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

/**
 * @author eccentric_nz
 */
public class UserInterface extends JPanel {

    @Serial
    private static final long serialVersionUID = -1098962567729971976L;
    private static final FileNameExtensionFilter TSCHM_FILTER = new FileNameExtensionFilter("TARDIS schematic file", "tschm");
    private static final FileNameExtensionFilter NBT_FILTER = new FileNameExtensionFilter("NBT file", "nbt");
    private final SchematicRenderer renderer;
    private File lastDirectory = new File(".");
    private FileFilter lastFileFilter = TSCHM_FILTER;
    private SquareButton selected;
    private int currentLayer;
    private Schematic schematic;
    private ListTag<CompoundTag> palette;
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
    private JLabel blockPositionLabel;
    private JTextField layerTextField;
    private JTextField nbtTextField;
    private JLabel nbtLabel;
    private JLabel paletteLabel;
    private JComboBox<Integer> paletteComboBox;
    private JComboBox<Integer> blockPaletteComboBox;
    private final ActionListener actionListener = this::squareActionPerformed;
    private JLabel blockPaletteLabel;

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
                JFileChooser chooser = new JFileChooser(lastDirectory);
                chooser.addChoosableFileFilter(TSCHM_FILTER);
                chooser.addChoosableFileFilter(NBT_FILTER);
                chooser.setFileFilter(lastFileFilter);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.showOpenDialog(panel);
                if (chooser.getSelectedFile() != null) {
                    fileTextField.setText(chooser.getSelectedFile().getPath());
                    lastDirectory = chooser.getCurrentDirectory();
                    lastFileFilter = chooser.getFileFilter();
                    choose(chooser.getSelectedFile());
                }
            }
        });
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editorPanel.setVisible(!editorPanel.isVisible());
                UserInterface.this.renderer.setVisible(!editorPanel.isVisible());
                loadLayer();
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (schematic != null) {
                    if (schematic.getFormat().equals("tschm")) {
                        String output = UserInterface.this.renderer.getPath();
                        try {
                            GzipUtils.zip(schematic.getData(), output);
                            System.out.println("Schematic saved to \"" + output + "\" successfully.");
                        } catch (IOException e1) {
                            System.err.println("Error saving schematic: " + e1.getMessage());
                        }
                    } else if (schematic.getFormat().equals("nbt")) {
                        String output = UserInterface.this.renderer.getPath();
                        try {
                            NBTUtil.write((NamedTag) schematic.getData(), output);
                            System.out.println("Schematic saved to \"" + output + "\" successfully.");
                        } catch (IOException e1) {
                            System.err.println("Error saving schematic: " + e1.getMessage());
                        }
                    } else {
                        System.err.println("Not a schematic file! 1");
                    }
                } else {
                    System.err.println("Schematic was null!");
                }
            }
        });
        plusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentLayer < UserInterface.this.renderer.getRenderedHeight() - 1) {
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
                String blockId = "minecraft:" + blockComboBox.getSelectedItem().toString().toLowerCase();
                schematic.setBlockId(schematic.getBlock(position[0], position[1], position[2]), blockId);
                loadLayer();
            }
        });
        propertiesTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (selected != null) {
                    int[] position = selected.getPosition();
                    String propertiesString = propertiesTextField.getText().isEmpty() || propertiesTextField.getText().equals("[]") ? "" : propertiesTextField.getText();
                    schematic.setBlockPropertiesAsString(schematic.getBlock(position[0], position[1], position[2]), propertiesString);
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
                if (selected != null) {
                    int[] position = selected.getPosition();
                    CompoundTag nbt = new CompoundTag();
                    try {
                        nbt = (CompoundTag) SNBTUtil.fromSNBT(nbtTextField.getText());
                        nbtTextField.setForeground(Color.BLACK);
                    } catch (StringIndexOutOfBoundsException ignored) {
                    } catch (IOException e1) {
                        nbtTextField.setForeground(Color.RED);
                    }
                    ((NbtSchematic) schematic).setBlockNbt((CompoundTag) schematic.getBlock(position[0], position[1], position[2]), nbt);
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
        paletteComboBox.addItemListener(e -> {
            palette = ((NbtSchematic) schematic).getPaletteListEntry(Integer.parseInt(paletteComboBox.getSelectedItem().toString()));
            this.renderer.setPalette(palette);
            loadLayer();
        });
        // TODO Blockbench-style palette editor, with a list of palettes and palette IDs?
        blockPaletteComboBox.addItemListener(e -> {
            if (selected != null) {
                int[] position = selected.getPosition();
                ((NbtSchematic) schematic).setBlockState((CompoundTag) schematic.getBlock(position[0], position[1], position[2]), blockPaletteComboBox.getSelectedIndex());
                loadLayer();
            }
        });
    }

    private void createUIComponents() {
        panel = this;
        blockComboBox = new JComboBox<>();
        blockComboBox.setModel(new DefaultComboBoxModel<>(Block.strings()));
        blockComboBox.setSelectedItem(null);
    }

    public void choose(File file) {
        String path = file.getAbsolutePath();
        if (!path.isEmpty()) {
            try {
                selected = null;
                fileTextField.setText(path);
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
                renderer.setPath(path);
                schematic = renderer.getSchematic();
                currentLayer = 0;
                if (schematic.getFormat().equals("nbt")) {
                    if (((NbtSchematic) schematic).hasPaletteList()) {
                        int palettesSize = ((NbtSchematic) schematic).getPaletteList().size();
                        Integer[] palettes = new Integer[palettesSize];
                        for (int i = 0; i < palettesSize; i++) {
                            palettes[i] = i;
                        }
                        paletteComboBox.setModel(new DefaultComboBoxModel<>(palettes));
                        paletteComboBox.setSelectedItem("0");
                        palette = ((NbtSchematic) schematic).getPaletteListEntry(Integer.parseInt(paletteComboBox.getSelectedItem().toString()));
                        renderer.setPalette(palette);
                        paletteLabel.setVisible(true);
                        paletteComboBox.setVisible(true);
                    } else {
                        palette = ((NbtSchematic) schematic).getPalette();
                        renderer.setPalette(palette);
                        paletteLabel.setVisible(false);
                        paletteComboBox.setVisible(false);
                    }
                    int paletteSize = palette.size();
                    Integer[] paletteIds = new Integer[paletteSize];
                    for (int i = 0; i < paletteSize; i++) {
                        paletteIds[i] = i;
                    }
                    nbtLabel.setVisible(true);
                    nbtTextField.setVisible(true);
                    blockPaletteLabel.setVisible(true);
                    blockPaletteComboBox.setModel(new DefaultComboBoxModel<>(paletteIds));
                    blockPaletteComboBox.setVisible(true);
                } else {
                    nbtLabel.setVisible(false);
                    nbtTextField.setVisible(false);
                    blockPaletteLabel.setVisible(false);
                    blockPaletteComboBox.setVisible(false);
                }
                loadLayer();
            } catch (IOException | JSONException e1) {
                System.err.println("Error reading schematic: " + e1.getMessage());
            }
        } else {
            System.err.println("No file selected!");
        }
    }

    public void loadLayer() {
        if (schematic != null) {
            gridPanel.removeAll();
            gridPanel.setLayout(null);
            gridPanel.updateUI();
            layerTextField.setText(String.valueOf(currentLayer));
            int[] size = schematic.getSize();
            int buttonSideLength = gridPanel.getWidth() / size[0];
            for (int x = 0; x < size[0]; x++) {
                for (int z = 0; z < size[2]; z++) {
                    String blockId = schematic.getBlockId(schematic.getBlock(x, currentLayer, z));
                    String blockName = blockId.substring(blockId.indexOf(':') + 1).toUpperCase(Locale.ROOT);
                    Block blockEnum = Block.valueOf(blockName);
                    SquareButton squareButton = new SquareButton(buttonSideLength, blockEnum, x, currentLayer, z);
                    squareButton.setBounds(x * buttonSideLength, z * buttonSideLength, buttonSideLength, buttonSideLength);
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
        int[] position = selected.getPosition();

        blockPositionTextField.setEnabled(true);
        blockComboBox.setEnabled(true);
        propertiesTextField.setEnabled(true);
        if (schematic instanceof NbtSchematic nbtSchematic) {
            nbtTextField.setEnabled(true);
            nbtTextField.setForeground(Color.BLACK);
            nbtTextField.setText(nbtSchematic.getBlockSnbt(nbtSchematic.getBlock(position[0], position[1], position[2])));
            blockPaletteComboBox.setEnabled(true);
        }
        selected.setBorder(new LineBorder(Color.RED));
        blockComboBox.setSelectedItem(selected.getBlock().name());
        propertiesTextField.setText(schematic.getBlockPropertiesAsString(schematic.getBlock(position[0], position[1], position[2])));
        propertiesTextField.setForeground(Color.BLACK);
        blockPositionTextField.setText(Arrays.toString(selected.getPosition()));
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
        editorPanel.setLayout(new GridLayoutManager(9, 4, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel.setVisible(false);
        panel.add(editorPanel, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        editorPanel.add(spacer1, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        editorPanel.add(spacer2, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        blockLabel = new JLabel();
        blockLabel.setText("Block:");
        editorPanel.add(blockLabel, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editorPanel.add(blockComboBox, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        propertiesLabel = new JLabel();
        propertiesLabel.setText("Properties:");
        editorPanel.add(propertiesLabel, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        editorPanel.add(gridPanel, new GridConstraints(0, 0, 9, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(500, 500), new Dimension(600, 600), 0, false));
        gridPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        propertiesTextField = new JTextField();
        editorPanel.add(propertiesTextField, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        blockPositionLabel = new JLabel();
        blockPositionLabel.setText("Block Position:");
        editorPanel.add(blockPositionLabel, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(77, 33), null, 0, false));
        blockPositionTextField = new JTextField();
        blockPositionTextField.setEditable(false);
        editorPanel.add(blockPositionTextField, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 33), null, 0, false));
        layerLabel = new JLabel();
        layerLabel.setText("Layer:");
        editorPanel.add(layerLabel, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        plusButton = new JButton();
        plusButton.setText("+");
        editorPanel.add(plusButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minusButton = new JButton();
        minusButton.setText("-");
        editorPanel.add(minusButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        layerTextField = new JTextField();
        layerTextField.setEditable(false);
        editorPanel.add(layerTextField, new GridConstraints(0, 3, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nbtTextField = new JTextField();
        nbtTextField.setVisible(false);
        editorPanel.add(nbtTextField, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        paletteLabel = new JLabel();
        paletteLabel.setText("Palette:");
        paletteLabel.setVisible(false);
        editorPanel.add(paletteLabel, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paletteComboBox = new JComboBox();
        paletteComboBox.setVisible(false);
        editorPanel.add(paletteComboBox, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nbtLabel = new JLabel();
        nbtLabel.setText("NBT:");
        nbtLabel.setVisible(false);
        editorPanel.add(nbtLabel, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPaletteLabel = new JLabel();
        blockPaletteLabel.setText("Block Palette:");
        blockPaletteLabel.setVisible(false);
        editorPanel.add(blockPaletteLabel, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockPaletteComboBox = new JComboBox();
        blockPaletteComboBox.setVisible(false);
        editorPanel.add(blockPaletteComboBox, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
