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

import me.eccentric_nz.tardisschematicviewer.drawing.Block;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.Serial;

/**
 * @author eccentric_nz
 */
public final class SquareButton extends JButton {

    @Serial
    private static final long serialVersionUID = 7623333770238989633L;

    private final int size;
    private final int[] position = new int[3];

    public SquareButton(int size, Block block, int x, int y, int z) {
        this.size = size;
        position[0] = x;
        position[1] = y;
        position[2] = z;
        setPreferredSize(new Dimension(size, size));
        setSize(getPreferredSize());
        Color color = block.getColor();
        Color noAlpha = new Color(color.getRed(), color.getGreen(), color.getBlue());
        setBackground(noAlpha);
        setOpaque(true);
        setText(block.name().substring(0, 1));
        setToolTipText("minecraft:" + block.name().toLowerCase());
        setBorder(new LineBorder(Color.BLACK));
    }

    public int[] getPosition() {
        return position;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }
}
