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

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.io.Serial;

/**
 * @author eccentric_nz
 */
public final class SquareButton extends JButton {

    @Serial
    private static final long serialVersionUID = 7623333770238989633L;

    private final int size;
    private final int xCoord, yCoord, zCoord;
    private Block block;
    private String properties;
    private CompoundTag nbt;

    public SquareButton(int size, Block block, int xCoord, int yCoord, int zCoord, String properties) {
        this(size, block, xCoord, yCoord, zCoord, properties, null);
    }

    public SquareButton(int size, Block block, int xCoord, int yCoord, int zCoord, String properties, CompoundTag nbt) {
        this.size = size;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.block = block;
        this.properties = properties;
        this.nbt = nbt;
        setPreferredSize(new Dimension(size, size));
        setSize(getPreferredSize());
        Color color = block.getColor();
        if (color != null) {
            setBackground(color);
        }
        setOpaque(true);
        setText(block.name().substring(0, 1));
        setToolTipText("minecraft:" + block.name().toLowerCase());
        setBorder(new LineBorder(Color.BLACK));
    }

    public int getXCoord() {
        return xCoord;
    }

    public int getYCoord() {
        return yCoord;
    }

    public int getZCoord() {
        return zCoord;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public CompoundTag getNbt() {
        return nbt;
    }

    public void setNbt(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public String getSnbt() {
        String snbt = null;
        try {
            snbt = nbt == null ? null : SNBTUtil.toSNBT(nbt);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return snbt;
    }

    public void setSnbt(String snbt) {
        try {
            nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }
}
