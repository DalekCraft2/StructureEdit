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
package me.dalekcraft.structureedit.schematic.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JsonSchematicReader implements SchematicReader {

    protected static <T extends JsonElement> T requireTag(@NotNull JsonObject items, String key, Class<T> expected) throws ValidationException {
        if (!items.has(key)) {
            throw new ValidationException("Schematic file is missing a \"" + key + "\" tag of type " + expected.getName());
        }

        JsonElement tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new ValidationException(key + " tag is not of tag type " + expected.getName() + ", got " + tag.getClass().getName() + " instead");
        }

        return expected.cast(tag);
    }

    protected static <T extends JsonElement> T requireTag(@NotNull JsonArray items, int index, Class<T> expected) throws ValidationException {
        if (!(items.size() > index)) {
            throw new ValidationException("Schematic file is missing a tag at index " + index + " of type " + expected.getName());
        }

        JsonElement tag = items.get(index);
        if (!expected.isInstance(tag)) {
            throw new ValidationException(index + " tag is not of tag type " + expected.getName() + ", got " + tag.getClass().getName() + " instead");
        }

        return expected.cast(tag);
    }

    @Nullable
    protected static <T extends JsonElement> T optTag(@NotNull JsonObject items, String key, Class<T> expected) {
        if (!items.has(key)) {
            return null;
        }

        JsonElement test = items.get(key);
        if (!expected.isInstance(test)) {
            return null;
        }

        return expected.cast(test);
    }

    @Nullable
    protected static <T extends JsonElement> T optTag(@NotNull JsonArray items, int index, Class<T> expected) {
        if (!(items.size() > index)) {
            return null;
        }

        JsonElement test = items.get(index);
        if (!expected.isInstance(test)) {
            return null;
        }

        return expected.cast(test);
    }
}
