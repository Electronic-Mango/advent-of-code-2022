package aoc2022.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Input {
    public static String readFile(final String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
