package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.exception.ValidationException;
import me.dalekcraft.structureedit.schematic.container.*;
import me.dalekcraft.structureedit.util.Constants;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

public class SpongeSchematicReader extends NbtSchematicReader {

    private final NBTInputStream inputStream;
    private int schematicVersion = -1;
    private int dataVersion = -1;

    public SpongeSchematicReader(NBTInputStream inputStream) {
        this.inputStream = Objects.requireNonNull(inputStream);
    }

    @Override
    public Schematic read() throws IOException, ValidationException {
        CompoundTag root = getRoot();

        return switch (schematicVersion) {
            case 1 -> readV1(root);
            case 2 -> readV2(root);
            case 3 -> readV3(root);
            default -> throw new ValidationException("Illegal Sponge schematic version: " + schematicVersion);
        };
    }

    @Override
    public OptionalInt getDataVersion() {
        try {
            CompoundTag root = getRoot();
            switch (schematicVersion) {
                case 1:
                    return OptionalInt.of(Constants.DATA_VERSION_MC_1_13_2);
                case 2:
                case 3:
                    dataVersion = requireTag(root, "DataVersion", IntTag.class).asInt();
                    if (dataVersion < 0) {
                        return OptionalInt.empty();
                    }
                    return OptionalInt.of(dataVersion);
            }
            return OptionalInt.empty();
        } catch (Exception e) {
            return OptionalInt.empty();
        }
    }

    private CompoundTag getRoot() throws IOException, ValidationException {
        CompoundTag root = (CompoundTag) inputStream.readTag(Tag.DEFAULT_MAX_DEPTH).getTag();
        if (root.containsKey("Schematic")) {
            root = requireTag(root, "Schematic", CompoundTag.class);
            schematicVersion = requireTag(root, "Version", IntTag.class).asInt();
        }
        return root;
    }

    private Schematic readV1(CompoundTag root) throws ValidationException {
        Schematic schematic = new Schematic();

        CompoundTag metadata = optTag(root, "Metadata", CompoundTag.class);
        schematic.setMetadata(metadata);

        short sizeX = requireTag(root, "Width", ShortTag.class).asShort();
        short sizeY = requireTag(root, "Height", ShortTag.class).asShort();
        short sizeZ = requireTag(root, "Length", ShortTag.class).asShort();
        schematic.setSize(sizeX, sizeY, sizeZ);

        int[] offset = optTag(root, "Offset", IntArrayTag.class).getValue();
        if (offset != null) {
            int offsetX = offset[0];
            int offsetY = offset[1];
            int offsetZ = offset[2];
            schematic.setOffset(offsetX, offsetY, offsetZ);
        }

        int paletteMax = requireTag(root, "PaletteMax", IntTag.class).asInt();

        CompoundTag palette = requireTag(root, "Palette", CompoundTag.class);

        for (String key : palette.keySet()) {
            int value = requireTag(palette, key, IntTag.class).asInt();

            int nameEndIndex = key.length();
            if (key.contains("[")) {
                nameEndIndex = key.indexOf('[');
            }

            String id = key.substring(0, nameEndIndex);

            String propertyString = key.substring(nameEndIndex).replace("[", "").replace("]", "");
            Map<String, String> propertyMap = BlockState.SPLITTER.split(propertyString);

            schematic.setBlockState(value, new BlockState(id, propertyMap));
        }

        byte[] blockData = requireTag(root, "BlockData", ByteArrayTag.class).getValue();
        for (int i = 0; i < blockData.length; i++) {
            // index = (y * length * width) + (z * width) + x
            int x = i % (sizeX * sizeZ) % sizeX;
            int y = i / (sizeX * sizeZ);
            int z = i % (sizeX * sizeZ) / sizeX;

            byte state = blockData[i];
            /*if (!palette.containsValue(new IntTag(state))) {
                throw new ValidationException("Key " + currentKey + " has invalid palette index " + state);
            }*/

            Block block = new Block(state);

            schematic.setBlock(x, y, z, block);
        }

        ListTag<?> tileEntities = optTag(root, "TileEntities", ListTag.class);
        if (tileEntities != null) {
            for (int i = 0; i < tileEntities.size(); i++) {
                CompoundTag tileEntity = requireTag(tileEntities, i, CompoundTag.class);
                int contentVersion = requireTag(tileEntity, "ContentVersion", IntTag.class).asInt();

                int[] position = requireTag(tileEntity, "Pos", IntArrayTag.class).getValue();
                int x = position[0];
                int y = position[1];
                int z = position[2];

                // TODO Use this, somehow.
                String id = requireTag(tileEntity, "Id", StringTag.class).getValue();

                Block block = schematic.getBlock(x, y, z);
                block.setNbt(tileEntity);
            }
        }

        return schematic;
    }

    private Schematic readV2(CompoundTag root) throws ValidationException {
        Schematic schematic = new Schematic();

        int dataVersion = requireTag(root, "DataVersion", IntTag.class).asInt();
        schematic.setDataVersion(dataVersion);

        CompoundTag metadata = optTag(root, "Metadata", CompoundTag.class);
        schematic.setMetadata(metadata);

        short sizeX = requireTag(root, "Width", ShortTag.class).asShort();
        short sizeY = requireTag(root, "Height", ShortTag.class).asShort();
        short sizeZ = requireTag(root, "Length", ShortTag.class).asShort();
        schematic.setSize(sizeX, sizeY, sizeZ);

        int[] offset = optTag(root, "Offset", IntArrayTag.class).getValue();
        if (offset != null) {
            int offsetX = offset[0];
            int offsetY = offset[1];
            int offsetZ = offset[2];
            schematic.setOffset(offsetX, offsetY, offsetZ);
        }

        int paletteMax = requireTag(root, "PaletteMax", IntTag.class).asInt();

        CompoundTag palette = requireTag(root, "Palette", CompoundTag.class);
        for (String key : palette.keySet()) {
            int value = requireTag(palette, key, IntTag.class).asInt();

            int nameEndIndex = key.length();
            if (key.contains("[")) {
                nameEndIndex = key.indexOf('[');
            }

            String id = key.substring(0, nameEndIndex);

            String propertyString = key.substring(nameEndIndex).replace("[", "").replace("]", "");
            Map<String, String> propertyMap = BlockState.SPLITTER.split(propertyString);

            schematic.setBlockState(value, new BlockState(id, propertyMap));
        }

        byte[] blockData = requireTag(root, "BlockData", ByteArrayTag.class).getValue();
        for (int i = 0; i < blockData.length; i++) {
            // index = (y * length * width) + (z * width) + x
            int x = i % (sizeX * sizeZ) % sizeX;
            int y = i / (sizeX * sizeZ);
            int z = i % (sizeX * sizeZ) / sizeX;

            byte state = blockData[i];
            /*if (!palette.containsValue(new IntTag(state))) {
                throw new ValidationException("Key " + currentKey + " has invalid palette index " + state);
            }*/

            Block block = new Block(state);

            schematic.setBlock(x, y, z, block);
        }

        ListTag<?> blockEntities = optTag(root, "BlockEntities", ListTag.class);
        if (blockEntities != null) {
            for (int i = 0; i < blockEntities.size(); i++) {
                CompoundTag blockEntity = requireTag(blockEntities, i, CompoundTag.class);

                int[] position = requireTag(blockEntity, "Pos", IntArrayTag.class).getValue();
                int x = position[0];
                int y = position[1];
                int z = position[2];

                String id = requireTag(blockEntity, "Id", StringTag.class).getValue();

                Block block = schematic.getBlock(x, y, z);
                block.setNbt(blockEntity);
            }
        }

        ListTag<?> entities = optTag(root, "Entities", ListTag.class);
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entityTag = requireTag(entities, i, CompoundTag.class);

                ListTag<?> position = requireTag(entityTag, "Pos", ListTag.class);
                double x = requireTag(position, 0, DoubleTag.class).asDouble();
                double y = requireTag(position, 1, DoubleTag.class).asDouble();
                double z = requireTag(position, 1, DoubleTag.class).asDouble();

                String id = requireTag(entityTag, "Id", StringTag.class).getValue();

                Entity entity = new Entity();
                entity.setPosition(x, y, z);
                entity.setId(id);
                entity.setNbt(entityTag);

                schematic.getEntities().add(entity);
            }
        }

        IntTag biomePaletteMaxTag = optTag(root, "BiomePaletteMax", IntTag.class);
        if (biomePaletteMaxTag != null) {
            int biomePaletteMax = biomePaletteMaxTag.asInt();
        }

        CompoundTag biomePalette = optTag(root, "BiomePalette", CompoundTag.class);

        ByteArrayTag biomeDataTag = optTag(root, "BiomeData", ByteArrayTag.class);

        if (biomePalette != null && biomeDataTag != null) {
            byte[] biomeData = biomeDataTag.getValue();

            for (String key : biomePalette.keySet()) {
                int value = requireTag(biomePalette, key, IntTag.class).asInt();

                schematic.setBiomeState(value, new BiomeState(key));
            }

            for (int i = 0; i < biomeData.length; i++) {
                // index = (y * length * width) + (z * width) + x
                int x = i % (sizeX * sizeZ) % sizeX;
                int y = i / (sizeX * sizeZ);
                int z = i % (sizeX * sizeZ) / sizeX;

                byte state = biomeData[i];
                /*if (!biomePalette.containsValue(new IntTag(state))) {
                    throw new ValidationException("Key " + currentKey + " has invalid palette index " + state);
                }*/

                Biome biome = new Biome(schematic.getBiomeState(state));
                schematic.setBiome(x, y, z, biome);
            }
        }

        return schematic;
    }

    private Schematic readV3(CompoundTag root) throws ValidationException {
        Schematic schematic = new Schematic();

        int dataVersion = requireTag(root, "DataVersion", IntTag.class).asInt();
        schematic.setDataVersion(dataVersion);

        CompoundTag metadata = optTag(root, "Metadata", CompoundTag.class);
        schematic.setMetadata(metadata);

        short sizeX = requireTag(root, "Width", ShortTag.class).asShort();
        short sizeY = requireTag(root, "Height", ShortTag.class).asShort();
        short sizeZ = requireTag(root, "Length", ShortTag.class).asShort();
        schematic.setSize(sizeX, sizeY, sizeZ);

        int[] offset = optTag(root, "Offset", IntArrayTag.class).getValue();
        if (offset != null) {
            int offsetX = offset[0];
            int offsetY = offset[1];
            int offsetZ = offset[2];
            schematic.setOffset(offsetX, offsetY, offsetZ);
        }

        CompoundTag blockContainer = optTag(root, "Blocks", CompoundTag.class);
        if (blockContainer != null) {
            CompoundTag palette = requireTag(blockContainer, "Palette", CompoundTag.class);
            for (String key : palette.keySet()) {
                int value = requireTag(palette, key, IntTag.class).asInt();

                int nameEndIndex = key.length();
                if (key.contains("[")) {
                    nameEndIndex = key.indexOf('[');
                }

                String id = key.substring(0, nameEndIndex);

                String propertyString = key.substring(nameEndIndex).replace("[", "").replace("]", "");
                Map<String, String> propertyMap = BlockState.SPLITTER.split(propertyString);

                schematic.setBlockState(value, new BlockState(id, propertyMap));
            }

            byte[] blockData = requireTag(blockContainer, "Data", ByteArrayTag.class).getValue();
            for (int i = 0; i < blockData.length; i++) {
                // index = (y * length * width) + (z * width) + x
                int x = i % (sizeX * sizeZ) % sizeX;
                int y = i / (sizeX * sizeZ);
                int z = i % (sizeX * sizeZ) / sizeX;

                byte state = blockData[i];
                /*if (!palette.containsValue(new IntTag(state))) {
                    throw new ValidationException("Key " + currentKey + " has invalid palette index " + state);
                }*/

                Block block = new Block(state);
                schematic.setBlock(x, y, z, block);
            }

            ListTag<?> blockEntities = optTag(blockContainer, "BlockEntities", ListTag.class);
            if (blockEntities != null) {
                for (int i = 0; i < blockEntities.size(); i++) {
                    CompoundTag blockEntity = requireTag(blockEntities, i, CompoundTag.class);

                    int[] position = requireTag(blockEntity, "Pos", IntArrayTag.class).getValue();
                    int x = position[0];
                    int y = position[1];
                    int z = position[2];

                    String id = requireTag(blockEntity, "Id", StringTag.class).getValue();

                    Block block = schematic.getBlock(x, y, z);
                    block.setNbt(blockEntity);
                }
            }
        }

        CompoundTag biomeContainer = optTag(root, "Biomes", CompoundTag.class);
        if (biomeContainer != null) {
            CompoundTag palette = requireTag(biomeContainer, "Palette", CompoundTag.class);
            for (String key : palette.keySet()) {
                int value = requireTag(palette, key, IntTag.class).asInt();

                schematic.setBiomeState(value, new BiomeState(key));
            }

            byte[] biomeData = requireTag(biomeContainer, "Data", ByteArrayTag.class).getValue();
            for (int i = 0; i < biomeData.length; i++) {
                // index = (y * length * width) + (z * width) + x
                int x = i % (sizeX * sizeZ) % sizeX;
                int y = i / (sizeX * sizeZ);
                int z = i % (sizeX * sizeZ) / sizeX;

                byte state = biomeData[i];
                /*if (!palette.containsValue(new IntTag(state))) {
                    throw new ValidationException("Key " + currentKey + " has invalid palette index " + state);
                }*/

                Biome biome = new Biome(schematic.getBiomeState(state));
                schematic.setBiome(x, y, z, biome);
            }
        }

        ListTag<?> entities = optTag(root, "Entities", ListTag.class);
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entityTag = requireTag(entities, i, CompoundTag.class);

                ListTag<?> position = requireTag(entityTag, "Pos", ListTag.class);
                double x = requireTag(position, 0, DoubleTag.class).asDouble();
                double y = requireTag(position, 1, DoubleTag.class).asDouble();
                double z = requireTag(position, 1, DoubleTag.class).asDouble();

                String id = requireTag(entityTag, "Id", StringTag.class).getValue();

                Entity entity = new Entity();
                entity.setPosition(x, y, z);
                entity.setId(id);
                entity.setNbt(entityTag);

                schematic.getEntities().add(entity);
            }
        }

        return schematic;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
