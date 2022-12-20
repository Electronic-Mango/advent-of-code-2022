package aoc2022.input;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class InputLoader {
    private static final String INPUT_FILE_PATTERN = "inputs/%s/%s";
    private static final String DEFAULT_INPUT_FILE = "input";

    public static String read(final String day) {
        return read(day, DEFAULT_INPUT_FILE);
    }

    public static String read(final String day, final String file) {
        try {
            return Resources.toString(getUrl(day, file), Charset.defaultCharset());
        } catch (final IOException ignored) {
            return "";
        }
    }

    public static List<String> readLines(final String day) {
        return readLines(day, DEFAULT_INPUT_FILE);
    }

    public static List<String> readLines(final String day, final String file) {
        try {
            return Resources.readLines(getUrl(day, file), Charset.defaultCharset());
        } catch (final IOException ignored) {
            return new ArrayList<>();
        }
    }

    private static URL getUrl(final String day, final String file) {
        return Resources.getResource(String.format(INPUT_FILE_PATTERN, day, file));
    }
}
