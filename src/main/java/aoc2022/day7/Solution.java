package aoc2022.day7;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution {
    private static final int MAX_DIRECTORY_SIZE = 100_000;
    private static final int TOTAL_MEMORY = 70_000_000;
    private static final int REQUIRED_MEMORY = 30_000_000;

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day7").stream().skip(1).toList();
        final var rootDirectory = traverseFileSystem(Lists.newLinkedList(input));
        final var result1 = rootDirectory.flatSizesStream().filter(size -> size <= MAX_DIRECTORY_SIZE).sum();
        System.out.println(result1);
        final var missingMemory = REQUIRED_MEMORY - (TOTAL_MEMORY - rootDirectory.getFullSize());
        final var result2 = rootDirectory.flatSizesStream().filter(size -> size > missingMemory).min().orElseThrow();
        System.out.println(result2);
    }

    private static Directory traverseFileSystem(final LinkedList<String> commands) {
        final var directory = new Directory();
        for (var command = ""; command != null && !command.contains("$ cd .."); command = commands.pollFirst()) {
            if (command.contains("$ cd")) {
                directory.addSubDirectory(traverseFileSystem(commands));
            } else {
                directory.addFile(NumberUtils.toInt(command.replaceAll("\\D+", "")));
            }
        }
        return directory;
    }
}

final class Directory {
    private final List<Directory> subDirectories = Lists.newArrayList();
    private int directSize = 0;

    void addSubDirectory(final Directory directory) {
        subDirectories.add(directory);
    }

    void addFile(final int size) {
        directSize += size;
    }

    int getFullSize() {
        return directSize + subDirectories.stream().mapToInt(Directory::getFullSize).sum();
    }

    IntStreamEx flatSizesStream() {
        return StreamEx.of(subDirectories)
                .map(Directory::flatSizesStream)
                .flatMap(IntStreamEx::boxed)
                .append(getFullSize())
                .mapToInt(Integer::valueOf);
    }
}
