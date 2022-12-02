package aoc2022.input;

import java.util.List;

import resource.ResourceLoader;

public final class InputLoader {
    private static final String INPUT_FILE_PATTERN = "inputs/%s/%s";
    private static final String DEFAULT_INPUT_FILE = "input";

    public static String read(final String day) {
        return read(day, DEFAULT_INPUT_FILE);
    }

    public static String read(final String day, final String file) {
        return ResourceLoader.loadResource(getPath(day, file));
    }

    public static List<String> readLines(final String day) {
        return readLines(day, DEFAULT_INPUT_FILE);
    }

    public static List<String> readLines(final String day, final String file) {
        return ResourceLoader.loadResourceLines(getPath(day, file));
    }

    private static String getPath(final String day, final String file) {
        return String.format(INPUT_FILE_PATTERN, day, file);
    }
}
