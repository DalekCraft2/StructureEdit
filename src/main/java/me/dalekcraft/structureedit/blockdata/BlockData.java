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

package me.dalekcraft.structureedit.blockdata;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.paint.Color;
import me.dalekcraft.structureedit.util.ColorAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides block data based on the built-in block database that is bundled
 * with WorldEdit.
 *
 * <p>A new instance cannot be created. Use {@link #getInstance()} to get
 * an instance.</p>
 *
 * <p>The data is read from a JSON file that is bundled with WorldEdit. If
 * reading fails (which occurs when this class is first instantiated), then
 * the methods will return {@code null}s for all blocks.</p>
 */
public final class BlockData {

    private static final Logger LOGGER = LogManager.getLogger();
    private static BlockData INSTANCE;

    private final Map<String, BlockEntry> idMap = new HashMap<>();

    /**
     * Create a new instance.
     */
    private BlockData() {
        try {
            loadFromResource();
        } catch (Throwable e) {
            LOGGER.warn("Failed to load the built-in block registry", e);
        }
    }

    /**
     * Attempt to load the data from file.
     *
     * @throws IOException thrown on I/O error
     */
    private void loadFromResource() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Color.class, new ColorAdapter());
        Gson gson = gsonBuilder.create();
        // URL url = BundledRegistries.loadRegistry("blocks");
        URL url = getClass().getResource("/registry/blocks.1214.json");
        LOGGER.debug("Using {} for bundled block data.", url);
        String data = Resources.toString(url, Charset.defaultCharset());
        List<BlockEntry> entries = gson.fromJson(data, new TypeToken<>() {
        });

        for (BlockEntry entry : entries) {
            idMap.put(entry.id, entry);
        }
    }

    /**
     * Return the entry for the given block ID.
     *
     * @param id the ID
     * @return the entry, or null
     */
    @Nullable
    public BlockEntry findById(String id) {
        // If it has no namespace, assume minecraft.
        if (!id.contains(":")) {
            id = "minecraft:" + id;
        }
        return idMap.get(id);
    }

    /**
     * Get the material properties for the given block.
     *
     * @param id the string ID
     * @return the material's properties, or null
     */
    @Nullable
    public BlockMaterial getMaterialById(String id) {
        BlockEntry entry = findById(id);
        if (entry != null) {
            return entry.material;
        } else {
            return null;
        }
    }

    /**
     * Get a singleton instance of this object.
     *
     * @return the instance
     */
    public static BlockData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BlockData();
        }
        return INSTANCE;
    }

    public static class BlockEntry {
        public String id;
        public String localizedName;
        private final SimpleBlockMaterial material = new SimpleBlockMaterial();
    }

}
