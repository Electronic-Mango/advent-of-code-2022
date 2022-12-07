package aoc2022.day7;

import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import aoc2022.input.InputLoader;

public final class Solution {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\$ (.+)");
    private static final Pattern CHANGE_DIR_COMMAND_PATTERN = Pattern.compile("\\$ cd (?<directory>.+)");
    private static final Pattern LIST_DIR_COMMAND_PATTERN = Pattern.compile("\\$ ls");
    private static final Pattern LIST_DIR_OUTPUT_PATTERN = Pattern.compile("dir (?<dirname>.+)");
    private static final Pattern LIST_FILE_OUTPUT_PATTERN = Pattern.compile("(?<filesize>\\d+) (?<filename>.+)");

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day7", "testinput");
        final var fileTree = new LinkedHashMap<String, Directory>();
        final var rootDir = new Directory("/", null);
        fileTree.put(rootDir.getName(), rootDir);
        var currentDir = rootDir;
        for (final var line : input) {
            final var lineSplit = List.of(line.split(" "));
            if (lineSplit.get(0).equals("$")) {
                if (lineSplit.get(1).equals("cd")) {
                    final var dirName = lineSplit.get(2);
                    if (!dirName.equals("..")) {
                        final var newDir = new Directory(dirName, currentDir);
                        fileTree.put(newDir.getName(), newDir);
                        currentDir = newDir;
                    } else {
                        currentDir = currentDir.getParent();
                    }
                }
            }
        }
        System.out.println(fileTree.entrySet());
        fileTree.values().forEach(dir -> System.out.println(dir.getFullPath()));
    }
}

@Getter
@RequiredArgsConstructor
@ToString
class Directory {
    private final String name;
    private final Directory parent;
    private final Set<File> files = new HashSet<>();

    public String getFullPath() {
        if (parent != null) {
            return parent.getFullPath() + name;
        } else {
            return name;
        }
    }

    public void add(final File file) {
        files.add(file);
    }

    public void addAll(final Collection<File> files) {
        this.files.addAll(files);
    }
}

record File(int size, String name) {}
