package dev.juliusabels.fish_fiesta.game;

import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;
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
    private List<WaterTemperature> waterTemperatures;

    public String getID() {
        return name.replace(" ", "_").toLowerCase();
    }
}
