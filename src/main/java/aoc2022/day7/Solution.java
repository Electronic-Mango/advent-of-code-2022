package aoc2022.day7;

import java.util.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution {
    private static final int TOTAL_MEMORY = 70_000_000;
    private static final int REQUIRED_MEMORY = 30_000_000;

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day7", "input");
        final var mutableInput = new ArrayList<>(input);
        mutableInput.remove(0);
        final var rootDir = new Directory("", null);
        final var fileTree = new LinkedHashMap<String, Directory>();
        fileTree.put(rootDir.getName(), rootDir);
        var currentDir = rootDir;
        for (final var line : mutableInput) {
            final var lineSplit = List.of(line.split(" "));
            if (lineSplit.get(0).equals("$")) {
                if (lineSplit.get(1).equals("cd")) {
                    final var dirName = lineSplit.get(2);
                    if (!dirName.equals("..")) {
                        final var newDir = new Directory(dirName, currentDir);
                        if (fileTree.containsKey(newDir.getFullPath())) {
                            currentDir = fileTree.get(newDir.getFullPath());
                        } else {
                            fileTree.put(newDir.getFullPath(), newDir);
                            currentDir.add(newDir);
                            currentDir = newDir;
                        }
                    } else {
                        currentDir = currentDir.getParent();
                    }
                }
            } else {
                if (NumberUtils.isParsable(lineSplit.get(0))) {
                    final var file = new File(NumberUtils.toInt(lineSplit.get(0)), lineSplit.get(1));
                    currentDir.add(file);
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
    private final Set<Directory> subDirs = new HashSet<>();
    private final Set<File> files = new HashSet<>();

    public String getFullPath() {
        if (parent != null) {
            return parent.getFullPath() + name + "/";
        } else {
            return name;
        }
    }

    public int getFullSize() {
        final var fileSize = !files.isEmpty() ? files.stream().mapToInt(File::size).sum() : 0;
        final var subDirsSize = !subDirs.isEmpty() ? subDirs.stream().mapToInt(Directory::getFullSize).sum() : 0;
        return fileSize + subDirsSize;
    }

    public void add(Directory directory) {
        subDirs.add(directory);
    }

    public void add(final File file) {
        files.add(file);
    }
}

record File(int size, String name) {
}
