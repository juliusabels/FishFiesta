package dev.juliusabels.fish_fiesta.game.level;

import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.game.features.SizeCategory;
import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public enum ConditionType {
    WATER_TYPE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            creature.getWaterTypes().forEach(v -> log.debug("Fish water type: {}", v));
            values.forEach(v -> log.debug("Level allows water type: {}", v));
            return values.stream()
                .map(WaterType::valueOf)
                .anyMatch(type -> creature.getWaterTypes().contains(type));
        }
    },
    WATER_SUBTYPE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            creature.getWaterSubtypes().forEach(v -> log.debug("Fish water subtype: {}", v));
            values.forEach(v -> log.debug("Level allows water subtype: {}", v));
            return values.stream()
                .map(WaterSubtype::valueOf)
                .anyMatch(subtype -> creature.getWaterSubtypes().contains(subtype));
        }
    },
    SIZE(true) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            log.debug("Fish size is: {}", creature.getSize().getCategoriesFromSize().name());
            values.forEach(v -> log.debug("Level allows size: {}", v));
            SizeCategory creatureSize = creature.getSize().getCategoriesFromSize();
            return values.stream()
                .map(SizeCategory::valueOf)
                .anyMatch(size -> creatureSize == size);
        }
    },
    TEMPERATURE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            creature.getWaterTemperatures().forEach(v -> log.debug("Fish Temperature has: {}", v));
            values.forEach(v -> log.debug("Level allows temperature: {}", v));
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
