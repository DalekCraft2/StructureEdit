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

import javafx.scene.control.Button;
import me.dalekcraft.structureedit.schematic.Schematic;
import org.jetbrains.annotations.Contract;

/**
 * @author eccentric_nz
 */
public class BlockButton extends Button {

    private final Schematic.Block block;

    public BlockButton(Schematic.Block block) {
        this.block = block;
    }

    @Contract(pure = true)
    public Schematic.Block getBlock() {
        return block;
    }
}
