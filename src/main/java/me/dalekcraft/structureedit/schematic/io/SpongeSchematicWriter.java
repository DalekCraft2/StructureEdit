package me.dalekcraft.structureedit.schematic.io;

import com.google.common.primitives.Bytes;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.schematic.container.*;
import me.dalekcraft.structureedit.util.Constants;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;

import java.io.IOException;
import java.util.*;

public class SpongeSchematicWriter extends NbtSchematicWriter {

    private final NBTOutputStream outputStream;

    // private int schematicVersion = -1;

    public SpongeSchematicWriter(NBTOutputStream outputStream) {
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    @Override
    public void write(Schematic schematic) throws IOException {
        // TODO How to determine which Sponge schematic version to use?
        writeV3(schematic);

        /* switch (schematicVersion) {
            case 1 -> writeV1(schematic);
            case 2 -> writeV2(schematic);
            case 3 -> writeV3(schematic);
            default -> throw new ValidationException("Illegal Sponge schematic version: " + schematicVersion);
        } */
    }

    private void writeV2(Schematic schematic) throws IOException {
        CompoundTag root = new CompoundTag();

        root.putInt("Version", 2);

        root.putInt("DataVersion", schematic.getDataVersion());

        CompoundTag metadata = schematic.getMetadata();
        if (metadata != null) {
            root.put("Metadata", metadata);
        }

        int[] size = schematic.getSize();
        root.putShort("Width", (short) size[0]);
        root.putShort("Height", (short) size[1]);
        root.putShort("Length", (short) size[2]);

        int[] offset = schematic.getOffset();
        if (offset[0] != 0 || offset[1] != 0 || offset[2] != 0) {
            root.putIntArray("Offset", offset);
        }

        // TODO Ensure that the block, biome, and entity stuff works correctly.

        // Copied to make the original schematic's palette not affected from operations (I.E. adding minecraft:air to it)
        List<BlockState> blockPalette = new ArrayList<>(schematic.getBlockPalette());

        root.putInt("PaletteMax", blockPalette.size());

        CompoundTag blockPaletteTag = new CompoundTag();

        for (int i = 0; i < blockPalette.size(); i++) {
            BlockState blockState = blockPalette.get(i);

            blockPaletteTag.putInt(blockState.toString(), i);
        }

        root.put("Palette", blockPaletteTag);

        ListTag<CompoundTag> blockEntitiesTag = new ListTag<>(CompoundTag.class);
        List<Byte> blocksList = Arrays.asList(new Byte[size[0] * size[1] * size[2]]);
        for (int y = 0; y < size[1]; y++) {
            for (int z = 0; z < size[2]; z++) {
                for (int x = 0; x < size[0]; x++) {
                    int index = y * size[2] * size[0] + z * size[0] + x;
                    Block block = schematic.getBlock(x, y, z);
                    if (block != null) {
                        blocksList.set(index, (byte) block.getBlockStateIndex());

                        BlockEntity blockEntity = block.getBlockEntity();
                        if (!blockEntity.isEmpty()) {
                            CompoundTag nbt = blockEntity.getNbt().clone();
                            CompoundTag blockEntityTag = new CompoundTag();

                            ResourceLocation id = blockEntity.getId();
                            blockEntityTag.putString("Id", id.toString());

                            blockEntityTag.putIntArray("Pos", new int[]{x, y, z});

                            for (Map.Entry<String, Tag<?>> entry : nbt.entrySet()) {
                                blockEntityTag.put(entry.getKey(), entry.getValue());
                            }

                            blockEntitiesTag.add(blockEntityTag);
                        }
                    } else {
                        BlockState blockState = new BlockState(Constants.DEFAULT_BLOCK);
                        if (!blockPalette.contains(blockState)) {
                            blockPalette.add(blockState);
                            blockPaletteTag.putInt(blockState.getId().toString(), blockPalette.indexOf(blockState));
                        }
                        int airIndex = schematic.getBlockPalette().indexOf(blockState);
                        blocksList.set(index, (byte) airIndex);
                    }
                }
            }
        }

        byte[] blocks = Bytes.toArray(blocksList);
        root.putByteArray("BlockData", blocks);
        if (blockEntitiesTag.size() > 0) {
            root.put("BlockEntities", blockEntitiesTag);
        }

        if (schematic.hasBiomes()) {
            // Copied to make the original schematic's palette not affected from operations (I.E. adding minecraft:ocean to it)
            List<BiomeState> biomePalette = new ArrayList<>(schematic.getBiomePalette());

            root.putInt("BiomePaletteMax", biomePalette.size());

            CompoundTag biomePaletteTag = new CompoundTag();

            for (int i = 0; i < biomePalette.size(); i++) {
                BiomeState biomeState = biomePalette.get(i);

                biomePaletteTag.putInt(biomeState.getId().toString(), i);
            }

            root.put("BiomePalette", biomePaletteTag);

            List<Byte> biomesList = Arrays.asList(new Byte[size[0] * size[1] * size[2]]);
            for (int y = 0; y < size[1]; y++) {
                for (int z = 0; z < size[2]; z++) {
                    for (int x = 0; x < size[0]; x++) {
                        int index = y * size[2] * size[0] + z * size[0] + x;
                        Biome biome = schematic.getBiome(x, y, z);
                        if (biome != null) {
                            biomesList.set(index, (byte) biome.getBiomeStateIndex());
                        } else {
                            BiomeState biomeState = new BiomeState(Constants.DEFAULT_BIOME);
                            if (!biomePalette.contains(biomeState)) {
                                biomePalette.add(biomeState);
                                biomePaletteTag.putInt(biomeState.getId().toString(), biomePalette.indexOf(biomeState));
                            }
                            int oceanIndex = schematic.getBiomePalette().indexOf(biomeState);
                            biomesList.set(index, (byte) oceanIndex);
                        }
                    }
                }
            }

            byte[] biomes = Bytes.toArray(biomesList);
            root.putByteArray("BiomeData", biomes);
        }

        List<Entity> entities = schematic.getEntities();
        if (entities != null) {
            ListTag<CompoundTag> entitiesTag = new ListTag<>(CompoundTag.class);
            for (Entity entity : entities) {
                CompoundTag entityTag = new CompoundTag();

                double[] position = entity.getPosition();
                ListTag<DoubleTag> positionTag = new ListTag<>(DoubleTag.class);
                positionTag.addDouble(position[0]);
                positionTag.addDouble(position[1]);
                positionTag.addDouble(position[2]);
                entityTag.put("Pos", positionTag);

                ResourceLocation id = entity.getId();
                CompoundTag nbt = entity.getNbt().clone();

                entityTag.put("Pos", positionTag);
                entityTag.putString("Id", id.toString());
                for (Map.Entry<String, Tag<?>> entry : nbt.entrySet()) {
                    entityTag.put(entry.getKey(), entry.getValue());
                }

                entitiesTag.add(entityTag);
            }

            root.put("Entities", entitiesTag);
        }

        NamedTag namedTag = new NamedTag("Schematic", root);

        outputStream.writeTag(namedTag, Tag.DEFAULT_MAX_DEPTH);
    }

    private void writeV3(Schematic schematic) throws IOException {
        CompoundTag realRoot = new CompoundTag();
        CompoundTag root = new CompoundTag();
        realRoot.put("Schematic", root);

        root.putInt("Version", 3);

        root.putInt("DataVersion", schematic.getDataVersion());

        CompoundTag metadata = Objects.requireNonNullElse(schematic.getMetadata(), new CompoundTag());
        // Update the date to the current time
        metadata.putLong("Date", System.currentTimeMillis());
        try {
            // Attempt to convert old WorldEdit metadata to V3 metadata
            if (metadata.containsKey("WEOffsetX") || metadata.containsKey("WEOffsetY") || metadata.containsKey("WEOffsetZ")) {
                if (metadata.containsKey("WEOffsetX") && metadata.containsKey("WEOffsetY") && metadata.containsKey("WEOffsetZ")) {
                    int offsetX = metadata.getInt("WEOffsetX");
                    int offsetY = metadata.getInt("WEOffsetY");
                    int offsetZ = metadata.getInt("WEOffsetZ");
                    metadata.remove("WEOffsetX");
                    metadata.remove("WEOffsetY");
                    metadata.remove("WEOffsetZ");
                    int[] offset = {offsetX, offsetY, offsetZ};
                    CompoundTag worldEditMeta = metadata.containsKey("WorldEdit") ? metadata.getCompoundTag("WorldEdit") : new CompoundTag();
                    worldEditMeta.putIntArray("Offset", offset);
                    metadata.put("WorldEdit", worldEditMeta);
                }
            }
        } catch (ClassCastException e) {
            // Ignore the WorldEdit metadata and continue
        }

        root.put("Metadata", metadata);

        int[] size = schematic.getSize();
        root.putShort("Width", (short) size[0]);
        root.putShort("Height", (short) size[1]);
        root.putShort("Length", (short) size[2]);

        int[] offset = schematic.getOffset();
        if (offset[0] != 0 || offset[1] != 0 || offset[2] != 0) {
            root.putIntArray("Offset", offset);
        }

        CompoundTag blockContainer = new CompoundTag();

        // Copied to make the original schematic's palette not affected from operations (I.E. adding minecraft:air to it)
        List<BlockState> blockPalette = new ArrayList<>(schematic.getBlockPalette());

        CompoundTag blockPaletteTag = new CompoundTag();

        for (int i = 0; i < blockPalette.size(); i++) {
            BlockState blockState = blockPalette.get(i);

            blockPaletteTag.putInt(blockState.toString(), i);
        }

        blockContainer.put("Palette", blockPaletteTag);

        ListTag<CompoundTag> blockEntitiesTag = new ListTag<>(CompoundTag.class);
        List<Byte> blocksList = Arrays.asList(new Byte[size[0] * size[1] * size[2]]);
        for (int y = 0; y < size[1]; y++) {
            for (int z = 0; z < size[2]; z++) {
                for (int x = 0; x < size[0]; x++) {
                    int index = y * size[2] * size[0] + z * size[0] + x;
                    Block block = schematic.getBlock(x, y, z);
                    if (block != null) {
                        blocksList.set(index, (byte) block.getBlockStateIndex());

                        BlockEntity blockEntity = block.getBlockEntity();
                        if (!blockEntity.isEmpty()) {
                            CompoundTag nbt = blockEntity.getNbt().clone();
                            CompoundTag blockEntityTag = new CompoundTag();

                            ResourceLocation id = blockEntity.getId();
                            blockEntityTag.putString("Id", id.toString());

                            blockEntityTag.putIntArray("Pos", new int[]{x, y, z});

                            blockEntityTag.put("Data", nbt);

                            blockEntitiesTag.add(blockEntityTag);
                        }
                    } else {
                        BlockState blockState = new BlockState(Constants.DEFAULT_BLOCK);
                        if (!blockPalette.contains(blockState)) {
                            blockPalette.add(blockState);
                            blockPaletteTag.putInt(blockState.getId().toString(), blockPalette.indexOf(blockState));
                        }
                        int airIndex = schematic.getBlockPalette().indexOf(blockState);
                        blocksList.set(index, (byte) airIndex);
                    }
                }
            }
        }

        byte[] blocks = Bytes.toArray(blocksList);
        blockContainer.putByteArray("Data", blocks);
        if (blockEntitiesTag.size() > 0) {
            blockContainer.put("BlockEntities", blockEntitiesTag);
        }

        root.put("Blocks", blockContainer);

        if (schematic.hasBiomes()) {
            CompoundTag biomeContainer = new CompoundTag();

            // Copied to make the original schematic's palette not affected from operations (I.E. adding minecraft:ocean to it)
            List<BiomeState> biomePalette = new ArrayList<>(schematic.getBiomePalette());

            CompoundTag biomePaletteTag = new CompoundTag();

            for (int i = 0; i < biomePalette.size(); i++) {
                BiomeState biomeState = biomePalette.get(i);

                biomePaletteTag.putInt(biomeState.getId().toString(), i);
            }

            biomeContainer.put("Palette", biomePaletteTag);

            List<Byte> biomesList = Arrays.asList(new Byte[size[0] * size[1] * size[2]]);
            for (int y = 0; y < size[1]; y++) {
                for (int z = 0; z < size[2]; z++) {
                    for (int x = 0; x < size[0]; x++) {
                        int index = y * size[2] * size[0] + z * size[0] + x;
                        Biome biome = schematic.getBiome(x, y, z);
                        if (biome != null) {
                            biomesList.set(index, (byte) biome.getBiomeStateIndex());
                        } else {
                            BiomeState biomeState = new BiomeState(Constants.DEFAULT_BIOME);
                            if (!biomePalette.contains(biomeState)) {
                                biomePalette.add(biomeState);
                                biomePaletteTag.putInt(biomeState.getId().toString(), biomePalette.indexOf(biomeState));
                            }
                            int oceanIndex = schematic.getBiomePalette().indexOf(biomeState);
                            biomesList.set(index, (byte) oceanIndex);
                        }
                    }
                }
            }

            byte[] biomes = Bytes.toArray(biomesList);
            biomeContainer.putByteArray("Data", biomes);

            root.put("Biomes", biomeContainer);
        }

        // TODO Ensure that the entity stuff works correctly.

        List<Entity> entities = schematic.getEntities();
        if (entities != null) {
            ListTag<CompoundTag> entitiesTag = new ListTag<>(CompoundTag.class);
            for (Entity entity : entities) {
                CompoundTag entityTag = new CompoundTag();

                double[] position = entity.getPosition();
                ListTag<DoubleTag> positionTag = new ListTag<>(DoubleTag.class);
                positionTag.addDouble(position[0]);
                positionTag.addDouble(position[1]);
                positionTag.addDouble(position[2]);
                entityTag.put("Pos", positionTag);

                ResourceLocation id = entity.getId();
                CompoundTag nbt = entity.getNbt().clone();

                entityTag.put("Pos", positionTag);
                entityTag.putString("Id", id.toString());
                entityTag.put("Data", nbt);

                entitiesTag.add(entityTag);
            }

            root.put("Entities", entitiesTag);
        }

        NamedTag namedTag = new NamedTag("", realRoot);

        outputStream.writeTag(namedTag, Tag.DEFAULT_MAX_DEPTH);
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
