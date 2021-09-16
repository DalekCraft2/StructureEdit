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
package me.eccentric_nz.tardisschematicviewer.drawing;

import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

import static org.lwjgl.opengl.GL46.glTranslatef;

/**
 * @author eccentric_nz
 */
public class Fence {

    public static void draw(Color color, float thickness, float sizeX, float sizeY, float sizeZ, CompoundTag properties) {

        glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(color, thickness, sizeY, thickness);

        if (properties.containsKey("south") && properties.getString("south").equals("true")) {
            // TODO Figure out the actual equation for this instead of using 2.5f.
            glTranslatef(0.0f, 0.0f, thickness * 2.5f);
            Cube.draw(color, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f);
            glTranslatef(0.0f, 0.0f, -thickness * 2.5f);
        }
        if (properties.containsKey("east") && properties.getString("east").equals("true")) {
            glTranslatef(thickness * 2.5f, 0.0f, 0.0f);
            Cube.draw(color, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness);
            glTranslatef(-thickness * 2.5f, 0.0f, 0.0f);
        }
        if (properties.containsKey("north") && properties.getString("north").equals("true")) {
            glTranslatef(0.0f, 0.0f, -thickness * 2.5f);
            Cube.draw(color, thickness, sizeY * 0.8f, (sizeZ - thickness) / 2.0f);
            glTranslatef(0.0f, 0.0f, thickness * 2.5f);
        }
        if (properties.containsKey("west") && properties.getString("west").equals("true")) {
            glTranslatef(-thickness * 2.5f, 0.0f, 0.0f);
            Cube.draw(color, (sizeX - thickness) / 2.0f, sizeY * 0.8f, thickness);
            glTranslatef(thickness * 2.5f, 0.0f, 0.0f);
        }
    }
}
