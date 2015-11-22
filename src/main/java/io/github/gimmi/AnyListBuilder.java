package io.github.gimmi;

import java.util.ArrayList;

public class AnyListBuilder {
	private final ArrayList<Any> list = new ArrayList<>();
	
	public AnyListBuilder put(Any value) {
		if (value == null) {
			value = Any.NULL;
		}
		list.add(value);
		return this;
	}
	
	public Any build() {
		if (list.isEmpty()) {
			return Any.NULL;
		}
		return new Any(null, null, new ArrayList<>(list));
	}
}
