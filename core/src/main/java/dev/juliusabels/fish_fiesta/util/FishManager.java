package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import dev.juliusabels.fish_fiesta.game.ConditionType;
import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.game.level.Level;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class FishManager {
    private final List<String> fishIds = new ArrayList<>();
    private boolean allFishesFound = false;
    @Getter
    @Null
    private WaterCreature currentFish;

    public void findFishes() {
        if (allFishesFound) return;

        FileHandle dir = Gdx.files.internal("fishes");
        if (!dir.exists()) {
            log.error("The directory {} does not exist", dir);
            return;
        } else if (!dir.isDirectory()) {
            log.error("The directory {} is not a directory", dir);
            return;
        } else {
            for (FileHandle file : dir.list(".json")) {
                String fishID = file.nameWithoutExtension();
                fishIds.add(fishID);
            }
        }

        allFishesFound = true;
        log.info("Found all fishes");
    }

    public List<String> getAllFishIds() {
        //We only have this in case this method (for a reason only god knows) is called before the fish ids were loaded
        if (!allFishesFound) {
            findFishes();
        }
        return Collections.unmodifiableList(fishIds);
    }

    public boolean loadLevelForId(String fishId) {
        if (!fishIds.contains(fishId)) {
            log.error("Fish {{}} not found", fishId);
            return false;
        }
/*
        FileHandle file = ResourceHandler.fishFileHandle(fishId + ".json");
        JsonValue levelJson = new JsonReader().parse(file);

        // Parse conditions
        Map<ConditionType, List<String>> conditions = new EnumMap<>(ConditionType.class);
        JsonValue conditionsJson = levelJson.get("conditions");
        if (conditionsJson == null) {
            log.error("{}.json has no valid 'conditions' block.", fishId);
            return false;
        }

        for (ConditionType type : ConditionType.values()) {
            String typeName = type.name().toLowerCase(); //I want the json values to be lowercase
            if (!conditionsJson.has(typeName)) {
                log.warn("Condition type {} not found in {} JSON", typeName, fishId);
            } else {
                JsonValue conditionValue = conditionsJson.get(typeName);
                List<String> values = new ArrayList<>();

                if (type.isAllowMultiple()) {
                    if (conditionValue.isArray()) {
                        // Handle array values
                        for (JsonValue value = conditionValue.child; value != null; value = value.next) {
                            values.add(value.asString().toUpperCase());
                        }
                    } else {
                        // Handle single value
                        values.add(conditionValue.asString().toUpperCase());
                    }
                } else {
                    if (conditionValue.isArray()) {
                        log.error("Condition {} should not be an array. Skipping condition", typeName);
                        continue;
                    }
                    values.add(conditionValue.asString().toUpperCase());
                }

                conditions.put(type, values);
                log.debug("Parsed condition: {} = {} for level {}", type, conditions.get(type), fishId);
            }
        }

        // Parse fish IDs
        List<String> fishIDs = new ArrayList<>();
        JsonValue fishIDsJson = levelJson.get("fishIDs");
        if (fishIDsJson == null) {
            log.error("{}.json has no valid fish array.", fishId);
            return false;
        }

        for (JsonValue fishId = fishIDsJson.child; fishId != null; fishId = fishId.next) {
            fishIDs.add(fishId.asString());
        }

        if (conditions.isEmpty()) {
            log.error("No conditions were loaded for level {{}}", fishId);
            return false;
        }

        if (fishIDs.isEmpty()) {
            log.error("No fishes were loaded for level {{}}", fishId);
            return false;
        }

        Level level = new Level(fishId, conditions, fishIDs);
        level.setCompleted(isLevelCompleted(fishId));
        level.setMistakes(isLevelCompleted(fishId) ? getMistakes(fishId) : 0);
        this.activelevel = level;

 */

        return true;
    }

}
