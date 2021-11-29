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

package me.dalekcraft.structureedit.schematic.io.legacycompat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class LegacyMapper {

    private static final Logger LOGGER = LogManager.getLogger();
    private static LegacyMapper INSTANCE;

    private final Map<String, BlockState> stringToBlockMap = new HashMap<>();
    private final Multimap<BlockState, String> blockToStringMap = HashMultimap.create();
    // private final Map<String, ItemType> stringToItemMap = new HashMap<>();
    // private final Multimap<ItemType, String> itemToStringMap = HashMultimap.create();

    /**
     * Create a new instance.
     */
    private LegacyMapper() {
        try {
            loadFromResource();
        } catch (Throwable e) {
            LOGGER.warn("Failed to load the built-in legacy id registry", e);
        }
    }

    public static LegacyMapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LegacyMapper();
        }
        return INSTANCE;
    }

    /**
     * Attempt to load the data from file.
     *
     * @throws IOException thrown on I/O error
     */
    private void loadFromResource() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // gsonBuilder.registerTypeAdapter(Vector3.class, new VectorAdapter());
        Gson gson = gsonBuilder.disableHtmlEscaping().create();
        URL url = getClass().getResource("/legacy.json");
        if (url == null) {
            throw new IOException("Could not find legacy.json");
        }
        String data = Resources.toString(url, Charset.defaultCharset());
        LegacyDataFile dataFile = gson.fromJson(data, new TypeToken<LegacyDataFile>() {
        }.getType());

        for (Map.Entry<String, String> blockEntry : dataFile.blocks.entrySet()) {
            String id = blockEntry.getKey();
            final String value = blockEntry.getValue();

            int nameEndIndex = value.length();
            if (value.contains("[")) {
                nameEndIndex = value.indexOf('[');
            }
            String blockId = value.substring(0, nameEndIndex);
            String propertyString = value.substring(nameEndIndex).replace("[", "").replace("]", "");
            Map<String, String> propertyMap = BlockState.SPLITTER.split(propertyString);
            BlockState blockState = new BlockState(blockId, propertyMap);

            blockToStringMap.put(blockState, id);
            stringToBlockMap.put(id, blockState);
        }

        /*for (Map.Entry<String, String> itemEntry : dataFile.items.entrySet()) {
            String id = itemEntry.getKey();
            String value = itemEntry.getValue();
            ItemType type = ItemTypes.get(value);
            if (type == null && fixer != null) {
                value = fixer.fixUp(DataFixer.FixTypes.ITEM_TYPE, value, Constants.DATA_VERSION_MC_1_13_2);
                type = ItemTypes.get(value);
            }
            if (type == null) {
                LOGGER.debug("Unknown item: " + value);
            } else {
                itemToStringMap.put(type, id);
                stringToItemMap.put(id, type);
            }
        }*/
    }

    /*@Nullable
    public ItemType getItemFromLegacy(int legacyId) {
        return getItemFromLegacy(legacyId, 0);
    }

    @Nullable
    public ItemType getItemFromLegacy(int legacyId, int data) {
        return stringToItemMap.get(legacyId + ":" + data);
    }

    @Nullable
    public int[] getLegacyFromItem(ItemType itemType) {
        if (itemToStringMap.containsKey(itemType)) {
            String value = itemToStringMap.get(itemType).stream().findFirst().get();
            return Arrays.stream(value.split(":")).mapToInt(Integer::parseInt).toArray();
        } else {
            return null;
        }
    }*/

    @Nullable
    public BlockState getBlockFromLegacy(int legacyId) {
        return getBlockFromLegacy(legacyId, 0);
    }

    @Nullable
    public BlockState getBlockFromLegacy(int legacyId, int data) {
        return stringToBlockMap.get(legacyId + ":" + data);
    }

    public int @Nullable [] getLegacyFromBlock(BlockState blockState) {
        if (blockToStringMap.containsKey(blockState)) {
            String value = blockToStringMap.get(blockState).stream().findFirst().get();
            return Arrays.stream(value.split(":")).mapToInt(Integer::parseInt).toArray();
        } else {
            return null;
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static class LegacyDataFile {
        private Map<String, String> blocks;
        private Map<String, String> items;
    }
}
