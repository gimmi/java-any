package io.github.gimmi;

import java.util.TreeMap;

import static org.apache.commons.lang3.StringUtils.stripToNull;

public class AnyMapBuilder {
    private final TreeMap<String, Any> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public AnyMapBuilder put(String key, Any value) {
        key = stripToNull(key);
        if (key == null) {
            return this;
        } else if (value == null || value.size() < 1) {
            map.remove(key);
        } else {
            map.put(key, value);
        }
        return this;
    }

    public Any build() {
        if (map.isEmpty()) {
            return Any.NULL;
        } else {
            TreeMap<String, Any> mapCopy = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            mapCopy.putAll(map);
            return new Any(null, mapCopy, null);
        }
    }
}
