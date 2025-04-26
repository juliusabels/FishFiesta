package dev.juliusabels.fish_fiesta.game;

import dev.juliusabels.fish_fiesta.game.features.SizeCategory;
import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;

public enum ConditionType {
    WATER_TYPE {
        @Override
        public boolean isSatisfied(WaterCreature creature, String value) {
            WaterType requiredType = WaterType.valueOf(value);
            return creature.getWaterTypes().contains(requiredType);
        }
    },
    WATER_SUBTYPE {
        @Override
        public boolean isSatisfied(WaterCreature creature, String value) {
            WaterSubtype requiredSubtype = WaterSubtype.valueOf(value);
            return creature.getWaterSubtypes().contains(requiredSubtype);
        }
    },
    SIZE_CATEGORY {
        @Override
        public boolean isSatisfied(WaterCreature creature, String value) {
            SizeCategory requiredSize = SizeCategory.valueOf(value);
            return creature.getSize().getCategoriesFromSize() == requiredSize;
        }
    },
    TEMPERATURE {
        @Override
        public boolean isSatisfied(WaterCreature creature, String value) {
            WaterTemperature temperature = WaterTemperature.valueOf(value);
            return creature.getWaterTemperatures().contains(temperature);
        }
    };
    public abstract boolean isSatisfied(WaterCreature creature, String value);
}
