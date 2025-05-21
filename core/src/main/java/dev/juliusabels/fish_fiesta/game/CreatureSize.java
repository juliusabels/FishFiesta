package dev.juliusabels.fish_fiesta.game;

import dev.juliusabels.fish_fiesta.game.features.SizeCategory;
import lombok.Getter;

/**
 * Represents the size range of a water creature in centimeters.
 * <p>
 * This record stores the minimum and maximum size of a creature and provides
 * functionality to determine its size category and average size. A size range
 * with zero values is considered invalid/undefined.
 */
public record CreatureSize(int rangeStart, int rangeEnd) {

    /**
     * Creates a new CreatureSize with the specified range.
     * <p>
     * If either value is zero, both are set to zero, marking the size as undefined.
     *
     * @param rangeStart The minimum size in centimeters
     * @param rangeEnd The maximum size in centimeters
     */
    public CreatureSize(int rangeStart, int rangeEnd) {
        if (rangeStart == 0 || rangeEnd == 0) {
            this.rangeStart = 0;
            this.rangeEnd = 0;
        } else {
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
        }
    }

    /**
     * Determines the size category based on the average size.
     * <p>
     * Categories are defined as: <br>
     * - UNDEFINED: average size is less than 1cm <br>
     * - SMALL: average size is 1-29cm <br>
     * - MEDIUM: average size is 30-99cm <br>
     * - BIG: average size is 100cm or larger <br>
     *
     * @return The size category of this creature
     */
    public SizeCategory getCategory() {
        int avg = getAverageSize();

        if (avg < 1) {
            return SizeCategory.UNDEFINED;
        } else if (avg <= 29) {
            return SizeCategory.SMALL;
        } else if (avg <= 99) {
            return SizeCategory.MEDIUM;
        } else {
            return SizeCategory.BIG;
        }
    }

    /**
     * Calculates the average size of the creature.
     *
     * @return The average of the minimum and maximum sizes
     */
    public int getAverageSize() {
        return (rangeStart + rangeEnd) / 2;
    }

    /**
     * Checks if this size range has valid values.
     * <p>
     * A size range is valid if its category is not UNDEFINED.
     *
     * @return true if the size range is valid, false otherwise
     */
    public boolean isValid() {
        return getCategory() != SizeCategory.UNDEFINED;
    }
}
