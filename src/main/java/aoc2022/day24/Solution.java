package aoc2022.day24;

import aoc2022.input.InputLoader;
import com.google.common.primitives.Chars;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Solution {
    private static final Set<Point> DELTAS = Set.of(new Point(0, -1), new Point(0, 1), new Point(-1, 0),
            new Point(1, 0), new Point(0, 0));

    public static void main(final String[] args) {
        final var input = EntryStream.of(InputLoader.readLines("day24", "input"))
                .flatMapValues(line -> EntryStream.of(Chars.asList(line.toCharArray())).filterValues(c -> c != '.'))
                .mapToKey((y, entry) -> new Point(entry.getKey(), y))
                .mapValues(Map.Entry::getValue)
                .mapValues(Type::parse)
                .mapKeyValue(Element::new)
                .toList();
        final var maxX = getMaxValue(input, Point::getX);
        final var maxY = getMaxValue(input, Point::getY);
        final var start = new Point(1, 0);
        final var finish = new Point(maxX - 1, maxY);
        final var states = new LinkedList<Point>();
        states.add(start);

        final var result1 = move(start, finish, states, input);
        System.out.println(result1);

        final var result2 = result1 + move(finish, start, states, input) + move(start, finish, states, input);
        System.out.println(result2);
    }

    private static int move(final Point start, final Point end, final Deque<Point> states, final List<Element> map) {
        var i = 0;
        final var maxX = getMaxValue(map, Point::getX);
        final var maxY = getMaxValue(map, Point::getY);
        states.clear();
        states.add(start);
        while (states.stream().noneMatch(e -> e.equals(end))) {
            final var newStates = new HashSet<Point>();
            map.forEach(element -> moveBlizzard(element, maxX, maxY));
            while (!states.isEmpty()) {
                final var state = states.pop();
                final var nextPlayerStates = movePlayer(state, StreamEx.of(map)
                        .map(Element::position)
                        .toSet(), maxX, maxY);
                newStates.addAll(nextPlayerStates);
            }
            i++;
            states.addAll(newStates);
        }
        return i;
    }

    private static int getMaxValue(final List<Element> element, final Function<Point, Double> getter) {
        return element.stream().map(Element::position).map(getter).mapToInt(Double::intValue).max().orElseThrow();
    }

    private static void moveBlizzard(final Element element, final int maxX, final int maxY) {
        if (element.type.equals(Type.EDGE)) {
            return;
        }
        switch (element.type) {
            case UP -> element.position.translate(0, -1);
            case DOWN -> element.position.translate(0, 1);
            case LEFT -> element.position.translate(-1, 0);
            case RIGHT -> element.position.translate(1, 0);
        }
        if (element.position.x >= maxX) {
            element.position.move(1, element.position.y);
        } else if (element.position.x < 1) {
            element.position.move(maxX - 1, element.position.y);
        } else if (element.position.y >= maxY) {
            element.position.move(element.position.x, 1);
        } else if (element.position.y < 1) {
            element.position.move(element.position.x, maxY - 1);
        }
    }

    private static Set<Point> movePlayer(final Point expedition, final Set<Point> map, final int maxX, final int maxY) {
        return DELTAS.stream()
                .map(delta -> new Point(delta.x + expedition.x, delta.y + expedition.y))
                .filter(p -> map.stream().noneMatch(o -> o.equals(p)))
                .filter(p -> p.x >= 0 && p.y >= 0)
                .filter(p -> p.x <= maxX && p.y <= maxY)
                .collect(Collectors.toSet());
    }

    private enum Type {
        UP, DOWN, LEFT, RIGHT, EDGE;

        static Type parse(final char type) {
            return switch (type) {
                case '#' -> EDGE;
                case '^' -> UP;
                case 'v' -> DOWN;
                case '<' -> LEFT;
                case '>' -> RIGHT;
                default -> throw new IllegalArgumentException("Unexpected type: " + type);
            };
        }
    }

    private record Element(Point position, Type type) {
    }
}
