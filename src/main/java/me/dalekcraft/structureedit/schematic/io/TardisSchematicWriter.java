package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.schematic.container.Schematic;

import java.io.IOException;
import java.io.OutputStream;

public class TardisSchematicWriter extends JsonSchematicWriter {

    private final OutputStream outputStream;

    public TardisSchematicWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(Schematic schematic) throws IOException {

    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
