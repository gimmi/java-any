package io.github.gimmi;

import java.util.HashMap;
import java.util.Map;

public class AnyMapBuilder {
	private Map<String, Any> map = new HashMap<>();

	public AnyMapBuilder put(String key, Any value) {
		map.put(key, value);
		return this;
	}
	
	public Any build() {
		return new Any(new HashMap<>(map));
	}
}
