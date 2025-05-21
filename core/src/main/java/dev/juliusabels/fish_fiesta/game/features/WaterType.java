package dev.juliusabels.fish_fiesta.game.features;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * Represents the primary types of water environments.
 * <p>
 * Water types classify aquatic environments as either saltwater or freshwater.
 * Each water creature is adapted to live in one or both of these water types.
 */
@Slf4j
public enum WaterType {
    SALT,
    FRESH;

    /**
     * Converts a list of strings to water type enums, ignoring invalid values.
     * <p>
     * This method attempts to parse each string into a WaterType enum constant.
     * If a string doesn't match any valid type, it's logged as a warning and excluded
     * from the result list.
     *
     * @param values List of strings to convert to WaterType values
     * @return List of successfully parsed WaterType values
     */
    public static List<WaterType> mapFromStrings(List<String> values) {
        return values.stream()
            .map(value -> {
                try {
                    return WaterType.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid water type {}", value);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
    }
}
