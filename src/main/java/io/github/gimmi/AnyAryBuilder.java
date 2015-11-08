package io.github.gimmi;

import java.util.ArrayList;
import java.util.List;

public class AnyAryBuilder {
	private final List<Any> ary = new ArrayList<>();
	
	public AnyAryBuilder put(Any value) {
		ary.add(value);
		return this;
	}
	
	public Any build() {
		return new Any(new ArrayList<>(ary));
	}
}
