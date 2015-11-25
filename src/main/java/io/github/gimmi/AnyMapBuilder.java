package io.github.gimmi;

import java.util.Map;
import java.util.TreeMap;

import static org.apache.commons.lang3.StringUtils.stripToNull;

public class AnyMapBuilder {
	private final TreeMap<String, AnyListBuilder> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	public AnyMapBuilder put(String key, Any value) {
		key = stripToNull(key);
		if (key != null) {
			map.computeIfAbsent(key, k -> new AnyListBuilder()).put(value);
		}
		return this;
	}

	public long length() {
		return map.values().stream()
			.filter(x -> x.length() > 0)
			.count();
	}

	public Any build() {
		if (map.isEmpty()) {
			return Any.NULL;
		} else {
			TreeMap<String, Any> mapCopy = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			for (Map.Entry<String, AnyListBuilder> entry : map.entrySet()) {
				if (entry.getValue().length() > 0) {
					mapCopy.put(entry.getKey(), entry.getValue().build());
				}
			}
			return new Any(null, mapCopy, null);
		}
	}
}
