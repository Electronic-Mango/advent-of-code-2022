package aoc2022.day19;

import aoc2022.input.InputLoader;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import one.util.streamex.EntryStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Solution {
    public static void main(String[] args) {
        final var input = InputLoader.readLines("day19", "input").stream()
                .map(Blueprint::new)
                .toList();

        final var maxGeodesList = new ArrayList<Integer>();
        for (final var blueprint : input) {
            final var initialState = new State(0, 0, 0, 0, 1, 0, 0, 0);
            final var states = Lists.newArrayList(initialState);
            var bestState = initialState;
            var maxGeodes = 0;
            for (int minute = 0; minute < 24; ++minute) {
                final var newStates = new HashSet<State>();
                for (final var state : states) {
                    final var newOre = state.getOre() + state.getOreRobots();
                    final var newClay = state.getClay() + state.getClayRobots();
                    final var newObsidian = state.getObsidian() + state.getObsidianRobots();
                    final var newGeode = state.getGeode() + state.getGeodeRobots();
                    maxGeodes = Math.max(maxGeodes, newGeode);
                    bestState = maxGeodes == newGeode ? state : bestState;

                    newStates.add(new State(newOre, newClay, newObsidian, newGeode,
                            state.getOreRobots(), state.getClayRobots(), state.getObsidianRobots(),
                            state.getGeodeRobots()));

                    final var maxOreCost = Stream.of(blueprint.getOre(), blueprint.getClay(),
                            blueprint.getObsidianOre(),
                            blueprint.getGeodeOre()).mapToInt(Integer::valueOf).max().orElseThrow();
                    if (state.getOreRobots() < maxOreCost) {
                        if (state.getOre() >= blueprint.getOre()) {
                            newStates.add(new State(newOre - blueprint.getOre(), newClay, newObsidian, newGeode,
                                    state.getOreRobots() + 1, state.getClayRobots(), state.getObsidianRobots(),
                                    state.getGeodeRobots()));
                        }
                    }

                    if (state.getClayRobots() < blueprint.getObsidianClay()) {
                        if (state.getOre() >= blueprint.getClay()) {
                            newStates.add(new State(newOre - blueprint.getClay(), newClay, newObsidian, newGeode,
                                    state.getOreRobots(), state.getClayRobots() + 1, state.getObsidianRobots(),
                                    state.getGeodeRobots()));
                        }
                    }

                    if (state.getObsidianRobots() < blueprint.getGeodeObsidian()) {
                        if (state.getOre() >= blueprint.getObsidianOre() && state.getClay() >= blueprint.getObsidianClay()) {
                            newStates.add(new State(newOre - blueprint.getObsidianOre(), newClay - blueprint.getObsidianClay(), newObsidian, newGeode,
                                    state.getOreRobots(), state.getClayRobots(), state.getObsidianRobots() + 1,
                                    state.getGeodeRobots()));
                        }
                    }

                    if (state.getOre() >= blueprint.getGeodeOre() && state.getObsidian() >= blueprint.getGeodeObsidian()) {
                        newStates.add(new State(newOre - blueprint.getGeodeOre(), newClay, newObsidian - blueprint.getGeodeObsidian(), newGeode,
                                state.getOreRobots(), state.getClayRobots(), state.getObsidianRobots(),
                                state.getGeodeRobots() + 1));
                    }
                }
                states.clear();
                for (final var state : newStates) {
                    if ((state.getGeodeRobots() * (23 - minute)) + state.getGeode() >= maxGeodes) {
                        states.add(state);
                    }
                }
                System.out.println(minute + " " + states.size() + " " + maxGeodes + " " + bestState);
            }
//            System.out.println(blueprint + " " + states.size() + " " + maxGeodes + " " + bestState);
            maxGeodesList.add(maxGeodes);
        }
        System.out.println(maxGeodesList);
        final var result1 = EntryStream.of(maxGeodesList)
                .mapKeys(Math::incrementExact)
                .mapKeyValue(Math::multiplyExact)
                .mapToInt(Integer::valueOf)
                .sum();
        System.out.println(result1);
    }
}

@Data
final class Blueprint {
    private static final Pattern INPUT_PATTERN = Pattern.compile("(\\d+)");
    private final int id;
    private final int ore;
    private final int clay;
    private final int obsidianOre;
    private final int obsidianClay;
    private final int geodeOre;
    private final int geodeObsidian;

    Blueprint(final String raw) {
        final var matcher = INPUT_PATTERN.matcher(raw);
        id = getInt(matcher);
        ore = getInt(matcher);
        clay = getInt(matcher);
        obsidianOre = getInt(matcher);
        obsidianClay = getInt(matcher);
        geodeOre = getInt(matcher);
        geodeObsidian = getInt(matcher);
    }

    private int getInt(final Matcher matcher) {
        matcher.find();
        return Integer.parseInt(matcher.group());
    }
}

@Data
final class State {
    private final int ore;
    private final int clay;
    private final int obsidian;
    private final int geode;
    private final int oreRobots;
    private final int clayRobots;
    private final int obsidianRobots;
    private final int geodeRobots;
}