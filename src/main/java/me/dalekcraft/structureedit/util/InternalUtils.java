package me.dalekcraft.structureedit.util;

import org.jetbrains.annotations.Contract;

import java.io.*;
import java.nio.charset.StandardCharsets;


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
