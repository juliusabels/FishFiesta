package dev.juliusabels.fish_fiesta.game;

import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Represents a water creature (fish) in this game.
 * <p>
 * This class stores all the relevant data about water creatures including their
 * name, description, notable features, size, and water environment preferences.
 * Each water creature can be identified by a unique ID derived from its name.
 */
@Getter
@AllArgsConstructor
public class WaterCreature {
    /** The display name of the water creature */
    private String name;

    /** Detailed description of the water creature */
    private String description;

    /** Specific notable features of the water creature */
    private String notableFeatures;

    /** Size classification of the water creature */
    private CreatureSize size;

    /** List of water types this creature can live in (max. 2) */
    private List<WaterType> waterTypes;

    /** List of water subtypes this creature can live in */
    private List<WaterSubtype> waterSubtypes;

    /** List of water temperatures this creature can live in (max. 3) */
    private List<WaterTemperature> waterTemperatures;
}
