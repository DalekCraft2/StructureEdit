/*
 * Copyright (C) 2015 eccentric_nz
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

import javafx.scene.control.Button;
import me.dalekcraft.structureedit.schematic.container.Block;

/**
 * @author eccentric_nz
 */
public class BlockButton extends Button {

    private final Block block;
    private final int[] position;

    public BlockButton(Block block, int[] position) {
        this(block, position[0], position[1], position[2]);
    }

    public BlockButton(Block block, int x, int y, int z) {
        this.block = block;
        position = new int[]{x, y, z};
    }

    public Block getBlock() {
        return block;
    }

    public int[] getPosition() {
        return position;
    }
}
