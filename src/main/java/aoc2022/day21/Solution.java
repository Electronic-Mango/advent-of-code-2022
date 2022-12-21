package aoc2022.day21;

import aoc2022.input.InputLoader;
import one.util.streamex.StreamEx;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Map;

public final class Solution {
    private static final String ROOT = "root";
    private static final String HUMAN = "humn";

    public static void main(String[] args) {
        final var input = StreamEx.of(InputLoader.readLines("day21"))
                .map(line -> StreamEx.split(line, ": ").toListAndThen(Pair::fromCollection))
                .mapToEntry(Pair::getValue0, Pair::getValue1)
                .toMap();

        final var equation1 = expand(input, ROOT);
        final var result1 = new Expression(equation1).calculate();
        System.out.println(result1);

        input.computeIfPresent(ROOT, (key, value) -> value.replaceAll("[+\\-*/]", "="));
        input.computeIfPresent(HUMAN, (key, value) -> "x");
        final var equation2 = expand(input, ROOT);
        System.out.println(equation2);
        // Solving the final equation is left as an exercise for the reader.
    }

    private static String expand(final Map<String, String> input, final String start) {
        final var value = input.get(start);
        if (!value.contains(" ")) {
            return value;
        }
        final var operationElements = StreamEx.split(value, " ").toListAndThen(Triplet::fromCollection);
        final var monkey1 = operationElements.getValue0();
        final var monkey2 = operationElements.getValue2();
        final var operation = operationElements.getValue1();
        return String.format("(%s%s%s)", expand(input, monkey1), operation, expand(input, monkey2));
    }
}
