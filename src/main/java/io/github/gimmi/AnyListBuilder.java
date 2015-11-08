package io.github.gimmi;

import java.util.ArrayList;
import java.util.List;

public class AnyListBuilder {
	private final List<Any> list = new ArrayList<>();
	
	public AnyListBuilder put(Any value) {
		list.add(value);
		return this;
	}
	
	public Any build() {
		return new Any(list);
	}
}
