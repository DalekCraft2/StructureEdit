package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.schematic.container.Schematic;
import net.querz.nbt.io.NBTOutputStream;

import java.io.IOException;
import java.util.Objects;

public class StructureWriter extends NbtSchematicWriter {

    private final NBTOutputStream outputStream;

    public StructureWriter(NBTOutputStream outputStream) {
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    @Override
    public void write(Schematic schematic) throws IOException {

    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
