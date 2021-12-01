package me.dalekcraft.structureedit.schematic.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.dalekcraft.structureedit.schematic.container.Block;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import me.dalekcraft.structureedit.schematic.container.Schematic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class TardisSchematicWriter extends JsonSchematicWriter {

    private final OutputStream outputStream;

    public TardisSchematicWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(Schematic schematic) throws IOException {
        JsonObject root = new JsonObject();

        int[] size = schematic.getSize();

        JsonArray input = new JsonArray();
        for (int y = 0; y < size[1]; y++) {
            JsonArray row = new JsonArray();
            input.add(row);
            for (int x = 0; x < size[0]; x++) {
                JsonArray column = new JsonArray();
                row.add(column);
                for (int z = 0; z < size[2]; z++) {
                    JsonObject blockTag = new JsonObject();

                    BlockState blockState = new BlockState("minecraft:air");

                    Block block = schematic.getBlock(x, y, z);
                    if (block != null) {
                        blockState = schematic.getBlockState(block.getBlockStateIndex());
                    }

                    String properties = blockState.getProperties().isEmpty() ? "" : "[" + BlockState.JOINER.join(blockState.getProperties()) + "]";

                    blockTag.add("data", new JsonPrimitive(blockState.getId() + properties));

                    column.add(blockTag);
                }
            }
        }
        root.add("input", input);

        JsonObject dimensions = new JsonObject();
        dimensions.add("width", new JsonPrimitive(size[0]));
        dimensions.add("height", new JsonPrimitive(size[1]));
        dimensions.add("length", new JsonPrimitive(size[2]));
        root.add("dimensions", dimensions);

        int[] offset = schematic.getOffset();
        if (offset[0] != 0 || offset[1] != 0 || offset[2] != 0) {
            JsonObject relative = new JsonObject();
            relative.add("x", new JsonPrimitive(offset[0]));
            relative.add("y", new JsonPrimitive(offset[1]));
            relative.add("z", new JsonPrimitive(offset[2]));
            root.add("relative", relative);
        }

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
            outputStreamWriter.write(root.toString());
        }
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
