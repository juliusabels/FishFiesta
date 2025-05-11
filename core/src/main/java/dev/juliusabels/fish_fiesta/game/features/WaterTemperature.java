package dev.juliusabels.fish_fiesta.game.features;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public enum WaterTemperature {
    COLD,
    MEDIUM,
    WARM;

    /**
     * Tries to map a list of strings to a list of WaterTemperature values. Invalid strings will be ignored, but logged!
     */
    public static List<WaterTemperature> mapFromStrings(List<String> values) {
        return values.stream()
            .map(value -> {
                try {
                    return WaterTemperature.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid water temperature {}", value);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
    }
}
