package dev.juliusabels.fish_fiesta.game.features;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * Represents the temperature classifications of water environments.
 */
@Slf4j
public enum WaterTemperature {
    COLD,
    MEDIUM,
    WARM;

    /**
     * Converts a list of strings to water temperature enums, ignoring invalid values.
     * <p>
     * This method attempts to parse each string into a WaterTemperature enum constant.
     * If a string doesn't match any valid temperature, it's logged as a warning and excluded
     * from the result list.
     *
     * @param values List of strings to convert to WaterTemperature values
     * @return List of successfully parsed WaterTemperature values
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
