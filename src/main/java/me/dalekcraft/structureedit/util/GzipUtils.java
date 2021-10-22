/*
 * Copyright (C) 2021 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.dalekcraft.structureedit.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author eccentric_nz
 */
public final class GzipUtils {

    @Contract(value = " -> fail", pure = true)
    private GzipUtils() {
        throw new UnsupportedOperationException();
    }

    public static String read(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file); GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream); InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(inputStreamReader); StringWriter stringWriter = new StringWriter()) {
            while (bufferedReader.ready()) {
                stringWriter.write(bufferedReader.read());
            }
            return stringWriter.toString();
        }
    }

    public static String read(Path path) throws IOException {
        try (InputStream pathInputStream = Files.newInputStream(path); GZIPInputStream gzipInputStream = new GZIPInputStream(pathInputStream); InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(inputStreamReader); StringWriter stringWriter = new StringWriter()) {
            while (bufferedReader.ready()) {
                stringWriter.write(bufferedReader.read());
            }
            return stringWriter.toString();
        }
    }

    public static void write(@NotNull Object o, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file); GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8); BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            bufferedWriter.write(o.toString());
        }
    }

    public static void write(@NotNull Object o, Path path) throws IOException {
        try (OutputStream pathOutputStream = Files.newOutputStream(path); GZIPOutputStream gzipOutputStream = new GZIPOutputStream(pathOutputStream); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8); BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            bufferedWriter.write(o.toString());
        }
    }
}
