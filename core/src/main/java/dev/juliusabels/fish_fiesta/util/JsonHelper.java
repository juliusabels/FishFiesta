package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.utils.JsonValue;
import dev.juliusabels.fish_fiesta.game.level.ConditionType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing and extracting data from LibGDX JsonValue objects.
 * <p>
 * This helper provides methods to safely extract various data types from JSON structures,
 * with appropriate error logging when values are missing or incorrectly formatted.
 * It's primarily used for loading game data files.
 */
@Slf4j
public class JsonHelper {

    /**
     * Extracts an integer value from a JSON object.
     *
     * @param parent The JSON object containing the desired value
     * @param name The name of the value to extract
     * @return The extracted integer value, or 0 if the value doesn't exist
     */
    public static int getIntValue(JsonValue parent, String name) {
        JsonValue value = parent.get(name);
        if (value == null) {
            log.warn("No value \"{}\" was found in json", name);
            return 0;
        }
        return value.asInt();
    }

    /**
     * Extracts a string value from a JSON object.
     *
     * @param parent The JSON object containing the desired value
     * @param name The name of the value to extract
     * @return The extracted string value, or an empty string if the value doesn't exist
     */
    public static String getStringValue(JsonValue parent, String name) {
        JsonValue value = parent.get(name);
        if (value == null) {
            log.warn("No value \"{}\" was found in json", name);
            return "";
        }
        return value.asString();
    }

    /**
     * Extracts a list of strings from a JSON array.
     *
     * @param parent The JSON object containing the array
     * @param name The name of the array to extract
     * @return A list containing the string values from the array, or an empty list if
     *         the array doesn't exist or isn't properly formatted
     */
    public static List<String> getList(JsonValue parent, String name) {
        JsonValue value = parent.get(name);

        List<String> list = new ArrayList<>();
        if (value == null) {
            log.error("Json value for {} is null", name);
            return list;
        } else if (!value.isArray()) {
            log.error("Json value for {} is not an array", name);
            return list;
        }

        for (JsonValue childValue = value.child; childValue != null; childValue = childValue.next) {
            list.add(childValue.asString());
        }
        return list;
    }

    /**
     * Extracts condition mappings from a JSON object.
     * <p>
     * Parses a JSON structure representing game conditions, handling both single and multiple
     * values based on the condition type's configuration. All condition values are converted
     * to uppercase for consistent processing.
     *
     * @param parent The JSON object containing the conditions block
     * @param name The name of the conditions block to extract
     * @return A map of condition types to their associated values, or an empty map if
     *         the conditions block doesn't exist or contains errors
     */
    public static Map<ConditionType, List<String>> getConditions(JsonValue parent, String name) {
        Map<ConditionType, List<String>> conditions = new EnumMap<>(ConditionType.class);
        JsonValue conditionsJson = parent.get(name);

        if (conditionsJson == null) {
            log.error("JSON has no valid '{}' block.", name);
            return conditions;
        }

        for (ConditionType type : ConditionType.values()) {
            String typeName = type.name().toLowerCase(); // JSON values are lowercase
            if (!conditionsJson.has(typeName)) {
                log.warn("Condition type {} not found in JSON", typeName);
                continue;
            }

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
            log.debug("Parsed condition: {} = {}", type, values);
        }

        return conditions;
    }
}
