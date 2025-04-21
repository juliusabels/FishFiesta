package dev.juliusabels.fish_fiesta.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class WaterCreature {
    private String name;
    private String description;
    private String notableFeatures;
    private CreatureSize size;
    private List<WaterType> waterTypes;
    private List<WaterSubtype> waterSubtypes;
    private Optional<CreatureLifespan> lifespan;

    public String getID() {
        return name.replace(" ", "_").toLowerCase();
    }

    public enum WaterType {
        SALT_WATER,
        FRESH_WATER
    }

    public enum WaterSubtype {
        DEEPSEA,
        COAST,
        OPEN_OCEAN,
        CORAL_REEF,
        LAKE,
        RIVER,
        KELP_FOREST
    }
}
