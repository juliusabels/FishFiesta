package dev.juliusabels.fish_fiesta.util;

import com.badlogic.gdx.utils.JsonValue;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonHelper {
    public static int getIntValue(JsonValue parent, String name) {
        JsonValue value = parent.get(name);
        if (value == null) {
            log.warn("No value \"{}\" was found in json", name);
            return 0;
        }
        return value.asInt();
    }

    public static String getStringValue(JsonValue parent, String name) {
        JsonValue value = parent.get(name);
        if (value == null) {
            log.warn("No value \"{}\" was found in json", name);
            return "";
        }
        return value.asString();
    }

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
}
