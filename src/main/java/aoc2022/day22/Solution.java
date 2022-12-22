package aoc2022.day22;

import aoc2022.input.InputLoader;
import com.google.common.primitives.Chars;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        final int result1 = move(map, commands, Solution::wrapFlat);
        System.out.println(result1);

        final int result2 = move(map, commands, Solution::wrapCube);
        System.out.println(result2);
    }

    private static Map<Point, Boolean> parseMap(final String map) {
        return EntryStream.of(map.split(System.lineSeparator()))
                .flatMapKeyValue((y, line) -> EntryStream.of(Chars.asList(line.toCharArray()))
                        .filterValues(c -> c != ' ')
                        .mapKeys(x -> new Point(x, y)))
                .mapToEntry(Map.Entry::getKey, Map.Entry::getValue)
                .mapValues(tile -> tile == '.')
                .toMap();
    }

    private static int move(final Map<Point, Boolean> map, final List<String> commands, final Wrapper wrapper) {
        final var state = new State(getStartingPoint(map), Direction.RIGHT);
        commands.forEach(command -> nextMove(state, command, map, wrapper));
        return ((state.getPoint().y + 1) * 1000) + ((state.getPoint().x + 1) * 4) + (state.getDirection().ordinal());
    }

    private static void nextMove(final State state,
                                 final String command,
                                 final Map<Point, Boolean> map,
                                 final Wrapper wrapper) {
        if (StringUtils.isNumeric(command)) {
            IntStreamEx.range(Integer.parseInt(command)).forEach(i -> stepForward(state, map, wrapper));
        } else {
            state.setDirection(turn(state.getDirection(), command));
        }
    }

    private static void stepForward(final State state, final Map<Point, Boolean> map, final Wrapper wrapper) {
        final var point = state.getPoint();
        final var nextPoint = new Point(point.x + state.getDirection().dx, point.y + state.getDirection().dy);
        if (map.getOrDefault(nextPoint, false)) {
            state.setPoint(nextPoint);
        } else if (map.getOrDefault(nextPoint, true)) {
            wrap(state, map, wrapper);
        }
    }

    private static void wrap(final State state, final Map<Point, Boolean> map, final Wrapper wrapper) {
        final var wrappedState = wrapper.wrap(state, map.keySet());
        if (map.getOrDefault(wrappedState.getPoint(), false)) {
            state.set(wrappedState);
        }
    }

    private static Point getStartingPoint(final Map<Point, Boolean> points) {
        return EntryStream.of(points)
                .filterValues(Boolean::booleanValue)
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

    private static State wrapFlat(final State state, final Set<Point> points) {
        final var wrappedPoint = switch (state.getDirection()) {
            case UP -> StreamEx.of(points).filter(p -> p.x == state.getPoint().x).maxByInt(e -> e.y).orElseThrow();
            case DOWN -> StreamEx.of(points).filter(p -> p.x == state.getPoint().x).minByInt(e -> e.y).orElseThrow();
            case RIGHT -> StreamEx.of(points).filter(p -> p.y == state.getPoint().y).minByInt(e -> e.x).orElseThrow();
            case LEFT -> StreamEx.of(points).filter(p -> p.y == state.getPoint().y).maxByInt(e -> e.x).orElseThrow();
        };
        return new State(wrappedPoint, state.getDirection());
    }

    private static State wrapCube(final State state, final Set<Point> points) {
        final var position = state.getPoint();
        final var direction = state.getDirection();
        final var side = SIDES.indexOf(Pair.with(position.x / SIDE_SIZE, position.y / SIDE_SIZE));
        return switch (direction) {
            case UP -> switch (side) {
                case 0 -> new State(0, (SIDE_SIZE * 2) + position.x, Direction.RIGHT);
                case 1 -> new State(position.x - (SIDE_SIZE * 2), (SIDE_SIZE * 4) - 1, Direction.UP);
                case 4 -> new State(SIDE_SIZE, SIDE_SIZE + position.x, Direction.RIGHT);
                default -> throw new IllegalArgumentException("Invalid state for wrapping " + state);
            };
            case DOWN -> switch (side) {
                case 1 -> new State((SIDE_SIZE * 2) - 1, position.x - SIDE_SIZE, Direction.LEFT);
                case 3 -> new State(SIDE_SIZE - 1, (SIDE_SIZE * 2) + position.x, Direction.LEFT);
                case 5 -> new State(position.x + (SIDE_SIZE * 2), 0, Direction.DOWN);
                default -> throw new IllegalArgumentException("Invalid state for wrapping " + state);
            };
            case RIGHT -> switch (side) {
                case 1 -> new State((SIDE_SIZE * 2) - 1, (SIDE_SIZE * 3) - position.y - 1, Direction.LEFT);
                case 2 -> new State(position.y + SIDE_SIZE, SIDE_SIZE - 1, Direction.UP);
                case 3 -> new State((SIDE_SIZE * 3) - 1, (SIDE_SIZE * 3) - position.y - 1, Direction.LEFT);
                case 5 -> new State(position.y - (SIDE_SIZE * 2), (SIDE_SIZE * 3) - 1, Direction.UP);
                default -> throw new IllegalArgumentException("Invalid state for wrapping " + state);
            };
            case LEFT -> switch (side) {
                case 0 -> new State(0, (SIDE_SIZE * 3) - position.y - 1, Direction.RIGHT);
                case 2 -> new State(position.y - SIDE_SIZE, SIDE_SIZE * 2, Direction.DOWN);
                case 4 -> new State(SIDE_SIZE, (SIDE_SIZE * 3) - position.y - 1, Direction.RIGHT);
                case 5 -> new State(position.y - (SIDE_SIZE * 2), 0, Direction.DOWN);
                default -> throw new IllegalArgumentException("Invalid state for wrapping " + state);
            };
        };
    }

    @RequiredArgsConstructor
    private enum Direction {
        RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0), UP(0, -1);
        private final int dx;
        private final int dy;
    }

    private interface Wrapper {
        State wrap(final State state, final Set<Point> points);
    }

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    private static final class State {
        private Point point;
        private Direction direction;

        State(final int x, final int y, final Direction direction) {
            this(new Point(x, y), direction);
        }

        void set(final State state) {
            this.point = state.point;
            this.direction = state.direction;
        }
    }
}
