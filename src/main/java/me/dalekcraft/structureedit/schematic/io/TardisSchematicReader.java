package me.dalekcraft.structureedit.schematic.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.dalekcraft.structureedit.schematic.container.Block;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import me.dalekcraft.structureedit.schematic.container.Schematic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TardisSchematicReader extends JsonSchematicReader {

    private final InputStream inputStream;

    public TardisSchematicReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public Schematic read() throws IOException, ValidationException {
        Schematic schematic = new Schematic();

        JsonObject root = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();

        JsonObject dimensions = requireTag(root, "dimensions", JsonObject.class);
        int sizeX = requireTag(dimensions, "width", JsonElement.class).getAsInt();
        int sizeY = requireTag(dimensions, "height", JsonElement.class).getAsInt();
        int sizeZ = requireTag(dimensions, "length", JsonElement.class).getAsInt();
        schematic.setSize(sizeX, sizeY, sizeZ);

        JsonObject relative = optTag(root, "relative", JsonObject.class);
        if (relative != null) {
            int offsetX = requireTag(relative, "x", JsonElement.class).getAsInt();
            int offsetY = requireTag(relative, "y", JsonElement.class).getAsInt();
            int offsetZ = requireTag(relative, "z", JsonElement.class).getAsInt();
            schematic.setOffset(offsetX, offsetY, offsetZ);
        }

        JsonArray levels = requireTag(root, "input", JsonArray.class);
        if (!levels.isEmpty()) {
            for (int y = 0; y < levels.size(); y++) {
                JsonArray rows = requireTag(levels, y, JsonArray.class);
                if (!rows.isEmpty()) {
                    for (int x = 0; x < rows.size(); x++) {
                        JsonArray columns = requireTag(rows, x, JsonArray.class);
                        if (!columns.isEmpty()) {
                            for (int z = 0; z < columns.size(); z++) {
                                JsonObject blockTag = requireTag(columns, z, JsonObject.class);

                                String data = requireTag(blockTag, "data", JsonElement.class).getAsString();

                                BlockState blockState = BlockState.toBlockState(data);
                                if (!schematic.getBlockPalette().contains(blockState)) {
                                    schematic.getBlockPalette().add(blockState);
                                } else {
                                    blockState = schematic.getBlockState(schematic.getBlockPalette().indexOf(blockState));
                                }

                                Block block = new Block(schematic.getBlockPalette().indexOf(blockState));
                                schematic.setBlock(x, y, z, block);
                            }
                        }
                    }
                }
            }
        }

        return schematic;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
