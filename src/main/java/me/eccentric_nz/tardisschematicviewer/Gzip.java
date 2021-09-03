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
package me.eccentric_nz.tardisschematicviewer;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author eccentric_nz
 */
public final class Gzip {

    private Gzip() {
        throw new UnsupportedOperationException();
    }

    public static void zip(String inString, String outString) {
        try (FileInputStream fileInputStream = new FileInputStream(inString); FileOutputStream fileOutputStream = new FileOutputStream(outString); GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream)) {
            byte[] buffer = new byte[1024 * 16];
            int len;
            while ((len = fileInputStream.read(buffer)) != -1) {
                gzipOutputStream.write(buffer, 0, len);
            }
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    public static JSONObject unzip(String inString) {
        String s = "";
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(inString)); InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8); StringWriter stringWriter = new StringWriter()) {
            char[] buffer = new char[1024 * 16];
            int len;
            while ((len = inputStreamReader.read(buffer)) > 0) {
                stringWriter.write(buffer, 0, len);
            }
            s = stringWriter.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return new JSONObject(s);
    }
}
