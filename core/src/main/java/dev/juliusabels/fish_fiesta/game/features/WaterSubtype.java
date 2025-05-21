package dev.juliusabels.fish_fiesta.game.features;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * Represents detailed subtypes of water environments.
 * <p>
 * Water subtypes define specific aquatic environments that water creatures can inhabit,
 * such as deep sea areas, rivers, or coral reefs. Each subtype includes a formatted
 * description for use in the journal.
 */
@Slf4j
public enum WaterSubtype {
    DEEPSEA("deep sea areas"),
    COAST("coastal waters"),
    OPEN_OCEAN("the open ocean"),
    CORAL_REEF("coral reefs"),
    LAKE("lakes"),
    RIVER("rivers"),
    KELP_FOREST("kelp forests");

    /**
     * A formatted description of this water subtype for use in the journal
     */
    @Getter
    private final String formattedForDesc;

    /**
     * Creates a new water subtype with the specified formatted description.
     *
     * @param formatted The text description of this water subtype
     */
    WaterSubtype(String formatted) {
        this.formattedForDesc = formatted;
    }

    /**
     * Converts a list of strings to water subtype enums, ignoring invalid values.
     * <p>
     * This method attempts to parse each string into a WaterSubtype enum constant.
     * If a string doesn't match any valid subtype, it's logged as a warning and excluded
     * from the result list.
     *
     * @param values List of strings to convert to WaterSubtype values
     * @return List of successfully parsed WaterSubtype values
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
