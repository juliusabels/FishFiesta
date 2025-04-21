package dev.juliusabels.fish_fiesta.game;

import lombok.Getter;

@Getter
public final class CreatureLife {
    private final int rangeStart;
    private final int rangeEnd;

    public CreatureLife(int rangeEnd) {
        this(0, rangeEnd);
    }

    public CreatureLife(int rangeStart, int rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }


    public enum CreatureLifespan {
        UNDEFINED(-1),
        U10(10),
        U30(30),
        U60(60),
        U100(100);

        CreatureLifespan(int value) {}
    }
}
