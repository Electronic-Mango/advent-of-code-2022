package aoc2022.day5;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.linear.MatrixUtils;

import aoc2022.input.InputLoader;

public final class Solution {

    public static void main(final String[] args) {
        final var input = InputLoader.read("day5");
        final var splitInput = Splitter.on(System.lineSeparator() + System.lineSeparator())
                .splitToStream(input)
                .collect(Collectors.toCollection(LinkedList::new));
        final var stack1 = new Stack(splitInput.getFirst());
        final var stack2 = new Stack(splitInput.getFirst());
        for (final var operation : prepareProcedure(splitInput.getLast())) {
            stack1.moveCrates(operation, Deque::addLast);
            stack2.moveCrates(operation, Deque::addFirst);
        }
        System.out.println(stack1.getTopCrates());
        System.out.println(stack2.getTopCrates());
    }

    private static List<Operation> prepareProcedure(final String procedureInput) {
        final Pattern procedurePattern = Pattern.compile(".+ +(?<count>\\d+).+ (?<source>\\d+).+ (?<target>\\d+)");
        return procedureInput.lines()
                .map(procedurePattern::matcher)
                .filter(Matcher::matches)
                .map(matcher -> new Operation(matcher.group("count"), matcher.group("source"), matcher.group("target")))
                .toList();
    }
}

class Stack {
    private final List<? extends Deque<Character>> stack;

    Stack(final String stackInput) {
        final var inputArray = stackInput.lines()
                .map(line -> line.chars().mapToDouble(Double::valueOf).toArray())
                .toArray(double[][]::new);
        final var stackArray = MatrixUtils.createRealMatrix(inputArray).transpose().getData();
        stack = Arrays.stream(stackArray)
                .map(column -> Arrays.stream(column)
                        .mapToObj(crate -> (char) crate)
                        .filter(Character::isAlphabetic)
                        .collect(Collectors.toCollection(LinkedList::new)))
                .filter(column -> !column.isEmpty())
                .toList();
    }

    void moveCrates(final Operation operation, final BiConsumer<Deque<Character>, Character> intermediateInserter) {
        final Deque<Character> movedCrates = new LinkedList<>();
        final Deque<Character> sourceStack = stack.get(operation.getSource());
        for (int i = 0; i < operation.getCount(); ++i) {
            intermediateInserter.accept(movedCrates, sourceStack.pollFirst());
        }
        final Deque<Character> targetStack = stack.get(operation.getTarget());
        movedCrates.forEach(targetStack::addFirst);
    }

    String getTopCrates() {
        return stack.stream().map(Deque::peekFirst).map(String::valueOf).collect(Collectors.joining());
    }
}

@Getter
class Operation {
    private final int count;
    private final int source;
    private final int target;

    Operation(final String count, final String source, final String target) {
        this.count = NumberUtils.toInt(count);
        this.source = NumberUtils.toInt(source) - 1;
        this.target = NumberUtils.toInt(target) - 1;
    }
}
