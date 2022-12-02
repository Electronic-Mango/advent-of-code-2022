package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;

import aoc2022.util.InputLoader;

public final class ResourceLoader {
    public static String loadResource(final String path) {
        try (final var inputStream = loadResourceStream(path)) {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> loadResourceLines(final String path) {
        try (final var inputStream = loadResourceStream(path)) {
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);
            return bufferedReader.lines().toList();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream loadResourceStream(final String path) {
        return InputLoader.class.getClassLoader().getResourceAsStream(path);
    }
}
