package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import dev.juliusabels.fish_fiesta.game.level.ConditionType;
import dev.juliusabels.fish_fiesta.game.level.Level;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LevelManager {
    private static final String PREFERENCES_NAME = "fish_fiesta_level_data";

    private final Preferences preferences;
    private final List<String> levelIds = new ArrayList<>();
    private boolean allLevelsFound = false;

    @Setter
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
        clearPreferences(levelId);
        preferences.putBoolean(getLevelCompletionKey(levelId), true);
        preferences.putInteger(getLevelMistakesKey(levelId), mistakes);
        preferences.flush();
    }

    public boolean isLevelInProgress(String levelId) {
        return preferences.getBoolean(getLevelInProgressKey(levelId), false);
    }

    public void safeLevelProgress(String levelId, int mistakes, int fishIndex) {
        clearPreferences(levelId);
        preferences.putBoolean(getLevelInProgressKey(levelId), true);
        preferences.putInteger(getLevelFishIndexKey(levelId), fishIndex);
        preferences.putInteger(getLevelMistakesKey(levelId), mistakes);
        preferences.flush();
    }

    public boolean isLevelFailed(String levelId) {
        return preferences.getBoolean(getLevelFailedKey(levelId), false);
    }

    public void markLevelFailed(String levelId, int mistakes) {
        clearPreferences(levelId);
        preferences.putInteger(getLevelMistakesKey(levelId), mistakes);
        preferences.putBoolean(getLevelFailedKey(levelId), true);
        preferences.flush();
    }

    public void clearPreferences(String levelId) {
        preferences.putInteger(getLevelMistakesKey(levelId), 0);
        preferences.putInteger(getLevelFishIndexKey(levelId), 0);
        preferences.putBoolean(getLevelFailedKey(levelId), false);
        preferences.putBoolean(getLevelInProgressKey(levelId), false);
        preferences.putBoolean(getLevelCompletionKey(levelId), false);
        preferences.flush();
    }

    public int getMistakes(String levelId) {
        return preferences.getInteger(getLevelMistakesKey(levelId), 0);
    }

    public int getFishIndex(String levelId) {
        return preferences.getInteger(getLevelFishIndexKey(levelId), 0);
    }

    private String getLevelCompletionKey(String levelId) {
        return levelId + ".completed";
    }

    private String getLevelInProgressKey(String levelId) {
        return levelId + ".in_progress";
    }

    private String getLevelFailedKey(String levelId) {
        return levelId + ".failed";
    }

    private String getLevelMistakesKey(String levelId) {
        return levelId + ".mistakes";
    }

    private String getLevelFishIndexKey(String levelId) {
        return levelId + ".fish_index";
    }

    //TODO maybe make use of JsonHelper here as well
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
        level.setCompleted(false);
        level.setMistakes(isLevelInProgress(levelId) ? getMistakes(levelId) : 0);
        level.setFishIndex(getFishIndex(levelId));
        level.setFailed(false);
        level.setInProgress(isLevelInProgress(levelId));
        this.setActivelevel(level);

        return true;
    }
}
