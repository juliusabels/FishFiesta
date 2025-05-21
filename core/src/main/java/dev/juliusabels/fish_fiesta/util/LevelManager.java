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

/**
 * Manages game level data and persistence for the Fish Fiesta game.
 * <p>
 * This class is responsible for discovering available level IDs, loading level data
 * from JSON files, tracking level progress, and managing level completion states.
 * It persists level progress information using LibGDX's Preferences system.
 */
@Slf4j
public class LevelManager {
    /** Name of the preferences file used to store level progress data */
    private static final String PREFERENCES_NAME = "fish_fiesta_level_data";

    /** Preferences instance for persisting level progress */
    private final Preferences preferences;

    /** List of all discovered level IDs */
    private final List<String> levelIds = new ArrayList<>();

    /** Flag to track whether level discovery has been completed */
    private boolean allLevelsFound = false;

    /** The currently active level, or null if none is loaded */
    @Setter
    @Getter
    @Null
    private Level activeLevel;

    /**
     * Creates a new level manager and initializes the preferences system.
     */
    public LevelManager() {
        this.preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
    }

    /**
     * Scans the "levels" directory to discover all available level IDs.
     * <p>
     * This method reads all JSON files in the "levels" directory and adds their names (without extension)
     * to the levelIds list. It only runs once; subsequent calls have no effect if level discovery is already complete.
     * <p>
     * This is all done so we don't have to load all fish objects on startup to safe time and memory.
     */
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

    /**
     * Returns an unmodifiable list of all discovered level IDs.
     * <p>
     * If level discovery has not yet been performed, this method will trigger it.
     *
     * @return An unmodifiable list containing all available level IDs
     */
    public List<String> getAllLevelIds() {
        // If levels haven't been found yet, find them first
        if (!allLevelsFound) {
            findLevels();
        }
        return Collections.unmodifiableList(levelIds);
    }

    /**
     * Loads a level's data from its JSON file and sets it as the active level.
     * <p>
     * This method parses the level data including its conditions and fish IDs from the
     * corresponding JSON file. It creates a new Level instance and sets it as the active level.
     *
     * @param levelId The ID of the level to load
     * @return true if the level was successfully loaded, false otherwise
     */
    public boolean loadLevelForId(String levelId) {
        if (!levelIds.contains(levelId)) {
            log.error("Level {} not found", levelId);
            return false;
        }

        FileHandle file = ResourceHandler.levelFileHandle(levelId + ".json");
        JsonValue levelJson = new JsonReader().parse(file);

        Map<ConditionType, List<String>> conditions = JsonHelper.getConditions(levelJson, "conditions");
        if (conditions.isEmpty()) {
            log.error("No conditions were loaded for level {}", levelId);
            return false;
        }

        List<String> fishIDs = JsonHelper.getList(levelJson, "fishIDs");
        if (fishIDs.isEmpty()) {
            log.error("No fishes were loaded for level {}", levelId);
            return false;
        }

        Level level = new Level(levelId, conditions, fishIDs);
        level.setCompleted(false);
        level.setMistakes(isLevelInProgress(levelId) ? getMistakes(levelId) : 0);
        level.setFishIndex(getFishIndex(levelId));
        level.setFailed(false);
        level.setInProgress(isLevelInProgress(levelId));
        this.setActiveLevel(level);

        return true;
    }

    /**
     * Checks if a level has been completed.
     *
     * @param levelId The ID of the level to check
     * @return true if the level has been completed, false otherwise
     */
    public boolean isLevelCompleted(String levelId) {
        return preferences.getBoolean(getLevelCompletionKey(levelId), false);
    }

    /**
     * Marks a level as completed and stores the number of mistakes made.
     *
     * @param levelId The ID of the level to mark as completed
     * @param mistakes The number of mistakes made during level completion
     */
    public void markLevelCompleted(String levelId, int mistakes) {
        clearPreferences(levelId);
        preferences.putBoolean(getLevelCompletionKey(levelId), true);
        preferences.putInteger(getLevelMistakesKey(levelId), mistakes);
        preferences.flush();
    }

    /**
     * Checks if a level is currently in progress.
     *
     * @param levelId The ID of the level to check
     * @return true if the level is in progress, false otherwise
     */
    public boolean isLevelInProgress(String levelId) {
        return preferences.getBoolean(getLevelInProgressKey(levelId), false);
    }

    /**
     * Saves the current progress of a level.
     *
     * @param levelId The ID of the level
     * @param mistakes The current number of mistakes made
     * @param fishIndex The current index in the fish list
     */
    public void safeLevelProgress(String levelId, int mistakes, int fishIndex) {
        clearPreferences(levelId);
        preferences.putBoolean(getLevelInProgressKey(levelId), true);
        preferences.putInteger(getLevelFishIndexKey(levelId), fishIndex);
        preferences.putInteger(getLevelMistakesKey(levelId), mistakes);
        preferences.flush();
    }

    /**
     * Checks if a level has been failed.
     *
     * @param levelId The ID of the level to check
     * @return true if the level has been failed, false otherwise
     */
    public boolean isLevelFailed(String levelId) {
        return preferences.getBoolean(getLevelFailedKey(levelId), false);
    }

    /**
     * Marks a level as failed and stores the number of mistakes made.
     *
     * @param levelId The ID of the level to mark as failed
     * @param mistakes The number of mistakes made before failure
     */
    public void markLevelFailed(String levelId, int mistakes) {
        clearPreferences(levelId);
        preferences.putInteger(getLevelMistakesKey(levelId), mistakes);
        preferences.putBoolean(getLevelFailedKey(levelId), true);
        preferences.flush();
    }

    /**
     * Clears all stored data for a level.
     *
     * @param levelId The ID of the level to clear
     */
    public void clearPreferences(String levelId) {
        preferences.putInteger(getLevelMistakesKey(levelId), 0);
        preferences.putInteger(getLevelFishIndexKey(levelId), 0);
        preferences.putBoolean(getLevelFailedKey(levelId), false);
        preferences.putBoolean(getLevelInProgressKey(levelId), false);
        preferences.putBoolean(getLevelCompletionKey(levelId), false);
        preferences.flush();
    }

    /**
     * Gets the number of mistakes made in a level.
     *
     * @param levelId The ID of the level
     * @return The number of mistakes made
     */
    public int getMistakes(String levelId) {
        return preferences.getInteger(getLevelMistakesKey(levelId), 0);
    }

    /**
     * Gets the current fish index for a level in progress.
     *
     * @param levelId The ID of the level
     * @return The current fish index
     */
    public int getFishIndex(String levelId) {
        return preferences.getInteger(getLevelFishIndexKey(levelId), 0);
    }

    /**
     * Generates the preference key for level completion status.
     *
     * @param levelId The ID of the level
     * @return The preference key for the level's completion status
     */
    private String getLevelCompletionKey(String levelId) {
        return levelId + ".completed";
    }

    /**
     * Generates the preference key for level in-progress status.
     *
     * @param levelId The ID of the level
     * @return The preference key for the level's in-progress status
     */
    private String getLevelInProgressKey(String levelId) {
        return levelId + ".in_progress";
    }

    /**
     * Generates the preference key for level failed status.
     *
     * @param levelId The ID of the level
     * @return The preference key for the level's failed status
     */
    private String getLevelFailedKey(String levelId) {
        return levelId + ".failed";
    }

    /**
     * Generates the preference key for level mistakes count.
     *
     * @param levelId The ID of the level
     * @return The preference key for the level's mistakes count
     */
    private String getLevelMistakesKey(String levelId) {
        return levelId + ".mistakes";
    }

    /**
     * Generates the preference key for level fish index.
     *
     * @param levelId The ID of the level
     * @return The preference key for the level's fish index
     */
    private String getLevelFishIndexKey(String levelId) {
        return levelId + ".fish_index";
    }
}
