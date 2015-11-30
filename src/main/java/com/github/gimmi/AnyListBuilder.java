package com.github.gimmi;

import java.util.ArrayList;

public class AnyListBuilder {
	private final ArrayList<Any> list = new ArrayList<>();
	private int lastNonNullIndex = -1;
	
	public AnyListBuilder put(Any value) {
		if (value == null) {
			value = Any.NULL;
		}
		if (value.count() > 0) {
			lastNonNullIndex = list.size();
		}
		list.add(value);
		return this;
	}

	public int count() {
		return lastNonNullIndex + 1;
	}
	
	public Any build() {
		if (lastNonNullIndex < 0) {
			return Any.NULL;
		} else if (lastNonNullIndex == 0) {
			return list.get(0);
		}
		return new Any(null, null, new ArrayList<>(list.subList(0, lastNonNullIndex + 1)));
	}
}
