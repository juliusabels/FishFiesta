package dev.juliusabels.fish_fiesta.game.level;

import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.game.features.SizeCategory;
import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Enumerates the types of conditions that can be applied to filter water creatures in a level.
 * <p>
 * Each condition type represents a specific attribute of a water creature that can be used
 * as a filtering criterion. The enum implementation defines the logic for checking whether
 * a creature satisfies a particular condition based on the attribute values.
 * <p>
 * Conditions can be configured to allow multiple values (like SIZE) or require just one match
 * from multiple possible values (like WATER_TYPE, WATER_SUBTYPE, TEMPERATURE).
 */
@Slf4j
public enum ConditionType {
    /**
     * Water type condition - checks if a creature can live in any of the specified water types.
     * <p>
     * A creature satisfies this condition if at least one of its compatible water types
     * matches any of the allowed types specified in the condition values.
     */
    WATER_TYPE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            if (log.isDebugEnabled()) {
                creature.getWaterTypes().forEach(v -> log.debug("Fish water type: {}", v));
                values.forEach(v -> log.debug("Level allows water type: {}", v));
            }

            return values.stream()
                .map(WaterType::valueOf)
                .anyMatch(type -> creature.getWaterTypes().contains(type));
        }
    },

    /**
     * Water subtype condition - checks if a creature can live in any of the specified water subtypes.
     * <p>
     * A creature satisfies this condition if at least one of its compatible water subtypes
     * matches any of the allowed subtypes specified in the condition values.
     */
    WATER_SUBTYPE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            if (log.isDebugEnabled()) {
                creature.getWaterSubtypes().forEach(v -> log.debug("Fish water subtype: {}", v));
                values.forEach(v -> log.debug("Level allows water subtype: {}", v));
            }

            return values.stream()
                .map(WaterSubtype::valueOf)
                .anyMatch(subtype -> creature.getWaterSubtypes().contains(subtype));
        }
    },

    /**
     * Size condition - checks if a creature's size category matches any of the specified categories.
     * <p>
     * A creature satisfies this condition if its size category matches any of the
     * allowed size categories specified in the condition values.
     */
    SIZE(true) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            SizeCategory creatureSize = creature.getSize().getCategory();

            if (log.isDebugEnabled()) {
                log.debug("Fish size is: {}", creatureSize);
                values.forEach(v -> log.debug("Level allows size: {}", v));
            }

            return values.stream()
                .map(SizeCategory::valueOf)
                .anyMatch(size -> creatureSize == size);
        }
    },

    /**
     * Temperature condition - checks if a creature can live in any of the specified temperature ranges.
     * <p>
     * A creature satisfies this condition if at least one of its compatible water temperatures
     * matches any of the allowed temperatures specified in the condition values.
     */
    TEMPERATURE(false) {
        @Override
        public boolean isSatisfied(WaterCreature creature, List<String> values) {
            if (log.isDebugEnabled()) {
                creature.getWaterTemperatures().forEach(v -> log.debug("Fish temperature: {}", v));
                values.forEach(v -> log.debug("Level allows temperature: {}", v));
            }

            return values.stream()
                .map(WaterTemperature::valueOf)
                .anyMatch(temp -> creature.getWaterTemperatures().contains(temp));
        }
    };

    /** Indicates whether multiple instances of this condition type are allowed in a level */
    @Getter
    private final boolean allowMultiple;

    /**
     * Creates a new condition type.
     *
     * @param allowMultiple if true, multiple instances of this condition type can be used in a level
     */
    ConditionType(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    /**
     * Checks if a water creature satisfies this condition given a list of acceptable values.
     *
     * @param creature The water creature to check against the condition
     * @param values List of acceptable values for this condition type (as strings)
     * @return true if the creature satisfies the condition, false otherwise
     */
    public abstract boolean isSatisfied(WaterCreature creature, List<String> values);
}
