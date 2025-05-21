package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import dev.juliusabels.fish_fiesta.game.CreatureSize;
import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Manages fish data loading and storage for the Fish Fiesta game.
 * <p>
 * This class is responsible for discovering available fish IDs, loading fish data from JSON files,
 * and providing access to the currently active fish. It uses JsonHelper to parse fish attributes
 * from JSON files located in the "fishes" directory.
 */
@Slf4j
public class FishManager {
    /** List of all discovered fish IDs */
    private final List<String> fishIds = new ArrayList<>();

    /** Flag to track whether fish discovery has been completed */
    private boolean allFishesFound = false;

    /** The currently loaded fish, or null if none is loaded */
    @Getter
    @Null
    private WaterCreature currentFish;

    /**
     * Scans the "fishes" directory to discover all available fish IDs.
     * <p>
     * This method reads all JSON files in the "fishes" directory and adds their names (without extension)
     * to the fishIds list. It only runs once; subsequent calls have no effect if fish discovery is already complete.
     * <p>
     * This is all done so we don't have to load all fish objects on startup to safe time and memory.
     */
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

    /**
     * Returns an unmodifiable list of all discovered fish IDs.
     * <p>
     * If fish discovery has not yet been performed, this method will trigger it.
     *
     * @return An unmodifiable list containing all available fish IDs
     */
    public List<String> getAllFishIds() {
        // We only have this in case this method (for a reason only god knows) is called before the fish ids were loaded
        if (!allFishesFound) {
            findFishes();
        }
        return Collections.unmodifiableList(fishIds);
    }

    /**
     * Loads a fish's data from its JSON file and sets it as the current fish.
     * <p>
     * This method parses the fish data including its description, size, and water preferences
     * from the corresponding JSON file. It creates a new WaterCreature instance and sets it
     * as the current fish.
     *
     * @param fishId The ID of the fish to load
     * @return true if the fish was successfully loaded, false otherwise
     */
    public boolean loadFishForId(String fishId) {
        if (!fishIds.contains(fishId)) {
            log.error("Fish {{}} not found", fishId);
            return false;
        }

        FileHandle file = ResourceHandler.fishFileHandle(fishId + ".json");
        JsonValue fishJson = new JsonReader().parse(file);

        String name = formatIdToName(fishId);

        String description = JsonHelper.getStringValue(fishJson, "description");
        String notableFeatures = JsonHelper.getStringValue(fishJson, "notableFeatures");

        CreatureSize size = new CreatureSize(JsonHelper.getIntValue(fishJson, "minSize"), JsonHelper.getIntValue(fishJson, "maxSize"));
        List<WaterType> waterTypes = WaterType.mapFromStrings(JsonHelper.getList(fishJson, "waterTypes"));
        List<WaterSubtype> waterSubtypes = WaterSubtype.mapFromStrings(JsonHelper.getList(fishJson, "waterSubtypes"));
        List<WaterTemperature> waterTemperatures = WaterTemperature.mapFromStrings(JsonHelper.getList(fishJson, "waterTemperatures"));

        this.currentFish = new WaterCreature(name, description, notableFeatures, size, waterTypes, waterSubtypes, waterTemperatures);

        return true;
    }

    /**
     * Converts a snake_case ID into a readable Title Case name.
     * <p>
     * For example, "common_carp" becomes "Common Carp"
     *
     * @param id The snake_case ID to format
     * @return A properly capitalized, space-separated name
     */
    public static String formatIdToName(String id) {
        final StringBuilder builder = new StringBuilder();

        for (String part : id.split("_")) {
            if (!builder.isEmpty()) {
                builder.append(" ");
            }
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return builder.toString();
    }
}
