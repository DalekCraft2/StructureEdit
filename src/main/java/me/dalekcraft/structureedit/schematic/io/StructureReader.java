package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.exception.ValidationException;
import me.dalekcraft.structureedit.schematic.container.Block;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import me.dalekcraft.structureedit.schematic.container.Entity;
import me.dalekcraft.structureedit.schematic.container.Schematic;
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

        int dataVersion = requireTag(root, "DataVersion", IntTag.class).asInt();
        schematic.setDataVersion(dataVersion);

        ListTag<?> size = requireTag(root, "size", ListTag.class);
        int sizeX = requireTag(size, 0, IntTag.class).asInt();
        int sizeY = requireTag(size, 1, IntTag.class).asInt();
        int sizeZ = requireTag(size, 2, IntTag.class).asInt();
        schematic.setSize(sizeX, sizeY, sizeZ);

        ListTag<?> palettes = optTag(root, "palettes", ListTag.class);
        if (palettes != null) {
            for (int i = 0; i < palettes.size(); i++) {
                ListTag<?> palette = requireTag(palettes, i, ListTag.class);
                for (int j = 0; j < palette.size(); j++) {
                    CompoundTag state = requireTag(palette, j, CompoundTag.class);
                    String name = requireTag(state, "Name", StringTag.class).getValue();
                    CompoundTag properties = optTag(state, "Properties", CompoundTag.class);
                    Map<String, String> propertyMap = new HashMap<>();
                    if (properties != null) {
                        properties.entrySet().forEach(entry -> {
                            String value = ((StringTag) entry.getValue()).getValue();
                            propertyMap.put(entry.getKey(), value);
                        });
                    }

                    // TODO Multiple palettes.
                    schematic.setBlockState(j, new BlockState(name, propertyMap));
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
                        String value = ((StringTag) entry.getValue()).getValue();
                        propertyMap.put(entry.getKey(), value);
                    });
                }
                schematic.setBlockState(i, new BlockState(name, propertyMap));
            }
        }

        ListTag<?> blocks = requireTag(root, "blocks", ListTag.class);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = requireTag(blocks, i, CompoundTag.class);
            int state = requireTag(block, "state", IntTag.class).asInt();

            ListTag<?> position = requireTag(block, "pos", ListTag.class);
            int x = requireTag(position, 0, IntTag.class).asInt();
            int y = requireTag(position, 1, IntTag.class).asInt();
            int z = requireTag(position, 2, IntTag.class).asInt();

            CompoundTag nbt = optTag(block, "nbt", CompoundTag.class);

            // TODO Ensure that the palette contains the state.
            Block blockObject = new Block(schematic.getBlockState(state), nbt);

            schematic.setBlock(x, y, z, blockObject);
        }

        ListTag<?> entities = optTag(root, "entities", ListTag.class);
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = requireTag(entities, i, CompoundTag.class);

                ListTag<?> position = requireTag(entity, "pos", ListTag.class);
                double entityX = requireTag(position, 0, DoubleTag.class).asDouble();
                double entityY = requireTag(position, 1, DoubleTag.class).asDouble();
                double entityZ = requireTag(position, 2, DoubleTag.class).asDouble();

                ListTag<?> blockPosition = requireTag(entity, "blockPos", ListTag.class);
                int blockX = requireTag(blockPosition, 0, IntTag.class).asInt();
                int blockY = requireTag(blockPosition, 1, IntTag.class).asInt();
                int blockZ = requireTag(blockPosition, 2, IntTag.class).asInt();

                CompoundTag nbt = requireTag(entity, "nbt", CompoundTag.class);

                String id = requireTag(nbt, "id", StringTag.class).getValue();

                Entity entityObject = new Entity();
                entityObject.setPosition(entityX, entityY, entityZ);
                entityObject.setId(id);
                entityObject.setNbt(nbt);
                schematic.getEntities().add(entityObject);
            }
        }

        return schematic;
    }

    @Override
    public OptionalInt getDataVersion() {
        return super.getDataVersion();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
