package aoc2022.day11;

import aoc2022.input.InputLoader;
import lombok.Getter;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongUnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Solution {
    private static final String MONKEY_SEPARATOR = System.lineSeparator() + System.lineSeparator();

    public static void main(final String[] args) {
        final var input = InputLoader.read("day11");
        solve(input, 3, 20);
        solve(input, 1, 10000);
    }

    private static void solve(final String input, final long boredWorryDecrease, final int rounds) {
        Monkey.reset();
        final var monkeys = StreamEx.split(input, MONKEY_SEPARATOR)
                .map(monkey -> MonkeyParser.parse(monkey, boredWorryDecrease))
                .toList();
        IntStreamEx.range(rounds).forEach(round -> monkeys.forEach(Monkey::handleAllItems));
        final var result = StreamEx.of(monkeys)
                .mapToLong(Monkey::getInspectedTimes)
                .reverseSorted()
                .limit(2)
                .reduce(Math::multiplyExact)
                .orElseThrow();
        System.out.println(result);
    }
}

final class Monkey {
    private static final List<Monkey> ALL_MONKEYS = new ArrayList<>();
    private static long COMMON_ITEM_MOD = 1;
    private final List<Long> items;
    private final LongUnaryOperator newWorryFunction;
    private final long boredWorryDecrease;
    private final long testDivisor;
    private final int targetTrue;
    private final int targetFalse;
    @Getter
    private long inspectedTimes = 0;

    public Monkey(final List<Long> items,
                  final LongUnaryOperator newWorryFunction,
                  final long boredWorryDecrease,
                  final long testDivisor,
                  final int targetTrue,
                  final int targetFalse) {
        this.items = items;
        this.newWorryFunction = newWorryFunction;
        this.boredWorryDecrease = boredWorryDecrease;
        this.testDivisor = testDivisor;
        this.targetTrue = targetTrue;
        this.targetFalse = targetFalse;
        ALL_MONKEYS.add(this);
        COMMON_ITEM_MOD *= testDivisor;
    }

    static void reset() {
        ALL_MONKEYS.clear();
        COMMON_ITEM_MOD = 1;
    }

    void handleAllItems() {
        items.forEach(this::handleItem);
        items.clear();
    }

    private void handleItem(final long item) {
        inspectedTimes++;
        final var newWorryValue = (newWorryFunction.applyAsLong(item) / boredWorryDecrease) % COMMON_ITEM_MOD;
        final var target = newWorryValue % testDivisor == 0 ? targetTrue : targetFalse;
        ALL_MONKEYS.get(target).items.add(newWorryValue);
    }
}

final class MonkeyParser {
    private static final Pattern MONKEY_STARTING_ITEMS_PATTERN = Pattern.compile("Starting items: ([\\d, ]+)");
    private static final Pattern MONKEY_OPERATION_PATTERN = Pattern.compile("Operation: new = old ([*+]) (\\w+)");
    private static final Pattern MONKEY_TEST_DIVISOR_PATTERN = Pattern.compile("Test: divisible by (\\d+)");
    private static final Pattern MONKEY_TARGET_TRUE_PATTERN = Pattern.compile("If true: throw to monkey (\\d+)");
    private static final Pattern MONKEY_TARGET_FALSE_PATTERN = Pattern.compile("If false: throw to monkey (\\d+)");

    static Monkey parse(final String input, final long boredWorryDecrease) {
        final var startingItems = parseStartingItems(input);
        final var worryFunction = parseNewWorryFunction(input);
        final var testDivisor = parseInt(input, MONKEY_TEST_DIVISOR_PATTERN);
        final var targetTrue = parseInt(input, MONKEY_TARGET_TRUE_PATTERN);
        final var targetFalse = parseInt(input, MONKEY_TARGET_FALSE_PATTERN);
        return new Monkey(startingItems, worryFunction, boredWorryDecrease, testDivisor, targetTrue, targetFalse);
    }

    private static List<Long> parseStartingItems(final String input) {
        final var matcher = MONKEY_STARTING_ITEMS_PATTERN.matcher(input);
        matcher.find();
        return StreamEx.split(matcher.group(1), ", ").map(Long::parseLong).collect(Collectors.toList());
    }

    private static LongUnaryOperator parseNewWorryFunction(final String input) {
        final var matcher = MONKEY_OPERATION_PATTERN.matcher(input);
        matcher.find();
        final var operation = matcher.group(1);
        final var value = matcher.group(2);
        if (operation.equals("+")) {
            return i -> i + operand(value).applyAsLong(i);
        } else {
            return i -> i * operand(value).applyAsLong(i);
        }
    }

    private static LongUnaryOperator operand(final String operand) {
        if (operand.equals("old")) {
            return i -> i;
        } else {
            return i -> Integer.parseInt(operand);
        }
    }

    private static int parseInt(final String input, final Pattern pattern) {
        final var matcher = pattern.matcher(input);
        matcher.find();
        return Integer.parseInt(matcher.group(1));
    }
}