package dev.juliusabels.fish_fiesta.game;

import dev.juliusabels.fish_fiesta.game.features.SizeCategory;
import lombok.Getter;

@Getter
public final class CreatureSize {
    private final int rangeStart;
    private final int rangeEnd;

    public CreatureSize(int rangeStart, int rangeEnd) {
        if (rangeStart == 0 || rangeEnd == 0) {
            this.rangeStart = 0;
            this.rangeEnd = 0;
        } else {
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
        }
    }

    public SizeCategory getCategoriesFromSize() {
        int avg = getAverageSize();
        if (avg < 1) {
            return SizeCategory.UNDEFINED;
        }

        if (getAverageSize() <= 29) {
            return SizeCategory.SMALL;
        } else if (getAverageSize() <= 99) {
            return SizeCategory.MEDIUM;
        } else {
            return SizeCategory.BIG;
        }
    }

    public int getAverageSize() {
        return (rangeStart + rangeEnd) / 2;
    }
}
