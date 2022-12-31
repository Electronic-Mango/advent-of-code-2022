package aoc2022.day19;

import aoc2022.input.InputLoader;
import com.google.common.collect.Sets;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;

import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        final var states = Sets.newHashSet(initialState);
        var maxGeodes = 0;
        for (int minute = 0; minute < minutes; ++minute) {
            final var newStates = new HashSet<State>();
            for (final var state : states) {
                final var ore = state.ore() + state.oreRobots();
                final var clay = state.clay() + state.clayRobots();
                final var obs = state.obs() + state.obsRobots();
                final var geode = state.geode() + state.geodeRobots();
                maxGeodes = Math.max(maxGeodes, geode);
                newStates.add(new State(ore, clay, obs, geode, state.oreRobots(), state.clayRobots(),
                        state.obsRobots(), state.geodeRobots()));
                if (state.oreRobots() < blueprint.maxOreCost() && state.ore() >= blueprint.ore()) {
                    newStates.add(new State(ore - blueprint.ore(), clay, obs, geode, state.oreRobots() + 1,
                            state.clayRobots(), state.obsRobots(), state.geodeRobots()));
                }
                if (state.clayRobots() < blueprint.obsClay() && state.ore() >= blueprint.clay()) {
                    newStates.add(new State(ore - blueprint.clay(), clay, obs, geode, state.oreRobots(),
                            state.clayRobots() + 1, state.obsRobots(), state.geodeRobots()));
                }
                if (state.obsRobots() < blueprint.geodeObs() && state.ore() >= blueprint.obsOre() && state.clay() >= blueprint.obsClay()) {
                    newStates.add(new State(ore - blueprint.obsOre(), clay - blueprint.obsClay(), obs, geode,
                            state.oreRobots(), state.clayRobots(), state.obsRobots() + 1, state.geodeRobots()));
                }
                if (state.ore() >= blueprint.geodeOre() && state.obs() >= blueprint.geodeObs()) {
                    newStates.add(new State(ore - blueprint.geodeOre(), clay, obs - blueprint.geodeObs(), geode,
                            state.oreRobots(), state.clayRobots(), state.obsRobots(), state.geodeRobots() + 1));
                }
            }
            states.clear();
            newStates.stream().filter(stateCanBePruned(minutes, minute, maxGeodes)).forEach(states::add);
        }
        return maxGeodes;
    }

    private static Predicate<State> stateCanBePruned(final int minutes, final int minute, final int maxGeodes) {
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
