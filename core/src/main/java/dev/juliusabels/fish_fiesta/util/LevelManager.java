package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import dev.juliusabels.fish_fiesta.game.ConditionType;
import dev.juliusabels.fish_fiesta.game.level.Level;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LevelManager {
    private static final String PREFERENCES_NAME = "fish_fiesta_level_data";

    private final Preferences preferences;
    private final List<String> levelIds = new ArrayList<>();
    private boolean allLevelsFound = false;

    @Getter
    @Null
    private Level activelevel;

    public LevelManager() {
        this.preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
    }

    public void findLevels() {
        if (allLevelsFound) return;

        FileHandle dir = Gdx.files.internal("levels");
        if (!dir.exists()) {
            log.error("The directory {} does not exist", dir);
            return;
        } else if (!dir.isDirectory()) {
            log.error("The directory {} is not a directory", dir);
            return;
        } else {
            for (FileHandle file : dir.list(".json")) {
                String levelId = file.nameWithoutExtension();
                levelIds.add(levelId);
            }
        }

        allLevelsFound = true;
        log.info("Found all levels");
    }

    public List<String> getAllLevelIds() {
        //We only have this in case this method (for a reason only god knows) is called before the level ids were loaded
        if (!allLevelsFound) {
            findLevels();
        }
        return Collections.unmodifiableList(levelIds);
    }

    public boolean isLevelCompleted(String levelId) {
        return preferences.getBoolean(getLevelCompletionKey(levelId), false);
    }

    public void markLevelCompleted(String levelId, int mistakes) {
        preferences.putBoolean(getLevelCompletionKey(levelId), true);
        preferences.putInteger(getLevelMistakes(levelId), mistakes);
        preferences.flush();
    }

    public int getMistakes(String levelId) {
        return preferences.getInteger(getLevelMistakes(levelId), 0);
    }

    private String getLevelCompletionKey(String levelId) {
        return levelId + ".completed";
    }

    private String getLevelMistakes(String levelId) {
        return levelId + ".mistakes";
    }

    public boolean loadLevelForId(String levelId) {
        if (!levelIds.contains(levelId)) {
           log.error("Level {{}} not found", levelId);
            return false;
        }

        FileHandle file = ResourceHandler.levelFileHandle(levelId + ".json");
        JsonValue levelJson = new JsonReader().parse(file);

        // Parse conditions
        Map<ConditionType, List<String>> conditions = new EnumMap<>(ConditionType.class);
        JsonValue conditionsJson = levelJson.get("conditions");
        if (conditionsJson == null) {
            log.error("{}.json has no valid 'conditions' block.", levelId);
            return false;
        }

        for (ConditionType type : ConditionType.values()) {
            String typeName = type.name().toLowerCase(); //I want the json values to be lowercase
            if (!conditionsJson.has(typeName)) {
                log.warn("Condition type {} not found in {} JSON", typeName, levelId);
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
                log.debug("Parsed condition: {} = {} for level {}", type, conditions.get(type), levelId);
            }
        }

        // Parse fish IDs
        List<String> fishIDs = new ArrayList<>();
        JsonValue fishIDsJson = levelJson.get("fishIDs");
        if (fishIDsJson == null) {
            log.error("{}.json has no valid fish array.", levelId);
            return false;
        }

        for (JsonValue fishId = fishIDsJson.child; fishId != null; fishId = fishId.next) {
            fishIDs.add(fishId.asString());
        }

        if (conditions.isEmpty()) {
            log.error("No conditions were loaded for level {{}}", levelId);
            return false;
        }

        if (fishIDs.isEmpty()) {
            log.error("No fishes were loaded for level {{}}", levelId);
            return false;
        }

        Level level = new Level(levelId, conditions, fishIDs);
        level.setCompleted(isLevelCompleted(levelId));
        level.setMistakes(isLevelCompleted(levelId) ? getMistakes(levelId) : 0);
        this.activelevel = level;

        return true;
    }

    public void saveLevelMetric(String levelId, String metricName, String value) {
        preferences.putString(levelId + "." + metricName, value);
        preferences.flush();
    }

    public String getLevelMetric(String levelId, String metricName, String defaultValue) {
        return preferences.getString(levelId + "." + metricName, defaultValue);
    }
}
