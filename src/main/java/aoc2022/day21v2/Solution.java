package aoc2022.day21v2;

import aoc2022.input.InputLoader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.Map;
import java.util.function.DoubleBinaryOperator;

public final class Solution {
    private static final String ROOT = "root";
    private static final String HUMAN = "humn";

    public static void main(final String[] args) {
        final var definitions = StreamEx.of(InputLoader.readLines("day21"))
                .map(line -> StreamEx.split(line, ": ").toListAndThen(Pair::fromCollection))
                .mapToEntry(Pair::getValue0, Pair::getValue1)
                .toMap();

        final var root1 = parseNode(ROOT, definitions);
        final var result1 = root1.getValue();
        System.out.println((long) result1);

        definitions.compute(HUMAN, (id, value) -> String.valueOf(Double.NaN));
        final var root2 = (OperationNode) parseNode(ROOT, definitions);
        final var branch1 = root2.getBranch1();
        final var branch2 = root2.getBranch2();
        final var result2 = branch1.hasX() ? branch1.getX(branch2.getValue()) : branch2.getX(branch1.getValue());
        System.out.println((long) result2);
    }

    private static Node parseNode(final String id, final Map<String, String> definitions) {
        final var definition = definitions.get(id);
        if (!definition.contains(" ")) {
            return new ValueNode(id, Double.parseDouble(definition));
        }
        final var parameters = StreamEx.split(definition, " ").toListAndThen(Triplet::fromCollection);
        final var branch1 = parseNode(parameters.getValue0(), definitions);
        final var branch2 = parseNode(parameters.getValue2(), definitions);
        final var operation = parameters.getValue1();
        return switch (operation) {
            case "+" -> new OperationNode(id, (v1, v2) -> v1 + v2, (o, v) -> o - v, (o, v) -> o - v, branch1, branch2);
            case "-" -> new OperationNode(id, (v1, v2) -> v1 - v2, (o, v) -> o + v, (o, v) -> v - o, branch1, branch2);
            case "*" -> new OperationNode(id, (v1, v2) -> v1 * v2, (o, v) -> o / v, (o, v) -> o / v, branch1, branch2);
            case "/" -> new OperationNode(id, (v1, v2) -> v1 / v2, (o, v) -> o * v, (o, v) -> v - o, branch1, branch2);
            default -> throw new IllegalArgumentException(String.format("Unexpected operation '%s'!", operation));
        };
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
final class ValueNode extends Node {
    private final double value;

    ValueNode(final String id, final double value) {
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
final class OperationNode extends Node {
    private final DoubleBinaryOperator operation;
    private final DoubleBinaryOperator inverseLeft;
    private final DoubleBinaryOperator inverseRight;
    private final Node branch1;
    private final Node branch2;

    OperationNode(final String id,
                  final DoubleBinaryOperator operation,
                  final DoubleBinaryOperator inverseLeft,
                  final DoubleBinaryOperator inverseRight,
                  final Node branch1,
                  final Node branch2) {
        super(id);
        this.operation = operation;
        this.inverseLeft = inverseLeft;
        this.inverseRight = inverseRight;
        this.branch1 = branch1;
        this.branch2 = branch2;
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
