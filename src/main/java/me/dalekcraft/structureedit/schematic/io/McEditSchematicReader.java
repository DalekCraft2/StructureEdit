package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.exception.ValidationException;
import me.dalekcraft.structureedit.schematic.container.Schematic;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.*;

import java.io.IOException;
import java.util.OptionalInt;

public class McEditSchematicReader extends NbtSchematicReader {

    private final NBTInputStream inputStream;

    public McEditSchematicReader(NBTInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public Schematic read() throws IOException, ValidationException {
        Schematic schematic = new Schematic();

        CompoundTag root = (CompoundTag) inputStream.readTag(Tag.DEFAULT_MAX_DEPTH).getTag();

        short sizeX = requireTag(root, "Width", ShortTag.class).asShort();
        short sizeY = requireTag(root, "Height", ShortTag.class).asShort();
        short sizeZ = requireTag(root, "Length", ShortTag.class).asShort();
        schematic.setSize(sizeX, sizeY, sizeZ);

        String materials = requireTag(root, "Materials", StringTag.class).getValue();
        if (!materials.equals("Classic") && !materials.equals("Pocket") && !materials.equals("Alpha")) {
            throw new ValidationException("Materials tag is not \"Classic\", \"Pocket\", or \"Alpha\"");
        }

        byte[] blocks = requireTag(root, "Blocks", ByteArrayTag.class).getValue();

        ByteArrayTag addBlocksTag = optTag(root, "AddBlocks", ByteArrayTag.class);
        if (addBlocksTag != null) {
            byte[] addBlocks = addBlocksTag.getValue();
        }

        byte[] data = requireTag(root, "Data", ByteArrayTag.class).getValue();

        ListTag<?> entities = optTag(root, "Entities", ListTag.class);
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = requireTag(entities, i, CompoundTag.class);
            }
        }

        ListTag<?> tileEntities = optTag(root, "TileEntities", ListTag.class);
        if (tileEntities != null) {
            for (int i = 0; i < tileEntities.size(); i++) {
                CompoundTag tileEntity = requireTag(tileEntities, i, CompoundTag.class);
            }
        }

        return null;
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
