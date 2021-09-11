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

import com.jogamp.opengl.GL4bc;
import me.eccentric_nz.tardisschematicviewer.util.BlockStateUtils;
import net.querz.nbt.tag.CompoundTag;

import java.awt.*;

/**
 * @author eccentric_nz
 */
public class Redstone {

    public static void draw(GL4bc gl, Color color, float thickness, float sizeX, float sizeY, float sizeZ, Object properties) {

        gl.glTranslatef(0.0f, sizeY - 1.0f, 0.0f);

        Cube.draw(gl, color, thickness, sizeY, thickness);

        CompoundTag tag = null;

        if (properties instanceof String) {
            tag = BlockStateUtils.toTag((String) properties);
        } else if (properties instanceof CompoundTag) {
            tag = (CompoundTag) properties;
        }
        if (tag.getString("south").equals("side")) {
            gl.glTranslatef(0.0f, 0.0f, thickness * 2.5f);
            Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 0.0f, -thickness * 2.5f);
        } else if (tag.getString("south").equals("up")) {
            gl.glTranslatef(0.0f, 0.0f, thickness * 2.5f);
            Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 0.0f, -thickness * 2.5f);
            gl.glTranslatef(0.0f, 1.0f + sizeY, sizeZ - sizeY);
            gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            Cube.draw(gl, color, thickness, sizeY, sizeZ);
            gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            gl.glTranslatef(0.0f, -1.0f - sizeY, sizeY - sizeZ);
        }
        if (tag.getString("east").equals("side")) {
            gl.glTranslatef(thickness * 2.5f, 0.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
            gl.glTranslatef(-thickness * 2.5f, 0.0f, 0.0f);
        } else if (tag.getString("east").equals("up")) {
            gl.glTranslatef(thickness * 2.5f, 0.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
            gl.glTranslatef(-thickness * 2.5f, 0.0f, 0.0f);
            gl.glTranslatef(sizeX - sizeY, 1.0f + sizeY, 0.0f);
            gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            Cube.draw(gl, color, sizeX, sizeY, thickness);
            gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
            gl.glTranslatef(sizeY - sizeX, -1.0f - sizeY, 0.0f);
        }
        if (tag.getString("north").equals("side")) {
            gl.glTranslatef(0.0f, 0.0f, -thickness * 2.5f);
            Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 0.0f, thickness * 2.5f);
        } else if (tag.getString("north").equals("up")) {
            gl.glTranslatef(0.0f, 0.0f, -thickness * 2.5f);
            Cube.draw(gl, color, thickness, sizeY, (sizeZ - thickness) / 2.0f);
            gl.glTranslatef(0.0f, 0.0f, thickness * 2.5f);
            gl.glTranslatef(0.0f, 1.0f + sizeY, sizeY - sizeZ);
            gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            Cube.draw(gl, color, thickness, sizeY, sizeZ);
            gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            gl.glTranslatef(0.0f, -1.0f - sizeY, sizeZ - sizeY);
        }
        if (tag.getString("west").equals("side")) {
            gl.glTranslatef(-thickness * 2.5f, 0.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
            gl.glTranslatef(thickness * 2.5f, 0.0f, 0.0f);
        } else if (tag.getString("east").equals("up")) {
            gl.glTranslatef(-thickness * 2.5f, 0.0f, 0.0f);
            Cube.draw(gl, color, (sizeX - thickness) / 2.0f, sizeY, thickness);
            gl.glTranslatef(thickness * 2.5f, 0.0f, 0.0f);
            gl.glTranslatef(sizeY - sizeX, 1.0f + sizeY, 0.0f);
            gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
            Cube.draw(gl, color, sizeX, sizeY, thickness);
            gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            gl.glTranslatef(sizeX - sizeY, -1.0f - sizeY, 0.0f);
        }
    }
}
