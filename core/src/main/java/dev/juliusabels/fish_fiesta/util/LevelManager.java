package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import dev.juliusabels.fish_fiesta.FishFiestaException;
import dev.juliusabels.fish_fiesta.game.ConditionType;
import dev.juliusabels.fish_fiesta.game.level.Level;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LevelManager {
    private static final String LEVEL_DIRECTORY = "levels";
    private static final String PREFERENCES_NAME = "fish_fiesta_level_data";

    private final Preferences preferences;
    private final List<String> levelIds = new ArrayList<>();
    private boolean levelsDiscovered = false;

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

    public Level loadLevel(String levelId) {
        if (!levelIds.contains(levelId)) {
            throw new FishFiestaException("Level not found: " + levelId);
        }

        FileHandle file = ResourceHandler.levelFileHandle(levelId + ".json");
        JsonValue levelJson = new JsonReader().parse(file);

        // Parse conditions
        Map<ConditionType, String> conditions = new EnumMap<>(ConditionType.class);
        JsonValue conditionsJson = levelJson.get("conditions");

        for (ConditionType type : ConditionType.values()) {
            String typeName = type.name();
            if (conditionsJson.has(typeName)) {
                conditions.put(type, conditionsJson.getString(typeName));
            }
        }

        // Parse fish IDs
        List<String> fishIDs = new ArrayList<>();
        JsonValue fishIDsJson = levelJson.get("fishIDs");
        for (JsonValue fishId = fishIDsJson.child; fishId != null; fishId = fishId.next) {
            fishIDs.add(fishId.asString());
        }

        Level level = new Level(levelId, conditions, fishIDs);
        level.setCompleted(isLevelCompleted(levelId));
        level.setRemainingLives(isLevelCompleted(levelId) ?
            getRemainingLives(levelId) : 3);

        return level;
    }

    public void saveLevelMetric(String levelId, String metricName, String value) {
        preferences.putString(levelId + "." + metricName, value);
        preferences.flush();
    }

    public String getLevelMetric(String levelId, String metricName, String defaultValue) {
        return preferences.getString(levelId + "." + metricName, defaultValue);
    }
}
