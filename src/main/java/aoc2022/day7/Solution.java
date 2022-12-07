package aoc2022.day7;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution {
    private static final int MAX_DIR_SIZE = 100_000;
    private static final int TOTAL_MEMORY = 70_000_000;
    private static final int REQUIRED_MEMORY = 30_000_000;

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day7").stream().skip(1).toList();
        final var rootDirectory = traverseFileSystemAndReturnRoot(input);
        final var result1 = rootDirectory.flatSizesStream().filter(size -> size <= MAX_DIR_SIZE).sum();
        System.out.println(result1);
        final var missingMemory = REQUIRED_MEMORY - (TOTAL_MEMORY - rootDirectory.getFullSize());
        final var result2 = rootDirectory.flatSizesStream().filter(size -> size > missingMemory).min().orElseThrow();
        System.out.println(result2);
    }

    private static Directory traverseFileSystemAndReturnRoot(final List<String> commands) {
        final var rootDirectory = new Directory(null);
        var currentDirectory = rootDirectory;
        for (final var command : commands) {
            currentDirectory = handleCommand(command, currentDirectory);
        }
        return rootDirectory;
    }

    private static Directory handleCommand(final String command, final Directory currentDirectory) {
        if (command.contains("$ cd ..")) {
            return currentDirectory.getParent();
        } else if (command.contains("$ cd")) {
            final var directory = new Directory(currentDirectory);
            currentDirectory.addSubDirectory(directory);
            return directory;
        } else {
            currentDirectory.addFile(NumberUtils.toInt(command.replaceAll("\\D+", "")));
            return currentDirectory;
        }
    }
}

@RequiredArgsConstructor
final class Directory {
    @Getter
    private final Directory parent;
    private final List<Directory> subDirectories = new ArrayList<>();
    private int directSize = 0;

    int getFullSize() {
        return directSize + subDirectories.stream().mapToInt(Directory::getFullSize).sum();
    }

    void addSubDirectory(final Directory directory) {
        subDirectories.add(directory);
    }

    void addFile(final int size) {
        directSize += size;
    }

    IntStreamEx flatSizesStream() {
        return StreamEx.of(subDirectories)
                .map(Directory::flatSizesStream)
                .flatMap(IntStreamEx::boxed)
                .append(getFullSize())
                .mapToInt(Integer::valueOf);
    }
}
