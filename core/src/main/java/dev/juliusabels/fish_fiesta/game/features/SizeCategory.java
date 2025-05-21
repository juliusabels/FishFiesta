package dev.juliusabels.fish_fiesta.game.features;

import lombok.Getter;

/**
 * Represents the size classification of water creatures.
 * <p>
 * Each category defines a size range for water creatures and provides a formatted
 * description which is used in the journal
 */
public enum SizeCategory {
    /**
     * Represents small water creatures (1-29cm average size).
     */
    SMALL("small"),

    /**
     * Represents medium-sized water creatures (30-99cm average size).
     */
    MEDIUM("medium sized"),

    /**
     * Represents large water creatures (100cm and above average size).
     */
    BIG("big"),

    /**
     * Represents creatures with undefined or invalid size.
     */
    UNDEFINED("");

    /**
     * A formatted version of the size name used for the journal description
     */
    @Getter
    private final String formattedForDesc;

    /**
     * Creates a new size category with the specified formatted description.
     *
     * @param formatted The text description of this size category
     */
    SizeCategory(String formatted) {
        this.formattedForDesc = formatted;
    }
}
