package aoc2022.day19;

import aoc2022.input.InputLoader;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import one.util.streamex.EntryStream;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Solution {
    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day19", "input").stream()
                .map(Blueprint::new)
                .toList();

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
                newStates.add(new State(ore, clay, obs, geode, state.oreRobots(), state.clayRobots(), state.obsRobots(),
                        state.geodeRobots()));
                final var maxOreCost = Collections.max(
                        List.of(blueprint.getOre(), blueprint.getClay(), blueprint.getObsOre(),
                                blueprint.getGeodeOre()));
                if (state.oreRobots() < maxOreCost && state.ore() >= blueprint.getOre()) {
                    newStates.add(new State(ore - blueprint.getOre(), clay, obs, geode, state.oreRobots() + 1,
                            state.clayRobots(), state.obsRobots(), state.geodeRobots()));
                }
                if (state.clayRobots() < blueprint.getObsClay() && state.ore() >= blueprint.getClay()) {
                    newStates.add(new State(ore - blueprint.getClay(), clay, obs, geode, state.oreRobots(),
                            state.clayRobots() + 1, state.obsRobots(), state.geodeRobots()));
                }
                if (state.obsRobots() < blueprint.getGeodeObs() && state.ore() >= blueprint.getObsOre() &&
                        state.clay() >= blueprint.getObsClay()) {
                    newStates.add(new State(ore - blueprint.getObsOre(), clay - blueprint.getObsClay(), obs, geode,
                            state.oreRobots(), state.clayRobots(), state.obsRobots() + 1, state.geodeRobots()));
                }
                if (state.ore() >= blueprint.getGeodeOre() && state.obs() >= blueprint.getGeodeObs()) {
                    newStates.add(new State(ore - blueprint.getGeodeOre(), clay, obs - blueprint.getGeodeObs(), geode,
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

@Getter(AccessLevel.PACKAGE)
final class Blueprint {
    private static final Pattern INPUT_PATTERN = Pattern.compile("(\\d+)");
    private final int id;
    private final int ore;
    private final int clay;
    private final int obsOre;
    private final int obsClay;
    private final int geodeOre;
    private final int geodeObs;

    Blueprint(final String raw) {
        final var matcher = INPUT_PATTERN.matcher(raw);
        id = getInt(matcher);
        ore = getInt(matcher);
        clay = getInt(matcher);
        obsOre = getInt(matcher);
        obsClay = getInt(matcher);
        geodeOre = getInt(matcher);
        geodeObs = getInt(matcher);
    }

    private int getInt(final Matcher matcher) {
        return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
    }
}

record State(int ore, int clay, int obs, int geode, int oreRobots, int clayRobots, int obsRobots, int geodeRobots) { }
