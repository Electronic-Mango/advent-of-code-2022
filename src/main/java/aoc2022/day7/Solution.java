package aoc2022.day7;

import java.util.*;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution {
    private static final int TOTAL_MEMORY = 70_000_000;
    private static final int REQUIRED_MEMORY = 30_000_000;

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day7", "input");
        final var fileTree = new LinkedHashMap<String, Directory>();
        final var rootDir = new Directory("/", null);
        fileTree.put(rootDir.getName(), rootDir);
        var currentDir = rootDir;
        for (final var command : input) {
            final var commandParameters = Splitter.on(' ').splitToList(command);
            if (commandParameters.get(0).equals("$")) {
                if (commandParameters.get(1).equals("cd")) {
                    final var dirName = commandParameters.get(2);
                    if (!dirName.equals("..")) {
                        final var newDir = new Directory(dirName, currentDir);
                        if (fileTree.containsKey(newDir.getFullPath())) {
                            currentDir = fileTree.get(newDir.getFullPath());
                        } else {
                            fileTree.put(newDir.getFullPath(), newDir);
                            currentDir.addSubDirectory(newDir);
                            currentDir = newDir;
                        }
                    } else {
                        currentDir = currentDir.getParent();
                    }
                }
            } else {
                if (NumberUtils.isParsable(commandParameters.get(0))) {
                    currentDir.addFile(NumberUtils.toInt(commandParameters.get(0)));
                }
            }
        }
        final var result1 = fileTree.values().stream().filter(dir -> dir.getFullSize() <= 100000)
                .mapToInt(Directory::getFullSize).sum();
        System.out.println("result=" + result1 + " expected=1086293 matches=" + (result1 == 1086293));
        final var freeMemory = TOTAL_MEMORY - rootDir.getFullSize();
        final var missingMemory = REQUIRED_MEMORY - freeMemory;
        final var result2 = fileTree.values().stream().filter(dir -> dir.getFullSize() > missingMemory)
                .mapToInt(Directory::getFullSize).min().orElseThrow();
        System.out.println("result=" + result2 + " expected=366028 matches=" + (result2 == 366028));
    }
}

@Getter
@RequiredArgsConstructor
//@ToString
class Directory {
    private final String name;
    private final Directory parent;
    private final List<Directory> subDirectories = new ArrayList<>();
    private int directSize = 0;

    public String getFullPath() {
        return parent == null ? name : parent.getFullPath() + name + "/";
    }

    public int getFullSize() {
        return directSize + subDirectories.stream().mapToInt(Directory::getFullSize).sum();
    }

    public void addSubDirectory(final Directory directory) {
        subDirectories.add(directory);
    }

    public void addFile(final int size) {
        directSize += size;
    }
}
