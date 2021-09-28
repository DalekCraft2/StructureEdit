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

import me.dalekcraft.structureedit.drawing.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.Serial;

/**
 * @author eccentric_nz
 */
public final class SquareButton extends JButton {

    private final int[] position = new int[3];

    public SquareButton(@NotNull Block block, int x, int y, int z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
        Color color = block.getColor();
        Color noAlpha = new Color(color.getRed(), color.getGreen(), color.getBlue());
        setBackground(noAlpha);
        setOpaque(true);
        setText(block.name().substring(0, 1));
        setToolTipText(block.toId());
        setBorder(new LineBorder(Color.BLACK));
    }

    @Contract(pure = true)
    public int[] getPosition() {
        return position;
    }
}
