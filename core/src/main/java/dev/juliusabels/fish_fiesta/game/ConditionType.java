package dev.juliusabels.fish_fiesta.game;

import dev.juliusabels.fish_fiesta.game.features.SizeCategory;
import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;
import lombok.Getter;

import java.util.List;

public enum ConditionType {
    WATER_TYPE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            return values.stream()
                .map(WaterType::valueOf)
                .anyMatch(type -> creature.getWaterTypes().contains(type));
        }
    },
    WATER_SUBTYPE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            return values.stream()
                .map(WaterSubtype::valueOf)
                .anyMatch(subtype -> creature.getWaterSubtypes().contains(subtype));
        }
    },
    SIZE(true) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            SizeCategory creatureSize = creature.getSize().getCategoriesFromSize();
            return values.stream()
                .map(SizeCategory::valueOf)
                .anyMatch(size -> creatureSize == size);
        }
    },
    TEMPERATURE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            return values.stream()
                .map(WaterTemperature::valueOf)
                .anyMatch(temp -> creature.getWaterTemperatures().contains(temp));
        }
    };

    @Getter
    private final boolean allowMultiple;

    ConditionType(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public abstract boolean isSatisfied(WaterCreature creature, List<String> values);
}
