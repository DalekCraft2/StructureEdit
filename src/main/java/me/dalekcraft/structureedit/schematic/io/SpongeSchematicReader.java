package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.schematic.container.*;
import me.dalekcraft.structureedit.util.Constants;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.*;

import java.io.IOException;
import java.util.Objects;
import java.util.OptionalInt;

public class SpongeSchematicReader extends NbtSchematicReader {

    private final NBTInputStream inputStream;
    private int schematicVersion = -1;

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
            return getDataVersion(root);
        } catch (Exception e) {
            return OptionalInt.empty();
        }
    }

    private OptionalInt getDataVersion(CompoundTag root) {
        try {
            switch (schematicVersion) {
                case 1 -> {
                    return OptionalInt.of(Constants.DATA_VERSION_MC_1_13_2);
                }
                case 2, 3 -> {
                    int dataVersion = requireTag(root, "DataVersion", IntTag.class).asInt();
                    if (dataVersion < 0) {
                        return OptionalInt.empty();
                    }
                    return OptionalInt.of(dataVersion);
                }
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
        }
        schematicVersion = requireTag(root, "Version", IntTag.class).asInt();
        return root;
    }

    private Schematic readV1(CompoundTag root) throws ValidationException {
        Schematic schematic = new Schematic();

        OptionalInt dataVersion = getDataVersion(root);
        if (dataVersion.isPresent()) {
            schematic.setDataVersion(dataVersion.getAsInt());
        }

        CompoundTag metadata = optTag(root, "Metadata", CompoundTag.class);
        schematic.setMetadata(metadata);

        short sizeX = requireTag(root, "Width", ShortTag.class).asShort();
        short sizeY = requireTag(root, "Height", ShortTag.class).asShort();
        short sizeZ = requireTag(root, "Length", ShortTag.class).asShort();
        schematic.setSize(sizeX, sizeY, sizeZ);

        IntArrayTag offsetTag = optTag(root, "Offset", IntArrayTag.class);
        if (offsetTag != null) {
            int[] offset = offsetTag.getValue();
            int offsetX = offset[0];
            int offsetY = offset[1];
            int offsetZ = offset[2];
            schematic.setOffset(offsetX, offsetY, offsetZ);
        }

        int blockPaletteMax = requireTag(root, "PaletteMax", IntTag.class).asInt();

        // TODO The Sponge specification says that palettes are actually optional, so I need to figure out how one would read a schematic without a palette.
        CompoundTag blockPalette = requireTag(root, "Palette", CompoundTag.class);
        if (blockPalette.size() != blockPaletteMax) {
            throw new ValidationException("Palette size does not match expected maximum");
        }

        for (String key : blockPalette.keySet()) {
            int value = requireTag(blockPalette, key, IntTag.class).asInt();

            schematic.setBlockState(value, BlockState.toBlockState(key));
        }

        byte[] blockData = requireTag(root, "BlockData", ByteArrayTag.class).getValue();
        if (blockData.length != sizeX * sizeY * sizeZ) {
            throw new ValidationException("BlockData length is " + blockData.length + "; should be " + sizeX * sizeY * sizeZ);
        }
        for (int i = 0; i < blockData.length; i++) {
            // index = (y * length * width) + (z * width) + x
            int x = i % (sizeX * sizeZ) % sizeX;
            int y = i / (sizeX * sizeZ);
            int z = i % (sizeX * sizeZ) / sizeX;

            byte state = blockData[i];
            if (!blockPalette.containsValue(new IntTag(state))) {
                throw new ValidationException("Entry at index " + i + " has invalid palette index " + state);
            }

            Block block = new Block(state);

            schematic.setBlock(x, y, z, block);
        }

        ListTag<?> tileEntities = optTag(root, "TileEntities", ListTag.class);
        if (tileEntities != null) {
            for (int i = 0; i < tileEntities.size(); i++) {
                CompoundTag tileEntity = requireTag(tileEntities, i, CompoundTag.class);

                // int contentVersion = requireTag(tileEntity, "ContentVersion", IntTag.class).asInt();
                tileEntity.remove("ContentVersion");

                int[] position = requireTag(tileEntity, "Pos", IntArrayTag.class).getValue();
                int x = position[0];
                int y = position[1];
                int z = position[2];

                String idString = requireTag(tileEntity, "Id", StringTag.class).getValue();
                ResourceLocation id = new ResourceLocation(idString);
                tileEntity.remove("Pos");
                tileEntity.remove("Id");

                Block block = schematic.getBlock(x, y, z);
                if (block != null) {
                    block.setBlockEntity(new BlockEntity(id, tileEntity));
                }
            }
        }

        return schematic;
    }

    private Schematic readV2(CompoundTag root) throws ValidationException {
        Schematic schematic = new Schematic();

        OptionalInt dataVersion = getDataVersion(root);
        if (dataVersion.isPresent()) {
            schematic.setDataVersion(dataVersion.getAsInt());
        }

        CompoundTag metadata = optTag(root, "Metadata", CompoundTag.class);
        schematic.setMetadata(metadata);

        short sizeX = requireTag(root, "Width", ShortTag.class).asShort();
        short sizeY = requireTag(root, "Height", ShortTag.class).asShort();
        short sizeZ = requireTag(root, "Length", ShortTag.class).asShort();
        schematic.setSize(sizeX, sizeY, sizeZ);

        IntArrayTag offsetTag = optTag(root, "Offset", IntArrayTag.class);
        if (offsetTag != null) {
            int[] offset = offsetTag.getValue();
            int offsetX = offset[0];
            int offsetY = offset[1];
            int offsetZ = offset[2];
            schematic.setOffset(offsetX, offsetY, offsetZ);
        }

        int blockPaletteMax = requireTag(root, "PaletteMax", IntTag.class).asInt();

        CompoundTag blockPalette = requireTag(root, "Palette", CompoundTag.class);
        if (blockPalette.size() != blockPaletteMax) {
            throw new ValidationException("Palette size does not match expected maximum");
        }

        for (String key : blockPalette.keySet()) {
            int value = requireTag(blockPalette, key, IntTag.class).asInt();

            schematic.setBlockState(value, BlockState.toBlockState(key));
        }

        byte[] blockData = requireTag(root, "BlockData", ByteArrayTag.class).getValue();
        if (blockData.length != sizeX * sizeY * sizeZ) {
            throw new ValidationException("BlockData length is " + blockData.length + "; should be " + sizeX * sizeY * sizeZ);
        }
        for (int i = 0; i < blockData.length; i++) {
            // index = (y * length * width) + (z * width) + x
            int x = i % (sizeX * sizeZ) % sizeX;
            int y = i / (sizeX * sizeZ);
            int z = i % (sizeX * sizeZ) / sizeX;

            byte state = blockData[i];
            if (!blockPalette.containsValue(new IntTag(state))) {
                throw new ValidationException("Entry at index " + i + " has invalid palette index " + state);
            }

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

                String idString = requireTag(blockEntity, "Id", StringTag.class).getValue();
                ResourceLocation id = new ResourceLocation(idString);

                blockEntity.remove("Pos");
                blockEntity.remove("Id");

                Block block = schematic.getBlock(x, y, z);
                if (block != null) {
                    block.setBlockEntity(new BlockEntity(id, blockEntity));
                }
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

                String idString = requireTag(entityTag, "Id", StringTag.class).getValue();
                ResourceLocation id = new ResourceLocation(idString);

                entityTag.remove("Pos");
                entityTag.remove("Id");

                Entity entity = new Entity(id, entityTag);
                entity.setPosition(x, y, z);

                schematic.getEntities().add(entity);
            }
        }

        IntTag biomePaletteMaxTag = optTag(root, "BiomePaletteMax", IntTag.class);

        CompoundTag biomePalette = optTag(root, "BiomePalette", CompoundTag.class);
        if (biomePalette != null && biomePaletteMaxTag != null && biomePalette.size() != biomePaletteMaxTag.asInt()) {
            throw new ValidationException("Palette size does not match expected maximum");
        }

        ByteArrayTag biomeDataTag = optTag(root, "BiomeData", ByteArrayTag.class);

        if (biomePalette != null && biomeDataTag != null) {
            byte[] biomeData = biomeDataTag.getValue();
            if (biomeData.length != sizeX * sizeY * sizeZ) {
                throw new ValidationException("BiomeData length is " + biomeData.length + "; should be " + sizeX * sizeY * sizeZ);
            }

            for (String key : biomePalette.keySet()) {
                int value = requireTag(biomePalette, key, IntTag.class).asInt();

                schematic.setBiomeState(value, new BiomeState(new ResourceLocation(key)));
            }

            for (int i = 0; i < biomeData.length; i++) {
                // index = (y * length * width) + (z * width) + x
                int x = i % (sizeX * sizeZ) % sizeX;
                int y = i / (sizeX * sizeZ);
                int z = i % (sizeX * sizeZ) / sizeX;

                byte state = biomeData[i];
                if (!biomePalette.containsValue(new IntTag(state))) {
                    throw new ValidationException("Entry at index " + i + " has invalid palette index " + state);
                }

                Biome biome = new Biome(state);
                schematic.setBiome(x, y, z, biome);
            }
        }

        return schematic;
    }

    private Schematic readV3(CompoundTag root) throws ValidationException {
        Schematic schematic = new Schematic();

        OptionalInt dataVersion = getDataVersion(root);
        if (dataVersion.isPresent()) {
            schematic.setDataVersion(dataVersion.getAsInt());
        }

        CompoundTag metadata = optTag(root, "Metadata", CompoundTag.class);
        schematic.setMetadata(metadata);

        short sizeX = requireTag(root, "Width", ShortTag.class).asShort();
        short sizeY = requireTag(root, "Height", ShortTag.class).asShort();
        short sizeZ = requireTag(root, "Length", ShortTag.class).asShort();
        schematic.setSize(sizeX, sizeY, sizeZ);

        IntArrayTag offsetTag = optTag(root, "Offset", IntArrayTag.class);
        if (offsetTag != null) {
            int[] offset = offsetTag.getValue();
            int offsetX = offset[0];
            int offsetY = offset[1];
            int offsetZ = offset[2];
            schematic.setOffset(offsetX, offsetY, offsetZ);
        }

        CompoundTag blockContainer = optTag(root, "Blocks", CompoundTag.class);
        if (blockContainer != null) {
            CompoundTag blockPalette = requireTag(blockContainer, "Palette", CompoundTag.class);
            for (String key : blockPalette.keySet()) {
                int value = requireTag(blockPalette, key, IntTag.class).asInt();

                schematic.setBlockState(value, BlockState.toBlockState(key));
            }

            byte[] blockData = requireTag(blockContainer, "Data", ByteArrayTag.class).getValue();
            if (blockData.length != sizeX * sizeY * sizeZ) {
                throw new ValidationException("BlockData length is " + blockData.length + "; should be " + sizeX * sizeY * sizeZ);
            }
            for (int i = 0; i < blockData.length; i++) {
                // index = (y * length * width) + (z * width) + x
                int x = i % (sizeX * sizeZ) % sizeX;
                int y = i / (sizeX * sizeZ);
                int z = i % (sizeX * sizeZ) / sizeX;

                byte state = blockData[i];
                if (!blockPalette.containsValue(new IntTag(state))) {
                    throw new ValidationException("Entry at index " + i + " has invalid palette index " + state);
                }

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

                    String idString = requireTag(blockEntity, "Id", StringTag.class).getValue();
                    ResourceLocation id = new ResourceLocation(idString);

                    CompoundTag nbt = optTag(blockEntity, "Data", CompoundTag.class);
                    if (nbt != null) {
                        nbt.remove("Pos");
                        nbt.remove("id");
                    }

                    Block block = schematic.getBlock(x, y, z);
                    if (block != null) {
                        block.setBlockEntity(new BlockEntity(id, nbt));
                    }
                }
            }
        }

        CompoundTag biomeContainer = optTag(root, "Biomes", CompoundTag.class);
        if (biomeContainer != null) {
            CompoundTag biomePalette = requireTag(biomeContainer, "Palette", CompoundTag.class);
            for (String key : biomePalette.keySet()) {
                int value = requireTag(biomePalette, key, IntTag.class).asInt();

                schematic.setBiomeState(value, new BiomeState(new ResourceLocation(key)));
            }

            byte[] biomeData = requireTag(biomeContainer, "Data", ByteArrayTag.class).getValue();
            if (biomeData.length != sizeX * sizeY * sizeZ) {
                throw new ValidationException("BiomeData length is " + biomeData.length + "; should be " + sizeX * sizeY * sizeZ);
            }
            for (int i = 0; i < biomeData.length; i++) {
                // index = (y * length * width) + (z * width) + x
                int x = i % (sizeX * sizeZ) % sizeX;
                int y = i / (sizeX * sizeZ);
                int z = i % (sizeX * sizeZ) / sizeX;

                byte state = biomeData[i];
                if (!biomePalette.containsValue(new IntTag(state))) {
                    throw new ValidationException("Entry at index " + i + " has invalid palette index " + state);
                }

                Biome biome = new Biome(state);
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

                String idString = requireTag(entityTag, "Id", StringTag.class).getValue();
                ResourceLocation id = new ResourceLocation(idString);

                CompoundTag nbt = optTag(entityTag, "Data", CompoundTag.class);
                if (nbt != null) {
                    nbt.remove("Pos");
                    nbt.remove("id");
                }

                Entity entity = new Entity(id, nbt);
                entity.setPosition(x, y, z);

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
