package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import dev.juliusabels.fish_fiesta.FishFiestaException;
import dev.juliusabels.fish_fiesta.game.ConditionType;
import dev.juliusabels.fish_fiesta.game.level.Level;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LevelManager {
    private static final String LEVEL_DIRECTORY = "levels";
    private static final String PREFERENCES_NAME = "fish_fiesta_level_data";

    private final Preferences preferences;
    private final List<String> levelIds = new ArrayList<>();
    private boolean levelsDiscovered = false;

    @Getter
    @Null
    private Level activelevel;

    public LevelManager() {
        this.preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
    }

    public void discoverLevels() {
        if (levelsDiscovered) return;

        FileHandle dir = Gdx.files.internal(LEVEL_DIRECTORY);
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

        levelsDiscovered = true;
        log.info("Discovered all levels");
    }

    public List<String> getAllLevelIds() {
        if (!levelsDiscovered) {
            discoverLevels();
        }
        return Collections.unmodifiableList(levelIds);
    }

    public boolean isLevelCompleted(String levelId) {
        return preferences.getBoolean(getLevelCompletionKey(levelId), false);
    }

    public void markLevelCompleted(String levelId, int remainingLives) {
        preferences.putBoolean(getLevelCompletionKey(levelId), true);
        preferences.putInteger(getLevelLivesKey(levelId), remainingLives);
        preferences.flush();
    }

    public int getRemainingLives(String levelId) {
        return preferences.getInteger(getLevelLivesKey(levelId), 0);
    }

    private String getLevelCompletionKey(String levelId) {
        return levelId + ".completed";
    }

    private String getLevelLivesKey(String levelId) {
        return levelId + ".lives";
    }

    public boolean loadLevel(String levelId) {
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
                log.debug("Parsed condition: {} = {}", type, conditions.get(type));
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
        level.setRemainingLives(isLevelCompleted(levelId) ? getRemainingLives(levelId) : 3);
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
