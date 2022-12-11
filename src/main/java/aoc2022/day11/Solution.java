package aoc2022.day11;

import aoc2022.input.InputLoader;
import com.google.common.base.Splitter;
import lombok.Getter;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.function.LongUnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Solution {
    public static void main(final String[] args) {
        final var input = InputLoader.read("day11");
        solve(input, 3, 20);
        Monkey.resetCommonItemMod();
        solve(input, 1, 10000);
    }

    private static void solve(final String input, final long boredWorryDecrease, final int rounds) {
        final var monkeys = Splitter.on(System.lineSeparator() + System.lineSeparator())
                .splitToStream(input)
                .map(monkey -> MonkeyParser.parse(monkey, boredWorryDecrease))
                .toList();
        for (int round = 0; round < rounds; ++round) {
            for (final var monkey : monkeys) {
                for (final var thrownItem : monkey.handleAllItems()) {
                    monkeys.get(thrownItem.nextMonkeyId()).addItem(thrownItem.item());
                }
            }
        }
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
    private static long COMMON_ITEM_MOD = 1;
    private final List<Long> items;
    private final LongUnaryOperator newWorryFunction;
    private final long testDivisor;
    private final int targetTrue;
    private final int targetFalse;
    private final long boredWorryDecrease;
    @Getter
    private long inspectedTimes = 0;

    public Monkey(final List<Long> items,
                  final LongUnaryOperator newWorryFunction,
                  final long testDivisor,
                  final int targetTrue,
                  final int targetFalse,
                  long boredWorryDecrease) {
        this.items = items;
        this.newWorryFunction = newWorryFunction;
        this.testDivisor = testDivisor;
        this.boredWorryDecrease = boredWorryDecrease;
        this.targetTrue = targetTrue;
        this.targetFalse = targetFalse;
        COMMON_ITEM_MOD *= testDivisor;
    }

    static void resetCommonItemMod() {
        COMMON_ITEM_MOD = 1;
    }

    List<ThrownItem> handleAllItems() {
        final var nextMonkeyData = items.stream().map(this::handleItem).toList();
        items.clear();
        return nextMonkeyData;
    }

    ThrownItem handleItem(final long item) {
        inspectedTimes++;
        final var newWorryValue = (newWorryFunction.applyAsLong(item) / boredWorryDecrease) % COMMON_ITEM_MOD;
        final var target = newWorryValue % testDivisor == 0 ? targetTrue : targetFalse;
        return new ThrownItem(target, newWorryValue);
    }

    void addItem(final long item) {
        items.add(item);
    }
}

record ThrownItem(int nextMonkeyId, long item) { }

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
        return new Monkey(startingItems, worryFunction, testDivisor, targetTrue, targetFalse, boredWorryDecrease);
    }

    private static int parseInt(final String input, final Pattern pattern) {
        final var matcher = pattern.matcher(input);
        matcher.find();
        return NumberUtils.toInt(matcher.group(1));
    }

    private static List<Long> parseStartingItems(final String input) {
        final var matcher = MONKEY_STARTING_ITEMS_PATTERN.matcher(input);
        matcher.find();
        final var startingItems = matcher.group(1);
        return Splitter.on(", ")
                .splitToStream(startingItems)
                .map(NumberUtils::toLong)
                .collect(Collectors.toList());
    }

    private static LongUnaryOperator parseNewWorryFunction(final String input) {
        final var matcher = MONKEY_OPERATION_PATTERN.matcher(input);
        matcher.find();
        final var operation = matcher.group(1);
        final var value = matcher.group(2);
        if (value.equals("old")) {
            return i -> i * i;
        } else if (operation.equals("+")) {
            return i -> i + NumberUtils.toInt(value);
        } else {
            return i -> i * NumberUtils.toInt(value);
        }
    }
}