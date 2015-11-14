package io.github.gimmi;

import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.util.TreeMap;

public class AnyMapBuilder {
	private final TreeMap<String, Any> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	public AnyMapBuilder put(String key, Any value) {
		key = stripToNull(key);
		if (key != null) {
			if (value == null) {
				value = new Any(null, null, null);
			}
			map.put(key, value);
		}
		return this;
	}

	public Any build() {
		TreeMap<String, Any> mapCopy = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		mapCopy.putAll(map);
		return new Any(null, mapCopy, null);
	}
}
