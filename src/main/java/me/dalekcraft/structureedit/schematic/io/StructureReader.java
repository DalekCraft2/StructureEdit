package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.schematic.container.*;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

public class StructureReader extends NbtSchematicReader {

    private final NBTInputStream inputStream;

    public StructureReader(NBTInputStream inputStream) {
        this.inputStream = Objects.requireNonNull(inputStream);
    }

    @Override
    public Schematic read() throws IOException, ValidationException {
        Schematic schematic = new Schematic();

        CompoundTag root = (CompoundTag) inputStream.readTag(Tag.DEFAULT_MAX_DEPTH).getTag();

        OptionalInt dataVersion = getDataVersion();
        if (dataVersion.isPresent()) {
            schematic.setDataVersion(dataVersion.getAsInt());
        }

        ListTag<?> size = requireTag(root, "size", ListTag.class);
        int sizeX = requireTag(size, 0, IntTag.class).asInt();
        int sizeY = requireTag(size, 1, IntTag.class).asInt();
        int sizeZ = requireTag(size, 2, IntTag.class).asInt();
        schematic.setSize(sizeX, sizeY, sizeZ);

        ListTag<?> palettes = optTag(root, "palettes", ListTag.class);
        if (palettes != null) {
            for (int paletteIndex = 0; paletteIndex < palettes.size(); paletteIndex++) {
                ListTag<?> palette = requireTag(palettes, paletteIndex, ListTag.class);
                for (int index = 0; index < palette.size(); index++) {
                    CompoundTag state = requireTag(palette, index, CompoundTag.class);

                    String name = requireTag(state, "Name", StringTag.class).getValue();

                    CompoundTag properties = optTag(state, "Properties", CompoundTag.class);
                    Map<String, String> propertyMap = new HashMap<>();
                    if (properties != null) {
                        properties.entrySet().forEach(entry -> {
                            String value = "";
                            if (entry.getValue() instanceof StringTag stringTag) {
                                value = stringTag.getValue();
                            } else if (entry.getValue() instanceof IntTag intTag) {
                                value = String.valueOf(intTag.asInt());
                            }
                            propertyMap.put(entry.getKey(), value);
                        });
                    }

                    schematic.setBlockState(index, paletteIndex, new BlockState(name, propertyMap));
                }
            }
        } else {
            ListTag<?> palette = requireTag(root, "palette", ListTag.class);
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag state = requireTag(palette, i, CompoundTag.class);

                String name = requireTag(state, "Name", StringTag.class).getValue();

                CompoundTag properties = optTag(state, "Properties", CompoundTag.class);
                Map<String, String> propertyMap = new HashMap<>();
                if (properties != null) {
                    properties.entrySet().forEach(entry -> {
                        String value = "";
                        if (entry.getValue() instanceof StringTag stringTag) {
                            value = stringTag.getValue();
                        } else if (entry.getValue() instanceof IntTag intTag) {
                            value = String.valueOf(intTag.asInt());
                        }
                        propertyMap.put(entry.getKey(), value);
                    });
                }

                schematic.setBlockState(i, new BlockState(name, propertyMap));
            }
        }

        ListTag<?> blocks = requireTag(root, "blocks", ListTag.class);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag blockTag = requireTag(blocks, i, CompoundTag.class);

            int state = requireTag(blockTag, "state", IntTag.class).asInt();
            if (state >= schematic.getBlockPalette().size()) {
                throw new ValidationException("Entry at index " + i + " has invalid palette index " + state);
            }

            ListTag<?> position = requireTag(blockTag, "pos", ListTag.class);
            int x = requireTag(position, 0, IntTag.class).asInt();
            int y = requireTag(position, 1, IntTag.class).asInt();
            int z = requireTag(position, 2, IntTag.class).asInt();

            CompoundTag nbt = optTag(blockTag, "nbt", CompoundTag.class);
            BlockEntity blockEntity = null;
            if (nbt != null) {
                String id = requireTag(nbt, "id", StringTag.class).getValue();

                nbt.remove("id");

                blockEntity = new BlockEntity(id, nbt);
            }

            Block block = new Block(state, blockEntity);

            schematic.setBlock(x, y, z, block);
        }

        ListTag<?> entities = optTag(root, "entities", ListTag.class);
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entityTag = requireTag(entities, i, CompoundTag.class);

                ListTag<?> position = requireTag(entityTag, "pos", ListTag.class);
                double entityX = requireTag(position, 0, DoubleTag.class).asDouble();
                double entityY = requireTag(position, 1, DoubleTag.class).asDouble();
                double entityZ = requireTag(position, 2, DoubleTag.class).asDouble();

                ListTag<?> blockPosition = requireTag(entityTag, "blockPos", ListTag.class);
                int blockX = requireTag(blockPosition, 0, IntTag.class).asInt();
                int blockY = requireTag(blockPosition, 1, IntTag.class).asInt();
                int blockZ = requireTag(blockPosition, 2, IntTag.class).asInt();

                CompoundTag nbt = requireTag(entityTag, "nbt", CompoundTag.class);

                String id = requireTag(nbt, "id", StringTag.class).getValue();

                nbt.remove("id");

                Entity entity = new Entity(id, nbt);
                entity.setPosition(entityX, entityY, entityZ);

                schematic.getEntities().add(entity);
            }
        }

        return schematic;
    }

    @Override
    public OptionalInt getDataVersion() {
        try {
            CompoundTag root = (CompoundTag) inputStream.readTag(Tag.DEFAULT_MAX_DEPTH).getTag();
            int dataVersion = requireTag(root, "DataVersion", IntTag.class).asInt();
            if (dataVersion < 0) {
                return OptionalInt.empty();
            }
            return OptionalInt.of(dataVersion);
        } catch (Exception e) {
            return OptionalInt.empty();
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
