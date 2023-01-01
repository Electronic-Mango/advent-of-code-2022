package aoc2022.day19;

import aoc2022.input.InputLoader;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Solution {
    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day19").stream().map(Blueprint::parse).toList();

        final var result1 = EntryStream.of(getMaxGeodesPerBlueprint(input, 24))
                .mapKeys(Math::incrementExact)
                .mapKeyValue(Math::multiplyExact)
                .mapToInt(Integer::valueOf)
                .sum();
        System.out.println(result1);

        final var result2 = getMaxGeodesPerBlueprint(input.stream().limit(3).toList(), 32).stream()
                .reduce(Math::multiplyExact)
                .orElseThrow();
        System.out.println(result2);
    }

    private static List<Integer> getMaxGeodesPerBlueprint(final List<Blueprint> blueprints, final int minutes) {
        return blueprints.stream().map(blueprint -> getMaxGeodesForBlueprint(blueprint, minutes)).toList();
    }

    private static int getMaxGeodesForBlueprint(final Blueprint blueprint, final int minutes) {
        final var initialState = new State(0, 0, 0, 0, 1, 0, 0, 0);
        var states = Set.of(initialState);
        var maxGeodes = 0;
        for (int minute = 0; minute < minutes; ++minute) {
            states = StreamEx.of(states)
                    .flatMap(state -> state.nextStates(blueprint))
                    .filter(canMineMoreGeodesThanMax(minutes, minute, maxGeodes))
                    .toSet();
            maxGeodes = states.stream().mapToInt(State::geode).max().orElseThrow();
        }
        return maxGeodes;
    }

    private static Predicate<State> canMineMoreGeodesThanMax(final int minutes, final int minute, final int maxGeodes) {
        return state -> (state.geodeRobots() * (minutes - minute)) + state.geode() >= maxGeodes;
    }
}

record Blueprint(int id, int ore, int clay, int obsOre, int obsClay, int geodeOre, int geodeObs) {
    static Blueprint parse(final String raw) {
        final var m = Pattern.compile("(\\d+)").matcher(raw);
        return new Blueprint(getInt(m), getInt(m), getInt(m), getInt(m), getInt(m), getInt(m), getInt(m));
    }

    private static int getInt(final Matcher matcher) {
        return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
    }

    int maxOreCost() {
        return IntStreamEx.of(ore, clay, obsOre, geodeOre).max().orElseThrow();
    }
}

record State(int ore, int clay, int obs, int geode, int oreRobots, int clayRobots, int obsRobots, int geodeRobots) {
    Stream<State> nextStates(final Blueprint blueprint) {
        if (ore >= blueprint.geodeOre() && obs >= blueprint.geodeObs()) {
            return Stream.of(next(blueprint.geodeOre(), 0, blueprint.geodeObs(), false, false, false, true));
        }
        final var nextStates = Stream.<State>builder();
        nextStates.add(next(0, 0, 0, false, false, false, false));
        if (oreRobots < blueprint.maxOreCost() && ore >= blueprint.ore()) {
            nextStates.add(next(blueprint.ore(), 0, 0, true, false, false, false));
        }
        if (clayRobots < blueprint.obsClay() && ore >= blueprint.clay()) {
            nextStates.add(next(blueprint.clay(), 0, 0, false, true, false, false));
        }
        if (obsRobots < blueprint.geodeObs() && ore >= blueprint.obsOre() && clay >= blueprint.obsClay()) {
            nextStates.add(next(blueprint.obsOre(), blueprint.obsClay(), 0, false, false, true, false));
        }
        return nextStates.build();
    }

    private State next(final int oreCost,
                       final int clayCost,
                       final int obsCost,
                       final boolean buyOreRobot,
                       final boolean buyClayRobot,
                       final boolean buyObsRobot,
                       final boolean buyGeodeRobot) {
        return new State(
                ore + oreRobots - oreCost,
                clay + clayRobots - clayCost,
                obs + obsRobots - obsCost,
                geode + geodeRobots,
                oreRobots + (buyOreRobot ? 1 : 0),
                clayRobots + (buyClayRobot ? 1 : 0),
                obsRobots + (buyObsRobot ? 1 : 0),
                geodeRobots + (buyGeodeRobot ? 1 : 0)
        );
    }
}
