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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SchematicFormats {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, SchematicFormat> ALIAS_MAP = new HashMap<>();
    private static final Multimap<String, SchematicFormat> FILE_EXTENSION_MAP = HashMultimap.create();
    private static final List<SchematicFormat> REGISTERED_FORMATS = new ArrayList<>();

    static {
        for (BuiltInSchematicFormat format : BuiltInSchematicFormat.values()) {
            registerSchematicFormat(format);
        }
    }

    private SchematicFormats() {
    }

    public static void registerSchematicFormat(SchematicFormat format) {
        checkNotNull(format);

        for (String key : format.getAliases()) {
            String lowKey = key.toLowerCase(Locale.ROOT);
            SchematicFormat old = ALIAS_MAP.put(lowKey, format);
            if (old != null) {
                ALIAS_MAP.put(lowKey, old);
                LOGGER.warn(format.getClass().getName() + " cannot override existing alias '" + lowKey + "' used by " + old.getClass().getName());
            }
        }
        for (String ext : format.getFileExtensions()) {
            String lowExt = ext.toLowerCase(Locale.ROOT);
            FILE_EXTENSION_MAP.put(lowExt, format);
        }
        REGISTERED_FORMATS.add(format);
    }

    /**
     * Find the schematic format named by the given alias.
     *
     * @param alias the alias
     * @return the format, otherwise null if none is matched
     */
    @Nullable
    public static SchematicFormat findByAlias(String alias) {
        checkNotNull(alias);
        return ALIAS_MAP.get(alias.toLowerCase(Locale.ROOT).trim());
    }

    /**
     * Detect the format of given a file.
     *
     * @param file the file
     * @return the format, otherwise null if one cannot be detected
     */
    @Nullable
    public static SchematicFormat findByFile(File file) {
        checkNotNull(file);

        for (SchematicFormat format : REGISTERED_FORMATS) {
            if (format.isFormat(file)) {
                return format;
            }
        }

        return null;
    }

    /**
     * A mapping from extensions to formats.
     *
     * @return a multimap from a file extension to the potential matching formats.
     */
    public static Multimap<String, SchematicFormat> getFileExtensionMap() {
        return Multimaps.unmodifiableMultimap(FILE_EXTENSION_MAP);
    }

    public static Collection<SchematicFormat> getAll() {
        return Collections.unmodifiableCollection(REGISTERED_FORMATS);
    }

    /**
     * Not public API, only used by SchematicCommands.
     * It is not in SchematicCommands because it may rely on internal register calls.
     */
    public static String[] getFileExtensionArray() {
        return FILE_EXTENSION_MAP.keySet().toArray(new String[FILE_EXTENSION_MAP.keySet().size()]);
    }

}
