package aoc2022.day21v2;

import aoc2022.input.InputLoader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.Map;
import java.util.function.DoubleBinaryOperator;

public final class Solution {
    private static final String ROOT = "root";
    private static final String HUMAN = "humn";

    public static void main(String[] args) {
        final var definitions = StreamEx.of(InputLoader.readLines("day21"))
                .map(line -> StreamEx.split(line, ": ").toListAndThen(Pair::fromCollection))
                .mapToEntry(Pair::getValue0, Pair::getValue1)
                .toMap();
        final var nodeMap = EntryStream.of(definitions).mapToValue(Solution::parseWithoutBranches).toMap();
        nodeMap.values()
                .stream()
                .filter(Operation.class::isInstance)
                .map(Operation.class::cast)
                .forEach(node -> addBranches(node, definitions, nodeMap));
        final var root = (Operation) nodeMap.get(ROOT);

        final var result1 = root.getValue();
        System.out.println((long) result1);

        final var human = (Value) nodeMap.get(HUMAN);
        human.setValue(Double.NaN);
        final var branch1 = root.getBranch1();
        final var branch2 = root.getBranch2();
        final var result2 = branch1.hasX() ? branch1.getX(branch2.getValue()) : branch2.getX(branch1.getValue());
        System.out.println((long) result2);
    }

    private static Node parseWithoutBranches(final String id, final String definition) {
        if (!definition.contains(" ")) {
            return new Value(id, Double.parseDouble(definition));
        }
        final var operation = StreamEx.split(definition, " ").toList().get(1);
        return switch (operation) {
            case "+" -> new Operation(id, (v1, v2) -> v1 + v2, (o, v) -> o - v, (o, v) -> o - v);
            case "-" -> new Operation(id, (v1, v2) -> v1 - v2, (o, v) -> o + v, (o, v) -> v - o);
            case "*" -> new Operation(id, (v1, v2) -> v1 * v2, (o, v) -> o / v, (o, v) -> o / v);
            case "/" -> new Operation(id, (v1, v2) -> v1 / v2, (o, v) -> o * v, (o, v) -> v - o);
            default -> null;
        };
    }

    private static void addBranches(final Operation node,
                                    final Map<String, String> definitions,
                                    final Map<String, Node> nodes) {
        final var operation = definitions.get(node.getId());
        final var operationElements = StreamEx.split(operation, " ").toListAndThen(Triplet::fromCollection);
        node.setBranch1(nodes.get(operationElements.getValue0()));
        node.setBranch2(nodes.get(operationElements.getValue2()));
    }
}

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
abstract class Node {
    private final String id;

    abstract double getValue();

    abstract boolean hasX();

    abstract double getX(final double offset);
}

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
final class Value extends Node {
    private double value;

    Value(final String id, final double value) {
        super(id);
        this.value = value;
    }

    @Override
    boolean hasX() {
        return Double.isNaN(value);
    }

    @Override
    double getX(final double offset) {
        return offset;
    }
}

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
final class Operation extends Node {
    private final DoubleBinaryOperator operation;
    private final DoubleBinaryOperator inverseLeft;
    private final DoubleBinaryOperator inverseRight;
    private Node branch1;
    private Node branch2;

    Operation(final String id,
              final DoubleBinaryOperator operation,
              final DoubleBinaryOperator inverseLeft,
              final DoubleBinaryOperator inverseRight) {
        super(id);
        this.operation = operation;
        this.inverseLeft = inverseLeft;
        this.inverseRight = inverseRight;
    }

    @Override
    double getValue() {
        return operation.applyAsDouble(branch1.getValue(), branch2.getValue());
    }

    @Override
    boolean hasX() {
        return branch1.hasX() || branch2.hasX();
    }

    @Override
    double getX(final double offset) {
        if (branch1.hasX()) {
            return branch1.getX(inverseLeft.applyAsDouble(offset, branch2.getValue()));
        } else {
            return branch2.getX(inverseRight.applyAsDouble(offset, branch1.getValue()));
        }
    }
}
