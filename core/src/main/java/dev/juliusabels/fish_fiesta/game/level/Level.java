package dev.juliusabels.fish_fiesta.game.level;

import dev.juliusabels.fish_fiesta.game.ConditionType;
import dev.juliusabels.fish_fiesta.game.WaterCreature;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
public class Level {
    private final String id;
    private final Map<ConditionType, List<String>> conditions;
    private final List<String> fishIDs;
    @Setter
    private boolean completed;
    @Setter
    private int remainingLives;

    public Level(String id, Map<ConditionType, List<String>> conditions, List<String> fishIds) {
        this.id = id;
        this.conditions = conditions;
        this.fishIDs = fishIds;
        this.completed = false;
        this.remainingLives = 3;
    }

    public boolean meetsConditions(WaterCreature creature) {
        return conditions.entrySet().stream()
            .allMatch(entry -> entry.getKey().isSatisfied(creature, entry.getValue()));
    }

}
