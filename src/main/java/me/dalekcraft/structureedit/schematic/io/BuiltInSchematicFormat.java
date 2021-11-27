/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.dalekcraft.structureedit.schematic.io;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.dalekcraft.structureedit.exception.ValidationException;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// TODO Change WorldEdit's misusage of IOExceptions.
public enum BuiltInSchematicFormat implements SchematicFormat {

    /**
     * The Schematic format used by MCEdit.
     */
    MCEDIT_SCHEMATIC("mcedit", "mce", "schematic") {
        @Override
        public String getPrimaryFileExtension() {
            return "schematic";
        }

        @Override
        public SchematicReader getReader(InputStream inputStream) throws IOException {
            NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(inputStream));
            return new McEditSchematicReader(nbtInputStream);
        }

        @Override
        public SchematicWriter getWriter(OutputStream outputStream) throws IOException {
            throw new IOException("This format does not support saving");
        }

        @Override
        public boolean isFormat(File file) {
            try (NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)))) {
                NamedTag rootTag = nbtInputStream.readTag(Tag.DEFAULT_MAX_DEPTH);
                if (!rootTag.getName().equals("Schematic")) {
                    return false;
                }
                CompoundTag schematic = (CompoundTag) rootTag.getTag();

                // Check
                if (!schematic.containsKey("Materials")) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    },
    /**
     * The Schematic format used by Sponge.
     */
    /*SPONGE_V1_SCHEMATIC("sponge.1") {
        @Override
        public String getPrimaryFileExtension() {
            return "schem";
        }

        @Override
        public SchematicReader getReader(InputStream inputStream) throws IOException {
            NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(inputStream));
            return new SpongeSchematicV1Reader(nbtStream);
        }

        @Override
        public SchematicWriter getWriter(OutputStream outputStream) throws IOException {
            throw new IOException("This format does not support saving");
        }

        @Override
        public boolean isFormat(File file) {
            try (NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)))) {
                NamedTag rootTag = nbtInputStream.readTag(Tag.DEFAULT_MAX_DEPTH);
                if (!rootTag.getName().equals("Schematic")) {
                    return false;
                }
                CompoundTag schematic = (CompoundTag) rootTag.getTag();

                // Check
                Tag<?> versionTag = schematic.get("Version");
                if (!(versionTag instanceof IntTag) || ((IntTag) versionTag).asInt() != 1) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        }
    },
    SPONGE_V2_SCHEMATIC("sponge.2") {
        @Override
        public String getPrimaryFileExtension() {
            return "schem";
        }

        @Override
        public SchematicReader getReader(InputStream inputStream) throws IOException {
            NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(inputStream));
            return new SpongeSchematicV2Reader(nbtInputStream);
        }

        @Override
        public SchematicWriter getWriter(OutputStream outputStream) throws IOException {
            NBTOutputStream nbtOutputStream = new NBTOutputStream(new GZIPOutputStream(outputStream));
            return new SpongeSchematicV2Writer(nbtOutputStream);
        }

        @Override
        public boolean isFormat(File file) {
            try (NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)))) {
                NamedTag rootTag = nbtInputStream.readTag(Tag.DEFAULT_MAX_DEPTH);
                if (!rootTag.getName().equals("Schematic")) {
                    return false;
                }
                CompoundTag schematic = (CompoundTag) rootTag.getTag();

                // Check
                Tag<?> versionTag = schematic.get("Version");
                if (!(versionTag instanceof IntTag) || ((IntTag) versionTag).asInt() != 2) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        }
    },
    SPONGE_V3_SCHEMATIC("sponge.3", "sponge", "schem") {
        @Override
        public String getPrimaryFileExtension() {
            return "schem";
        }

        @Override
        public SchematicReader getReader(InputStream inputStream) throws IOException {
            NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(inputStream));
            return new SpongeSchematicV3Reader(nbtStream);
        }

        @Override
        public SchematicWriter getWriter(OutputStream outputStream) throws IOException {
            NBTOutputStream nbtStream = new NBTOutputStream(new GZIPOutputStream(outputStream));
            return new SpongeSchematicV3Writer(nbtStream);
        }

        @Override
        public boolean isFormat(File file) {
            try (NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)))) {
                NamedTag rootTag = nbtInputStream.readTag(Tag.DEFAULT_MAX_DEPTH);
                CompoundTag rootCompoundTag = (CompoundTag) rootTag.getTag();
                if (!rootCompoundTag.containsKey("Schematic")) {
                    return false;
                }
                Tag<?> schematic = rootCompoundTag.get("Schematic");
                if (!(schematic instanceof CompoundTag)) {
                    return false;
                }

                // Check
                Tag<?> versionTag = ((CompoundTag) schematic).get("Version");
                if (!(versionTag instanceof IntTag) || ((IntTag) versionTag).asInt() != 3) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        }
    },*/
    SPONGE_SCHEMATIC("sponge", "schem") {
        @Override
        public String getPrimaryFileExtension() {
            return "schem";
        }

        @Override
        public SchematicReader getReader(InputStream inputStream) throws IOException {
            NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(inputStream));
            return new SpongeSchematicReader(nbtInputStream);
        }

        @Override
        public SchematicWriter getWriter(OutputStream outputStream) throws IOException {
            NBTOutputStream nbtOutputStream = new NBTOutputStream(new GZIPOutputStream(outputStream));
            return new SpongeSchematicWriter(nbtOutputStream);
        }

        @Override
        public boolean isFormat(File file) {
            try (NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)))) {
                NamedTag rootTag = nbtInputStream.readTag(Tag.DEFAULT_MAX_DEPTH);
                CompoundTag schematic = (CompoundTag) rootTag.getTag();
                CompoundTag baseTag;
                if (!rootTag.getName().equals("Schematic")) {
                    baseTag = getRoot(schematic);
                } else {
                    baseTag = schematic;
                }

                // Check
                if (!baseTag.containsKey("Version")) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        }

        private CompoundTag getRoot(@NotNull CompoundTag root) throws ValidationException {
            if (root.containsKey("Schematic")) {
                String key = "Schematic";
                Class<CompoundTag> expected = CompoundTag.class;
                if (!root.containsKey(key)) {
                    throw new ValidationException("Schematic file is missing a \"" + key + "\" tag of type " + expected.getName());
                }

                Tag<?> tag = root.get(key);
                if (!expected.isInstance(tag)) {
                    throw new ValidationException(key + " tag is not of tag type " + expected.getName() + ", got " + tag.getClass().getName() + " instead");
                }

                root = expected.cast(tag);
            }
            return root;
        }
    },
    /**
     * The Schematic format used by Eccentric Devotion's TARDIS plugin.
     */
    TARDIS_SCHEMATIC("tschm") {
        @Override
        public String getPrimaryFileExtension() {
            return "tschm";
        }

        @Override
        public SchematicReader getReader(InputStream inputStream) throws IOException {
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            return new TardisSchematicReader(gzipInputStream);
        }

        @Override
        public SchematicWriter getWriter(OutputStream outputStream) throws IOException {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            return new TardisSchematicWriter(gzipOutputStream);
        }

        @Override
        public boolean isFormat(File file) {
            try (GZIPInputStream nbtInputStream = new GZIPInputStream(new FileInputStream(file))) {
                JsonObject root = JsonParser.parseReader(new InputStreamReader(nbtInputStream)).getAsJsonObject();

                // Check
                // TODO How to check for this format?
            } catch (Exception e) {
                return false;
            }

            // TODO

            return true;
        }
    },
    /**
     * The Schematic format used by vanilla Minecraft.
     */
    VANILLA_STRUCTURE("nbt") {
        @Override
        public String getPrimaryFileExtension() {
            return "nbt";
        }

        @Override
        public SchematicReader getReader(InputStream inputStream) throws IOException {
            NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(inputStream));
            return new StructureReader(nbtInputStream);
        }

        @Override
        public SchematicWriter getWriter(OutputStream outputStream) throws IOException {
            NBTOutputStream nbtOutputStream = new NBTOutputStream(new GZIPOutputStream(outputStream));
            return new StructureWriter(nbtOutputStream);
        }

        @Override
        public boolean isFormat(File file) {
            try (NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)))) {
                NamedTag rootTag = nbtInputStream.readTag(Tag.DEFAULT_MAX_DEPTH);
                if (!rootTag.getName().equals("")) {
                    return false;
                }
                CompoundTag schematic = (CompoundTag) rootTag.getTag();

                // Check
                if (!schematic.containsKey("DataVersion")) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    };

    private final ImmutableSet<String> aliases;

    BuiltInSchematicFormat(String... aliases) {
        this.aliases = ImmutableSet.copyOf(aliases);
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Set<String> getAliases() {
        return this.aliases;
    }

    @Override
    public Set<String> getFileExtensions() {
        return ImmutableSet.of(getPrimaryFileExtension());
    }
}
