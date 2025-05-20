package dev.juliusabels.fish_fiesta.game.features;

import lombok.Getter;

public enum SizeCategory {
    SMALL("small"),
    MEDIUM("medium sized"),
    BIG("big"),
    UNDEFINED("");


    @Getter
    private final String formattedForDesc;

    SizeCategory(String formatted) {
        this.formattedForDesc = formatted;
    }
}
