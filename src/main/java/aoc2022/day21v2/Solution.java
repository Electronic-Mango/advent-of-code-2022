package aoc2022.day21v2;

import aoc2022.input.InputLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.Map;

public final class Solution {
    private static final String ROOT = "root";
    private static final String HUMAN = "humn";

    public static void main(String[] args) {
        final var input = StreamEx.of(InputLoader.readLines("day21"))
                .map(line -> StreamEx.split(line, ": ").toListAndThen(Pair::fromCollection))
                .mapToEntry(Pair::getValue0, Pair::getValue1)
                .toMap();
        final var nodeMap = EntryStream.of(input).mapToValue(Solution::parse).toMap();
        nodeMap.values()
                .stream()
                .filter(Inner.class::isInstance)
                .map(Inner.class::cast)
                .forEach(node -> branches(node, input, nodeMap));
        final var root = (Inner) nodeMap.get(ROOT);

        final var result1 = (long) root.getValue();
        System.out.println(result1);

        final var human = (Leaf) nodeMap.get(HUMAN);
        human.setValue(Double.NaN);
        final var node1 = root.getNode1();
        final var node2 = root.getNode2();
        final var known = node1.hasX() ? node2 : node1;
        final var unknown = node1.hasX() ? node1 : node2;
        final var value = known.getValue();
        final var result2 = (long) unknown.getX(value);
        System.out.println(result2);
    }

    private static Node parse(final String name, final String definition) {
        if (!definition.contains(" ")) {
            return new Leaf(name, Double.parseDouble(definition));
        }
        final var operation = StreamEx.split(definition, " ").skip(1).limit(1).findAny().orElseThrow();
        return switch (operation) {
            case "+" -> new Add(name);
            case "-" -> new Subtract(name);
            case "*" -> new Multiply(name);
            case "/" -> new Divide(name);
            default -> null;
        };
    }

    private static void branches(final Inner node, final Map<String, String> input, final Map<String, Node> nodes) {
        final var operation = input.get(node.getId());
        final var operationElements = StreamEx.split(operation, " ").toListAndThen(Triplet::fromCollection);
        final var node1 = nodes.get(operationElements.getValue0());
        final var node2 = nodes.get(operationElements.getValue2());
        node.setNode1(node1);
        node.setNode2(node2);
    }
}

@Getter
@RequiredArgsConstructor
abstract class Node {
    private final String id;

    abstract double getValue();

    abstract boolean hasX();

    abstract double getX(final double offset);
}

@Setter
final class Leaf extends Node {
    private double value;

    Leaf(final String id, final double value) {
        super(id);
        this.value = value;
    }

    @Override
    double getValue() {
        return value;
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

@Getter
@Setter
abstract class Inner extends Node {
    private Node node1;
    private Node node2;

    Inner(final String id) {
        super(id);
    }

    @Override
    double getValue() {
        return operation(node1.getValue(), node2.getValue());
    }

    @Override
    boolean hasX() {
        return node1.hasX() || node2.hasX();
    }

    @Override
    double getX(final double offset) {
        if (node1.hasX()) {
            return node1.getX(inverseOperation1(offset, node2.getValue()));
        } else {
            return node2.getX(inverseOperation2(offset, node1.getValue()));
        }
    }

    abstract double inverseOperation1(final double offset, final double value);

    abstract double inverseOperation2(final double offset, final double value);

    abstract double operation(final double value1, final double value2);
}

final class Add extends Inner {
    Add(final String id) {
        super(id);
    }

    @Override
    double operation(final double value1, final double value2) {
        return value1 + value2;
    }

    @Override
    double inverseOperation1(double offset, double value) {
        return offset - value;
    }

    @Override
    double inverseOperation2(double offset, double value) {
        return offset - value;
    }
}

final class Subtract extends Inner {

    Subtract(final String id) {
        super(id);
    }

    @Override
    double operation(final double value1, final double value2) {
        return value1 - value2;
    }

    @Override
    double inverseOperation1(double offset, double value) {
        return offset + value;
    }

    @Override
    double inverseOperation2(double offset, double value) {
        return value - offset;
    }
}

final class Multiply extends Inner {

    Multiply(final String id) {
        super(id);
    }

    @Override
    double operation(final double value1, final double value2) {
        return value1 * value2;
    }

    @Override
    double inverseOperation1(double offset, double value) {
        return offset / value;
    }

    @Override
    double inverseOperation2(double offset, double value) {
        return offset / value;
    }
}

final class Divide extends Inner {

    Divide(final String id) {
        super(id);
    }

    @Override
    double operation(final double value1, final double value2) {
        return value1 / value2;
    }

    @Override
    double inverseOperation1(double offset, double value) {
        return offset * value;
    }

    @Override
    double inverseOperation2(double offset, double value) {
        return value / offset;
    }
}
