/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.dalekcraft.structureedit.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;

/**
 * Deserializes hexadecimal {@link Integer}s from {@link String}s.
 */
public class ColorAdapter implements JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String hexString = json.getAsJsonPrimitive().getAsString();
        /* if (!hexString.startsWith("#")) {
            hexString = "#" + hexString;
        }
        if (hexString.length() != 7) {
            throw new JsonParseException("String does not have length 6");
        }
        System.out.println("String: " + hexString);
        int hex;
        try {
            hex = Integer.decode(hexString);
        } catch (NumberFormatException e) {
            throw new JsonParseException("String does not contain parseable integer", e);
        }
        return hex; */
        return Color.valueOf(hexString);
        // Color color = Color.valueOf(hexString);
        // if (color.equals(Color.BLACK)) {
        //     // A map color of "#000000" in block data typically indicates that the block is transparent on maps
        //     color = Color.TRANSPARENT;
        // }
        // return color;
    }
}