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
                    .flatMap(state -> nextStates(blueprint, state))
                    .filter(canMineMoreGeodesThanMax(minutes, minute, maxGeodes))
                    .toSet();
            maxGeodes = states.stream().mapToInt(State::geode).max().orElseThrow();
        }
        return maxGeodes;
    }

    private static Stream<State> nextStates(final Blueprint blueprint, final State state) {
        final var nextOre = state.ore() + state.oreRobots();
        final var nextClay = state.clay() + state.clayRobots();
        final var nextObs = state.obs() + state.obsRobots();
        final var nextGeode = state.geode() + state.geodeRobots();
        if (state.ore() >= blueprint.geodeOre() && state.obs() >= blueprint.geodeObs()) {
            return Stream.of(new State(nextOre - blueprint.geodeOre(), nextClay, nextObs - blueprint.geodeObs(),
                    nextGeode, state.oreRobots(), state.clayRobots(), state.obsRobots(), state.geodeRobots() + 1));
        }
        final var nextStates = Stream.<State>builder();
        nextStates.add(new State(nextOre, nextClay, nextObs, nextGeode, state.oreRobots(), state.clayRobots(),
                state.obsRobots(), state.geodeRobots()));
        if (state.oreRobots() < blueprint.maxOreCost() && state.ore() >= blueprint.ore()) {
            nextStates.add(new State(nextOre - blueprint.ore(), nextClay, nextObs, nextGeode, state.oreRobots() + 1,
                    state.clayRobots(), state.obsRobots(), state.geodeRobots()));
        }
        if (state.clayRobots() < blueprint.obsClay() && state.ore() >= blueprint.clay()) {
            nextStates.add(new State(nextOre - blueprint.clay(), nextClay, nextObs, nextGeode, state.oreRobots(),
                    state.clayRobots() + 1, state.obsRobots(), state.geodeRobots()));
        }
        if (state.obsRobots() < blueprint.geodeObs() && state.ore() >= blueprint.obsOre() && state.clay() >= blueprint.obsClay()) {
            nextStates.add(new State(nextOre - blueprint.obsOre(), nextClay - blueprint.obsClay(), nextObs, nextGeode,
                    state.oreRobots(), state.clayRobots(), state.obsRobots() + 1, state.geodeRobots()));
        }
        return nextStates.build();
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

record State(int ore, int clay, int obs, int geode, int oreRobots, int clayRobots, int obsRobots, int geodeRobots) { }
