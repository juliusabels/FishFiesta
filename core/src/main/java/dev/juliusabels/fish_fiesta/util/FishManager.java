package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import dev.juliusabels.fish_fiesta.game.ConditionType;
import dev.juliusabels.fish_fiesta.game.CreatureSize;
import dev.juliusabels.fish_fiesta.game.WaterCreature;
import dev.juliusabels.fish_fiesta.game.features.WaterSubtype;
import dev.juliusabels.fish_fiesta.game.features.WaterTemperature;
import dev.juliusabels.fish_fiesta.game.features.WaterType;
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
