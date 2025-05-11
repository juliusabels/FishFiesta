package dev.juliusabels.fish_fiesta.game.features;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public enum WaterSubtype {
    DEEPSEA,
    COAST,
    OPEN_OCEAN,
    CORAL_REEF,
    LAKE,
    RIVER,
    KELP_FOREST;

    /**
     * Tries to map a list of strings to a list of WaterSubtype values. Invalid strings will be ignored, but logged!
     */
    public static List<WaterSubtype> mapFromStrings(List<String> values) {
        return values.stream()
            .map(value -> {
                try {
                    return WaterSubtype.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid water subtype {}", value);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
    }
}
