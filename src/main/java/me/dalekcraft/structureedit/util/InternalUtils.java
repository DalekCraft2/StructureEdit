package me.dalekcraft.structureedit.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public final class InternalUtils {

    @Contract(value = " -> fail", pure = true)
    private InternalUtils() {
        throw new UnsupportedOperationException();
    }

    public static String read(String s) throws IOException {
        try (InputStream inputStream = InternalUtils.class.getResourceAsStream(s); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(inputStreamReader); StringWriter stringWriter = new StringWriter()) {
            while (bufferedReader.ready()) {
                stringWriter.write(bufferedReader.read());
            }
            return stringWriter.toString();
        }
    }
}
