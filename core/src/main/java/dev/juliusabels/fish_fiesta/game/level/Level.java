package dev.juliusabels.fish_fiesta.game.level;

import dev.juliusabels.fish_fiesta.game.WaterCreature;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Represents a game level in the Fish Fiesta game.
 * <p>
 * Each level contains a set of conditions that water creatures must meet,
 * a list of fish IDs that are part of the level, and various state tracking
 * variables to monitor player progress through the level.
 */
@Getter
public class Level {
    /** Unique identifier for this level */
    private final String id;

    /** Map of condition types to lists of valid values for each condition */
    private final Map<ConditionType, List<String>> conditions;

    /** Ordered list of fish IDs that appear in this level */
    private final List<String> fishIDs;

    /** Whether the level has been successfully completed */
    @Setter
    private boolean completed;

    /** Whether the level is currently in progress */
    @Setter
    private boolean inProgress;

    /** Whether the player has failed the level */
    @Setter
    private boolean failed;

    /** Number of mistakes made by the player in this level */
    @Setter
    private int mistakes;

    /** Current index in the fishIDs list, representing the active fish */
    @Setter
    private int fishIndex;

    /**
     * Creates a new level with the specified parameters.
     *
     * @param id The unique identifier for this level
     * @param conditions Map of condition types to lists of valid values
     * @param fishIds Ordered list of fish IDs that appear in this level
     */
    public Level(String id, Map<ConditionType, List<String>> conditions, List<String> fishIds) {
        this.id = id;
        this.conditions = conditions;
        this.fishIDs = fishIds;
        this.completed = false;
        this.failed = false;
        this.inProgress = false;
        this.mistakes = 0;
        this.fishIndex = 0;
    }

    /**
     * Checks if a water creature meets all the conditions required by this level.
     * <p>
     * A creature meets the conditions if it satisfies all condition types defined
     * in the level's condition map.
     *
     * @param creature The water creature to check against the level's conditions
     * @return true if the creature meets all conditions, false otherwise
     */
    public boolean meetsConditions(WaterCreature creature) {
        return conditions.entrySet().stream()
            .allMatch(entry -> entry.getKey().isSatisfied(creature, entry.getValue()));
    }

    /**
     * Increments the number of mistakes made in this level by one.
     */
    public void increaseMistakes() {
        this.setMistakes(this.getMistakes() + 1);
    }
}
