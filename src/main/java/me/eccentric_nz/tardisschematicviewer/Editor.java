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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author eccentric_nz
 */
public class Editor extends JPanel {

    @Serial
    private static final long serialVersionUID = 6012462642009590681L;
    private final TardisSchematicViewer viewer;
    private final List<SquareButton> buttons;
    private SquareButton selected;
    private JSONObject schematic;
    private JPanel panel;
    private JButton closeButton;
    private JComboBox<String> blockComboBox;
    private JComboBox dataComboBox;
    ActionListener actionListener = this::squareActionPerformed;
    private JLabel blockLabel;
    private JLabel dataLabel;
    private JInternalFrame layoutArea;

    public Editor(TardisSchematicViewer viewer) {
        this.viewer = viewer;
        buttons = new ArrayList<>();
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setVisible(false);
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

    public void loadLayer() {
        if (buttons.size() > 0) {
            buttons.forEach((button) -> layoutArea.remove(button));
            buttons.clear();
        }
        layoutArea.setLayout(null);
        schematic = viewer.getSchematic();
        if (schematic != null) {
            JSONObject dimensions = (JSONObject) schematic.get("dimensions");
            int current = viewer.getHeight() - 1;
            JSONArray level = ((JSONArray) schematic.get("input")).getJSONArray(current);
            int width = dimensions.getInt("width");
            int layoutWidth = layoutArea.getWidth() / width;
            for (int i = 0; i < width; i++) {
                JSONArray row = (JSONArray) level.get(i);
                for (int j = 0; j < width; j++) {
                    JSONObject column = (JSONObject) row.get(j);
                    String data = column.getString("data");
                    String blockName = data.split(":")[1].split("\\[")[0].toUpperCase(Locale.ROOT);
                    Block block = Block.valueOf(blockName);
                    SquareButton squareButton = new SquareButton(layoutWidth, block.getColor());
                    squareButton.setText(blockName.substring(0, 1));
                    squareButton.setPreferredSize(new Dimension(layoutWidth, layoutWidth));
                    squareButton.setBounds(i * layoutWidth, j * layoutWidth, layoutWidth, layoutWidth);
                    squareButton.setBorder(new LineBorder(Color.BLACK));
                    squareButton.setToolTipText(data);
                    squareButton.addActionListener(actionListener);
                    layoutArea.add(squareButton);
                    buttons.add(squareButton);
                }
            }
        } else {
            System.out.println("schematic was null");
        }
    }

    private void squareActionPerformed(ActionEvent evt) {
        if (selected != null) {
            // remove selected border
            selected.setBorder(new LineBorder(Color.BLACK));
        }
        selected = (SquareButton) evt.getSource();
        int x = selected.getX() / 37;
        int z = selected.getY() / 37;
        System.out.println(x + "," + z);
        String[] split = selected.getToolTipText().split(":");
        selected.setBorder(new LineBorder(Color.RED));
        blockComboBox.setSelectedItem(split[0]);
        dataComboBox.setSelectedItem(split[1]);
    }
}
