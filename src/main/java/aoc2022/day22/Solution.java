package aoc2022.day22;

import aoc2022.input.InputLoader;
import com.google.common.primitives.Chars;
import lombok.Data;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.javatuples.Pair;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class Solution {
    private static final String INPUT_SEPARATOR = System.lineSeparator() + System.lineSeparator();
    private static final Pattern COMMANDS_PATTERN = Pattern.compile("(\\d+|[RL])");
    private static final List<Pair<Integer, Integer>> SIDES = List.of(Pair.with(1, 0), Pair.with(2, 0), Pair.with(1, 1),
            Pair.with(1, 2), Pair.with(0, 2), Pair.with(0, 3));
    private static final int SIDE_SIZE = 50;

    public static void main(final String[] args) {
        final var input = StreamEx.split(InputLoader.read("day22"), INPUT_SEPARATOR)
                .toListAndThen(Pair::fromCollection);
        final var map = parseMap(input.getValue0());
        final var commands = COMMANDS_PATTERN.matcher(input.getValue1()).results().map(MatchResult::group).toList();

        final int result1 = move(map, commands, Solution::wrap);
        System.out.println(result1);

        final int result2 = move(map, commands, Solution::wrapCube);
        System.out.println(result2);
    }

    private static Map<Point, Tile> parseMap(final String map) {
        return EntryStream.of(map.split(System.lineSeparator()))
                .flatMapKeyValue((y, line) -> EntryStream.of(Chars.asList(line.toCharArray()))
                        .filterValues(c -> c != ' ')
                        .mapKeys(x -> new Point(x, y)))
                .mapToEntry(Map.Entry::getKey, Map.Entry::getValue)
                .mapValues(tile -> tile == '.' ? Tile.OPEN : Tile.WALL)
                .toMap();
    }

    private static int move(final Map<Point, Tile> map,
                            final List<String> commands,
                            final TriFunction<Point, Direction, Map<Point, Tile>, Wrapped> wrapper) {
        var position = getStartingPoint(map);
        var direction = Direction.RIGHT;
        for (final var command : commands) {
            if (!StringUtils.isNumeric(command)) {
                direction = turn(direction, command);
                continue;
            }
            final var steps = Integer.parseInt(command);
            for (int i = 0; i < steps; ++i) {
                final var nextPosition = nextPoint(position, direction);
                if (map.getOrDefault(nextPosition, Tile.WALL) == Tile.OPEN) {
                    position = nextPosition;
                    continue;
                }
                if (map.get(nextPosition) != null && map.get(nextPosition) == Tile.WALL) {
                    break;
                }
                final var wrappedPoint = wrapper.apply(position, direction, map);
                if (map.getOrDefault(wrappedPoint.getPoint(), Tile.WALL) == Tile.WALL) {
                    break;
                }
                position = wrappedPoint.getPoint();
                direction = wrappedPoint.getDirection();
            }
        }
        return ((position.y + 1) * 1000) + ((position.x + 1) * 4) + (direction.ordinal());
    }

    private static Point getStartingPoint(final Map<Point, Tile> points) {
        return EntryStream.of(points)
                .filterValues(tile -> tile.equals(Tile.OPEN))
                .keys()
                .sortedByDouble(Point::getX)
                .sortedByDouble(Point::getY)
                .findFirst()
                .orElseThrow();
    }

    private static Direction turn(final Direction current, final String command) {
        final var turn = command.equals("R") ? 1 : -1;
        return Direction.values()[Math.floorMod(current.ordinal() + turn, Direction.values().length)];
    }

    private static Point nextPoint(final Point point, final Direction direction) {
        return switch (direction) {
            case RIGHT -> new Point(point.x + 1, point.y);
            case UP -> new Point(point.x, point.y - 1);
            case DOWN -> new Point(point.x, point.y + 1);
            case LEFT -> new Point(point.x - 1, point.y);
        };
    }

    private static Wrapped wrap(final Point next, final Direction direction, final Map<Point, Tile> map) {
        final var wrappedPoint = switch (direction) {
            case UP -> EntryStream.of(map).keys().filter(p -> p.x == next.x).maxByInt(e -> e.y).orElseThrow();
            case DOWN -> EntryStream.of(map).keys().filter(p -> p.x == next.x).minByInt(e -> e.y).orElseThrow();
            case RIGHT -> EntryStream.of(map).keys().filter(p -> p.y == next.y).minByInt(e -> e.x).orElseThrow();
            case LEFT -> EntryStream.of(map).keys().filter(p -> p.y == next.y).maxByInt(e -> e.x).orElseThrow();
        };
        return new Wrapped(wrappedPoint.x, wrappedPoint.y, direction);
    }

    private static Wrapped wrapCube(final Point position, final Direction direction, final Map<Point, Tile> map) {
        final var side = SIDES.indexOf(Pair.with(position.x / SIDE_SIZE, position.y / SIDE_SIZE));
        return switch (direction) {
            case UP -> switch (side) {
                case 0 -> new Wrapped(0, (SIDE_SIZE * 2) + position.x, Direction.RIGHT);
                case 1 -> new Wrapped(position.x - (SIDE_SIZE * 2), (SIDE_SIZE * 4) - 1, Direction.UP);
                case 4 -> new Wrapped(SIDE_SIZE, SIDE_SIZE + position.x, Direction.RIGHT);
                default -> null;
            };
            case DOWN -> switch (side) {
                case 1 -> new Wrapped((SIDE_SIZE * 2) - 1, position.x - SIDE_SIZE, Direction.LEFT);
                case 3 -> new Wrapped(SIDE_SIZE - 1, (SIDE_SIZE * 2) + position.x, Direction.LEFT);
                case 5 -> new Wrapped(position.x + (SIDE_SIZE * 2), 0, Direction.DOWN);
                default -> null;
            };
            case RIGHT -> switch (side) {
                case 1 -> new Wrapped((SIDE_SIZE * 2) - 1, (SIDE_SIZE * 3) - position.y - 1, Direction.LEFT);
                case 2 -> new Wrapped(position.y + SIDE_SIZE, SIDE_SIZE - 1, Direction.UP);
                case 3 -> new Wrapped((SIDE_SIZE * 3) - 1, (SIDE_SIZE * 3) - position.y - 1, Direction.LEFT);
                case 5 -> new Wrapped(position.y - (SIDE_SIZE * 2), (SIDE_SIZE * 3) - 1, Direction.UP);
                default -> null;
            };
            case LEFT -> switch (side) {
                case 0 -> new Wrapped(0, (SIDE_SIZE * 3) - position.y - 1, Direction.RIGHT);
                case 2 -> new Wrapped(position.y - SIDE_SIZE, SIDE_SIZE * 2, Direction.DOWN);
                case 4 -> new Wrapped(SIDE_SIZE, (SIDE_SIZE * 3) - position.y - 1, Direction.RIGHT);
                case 5 -> new Wrapped(position.y - (SIDE_SIZE * 2), 0, Direction.DOWN);
                default -> null;
            };
        };
    }

    private enum Tile {
        OPEN, WALL
    }

    private enum Direction {
        RIGHT, DOWN, LEFT, UP
    }

    @Data
    private static final class Wrapped {
        private final Point point;
        private final Direction direction;

        public Wrapped(final int x, final int y, final Direction direction) {
            this.point = new Point(x, y);
            this.direction = direction;
        }
    }
}
