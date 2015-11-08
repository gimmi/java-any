package io.github.gimmi;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Any {
	public static Any scalar(String value) {
		return new Any(value);
	}

	public static Any map(Consumer<AnyMapBuilder> builder) {
		AnyMapBuilder b = new AnyMapBuilder();
		builder.accept(b);
		return b.build();
	}

	public static Any list(Consumer<AnyAryBuilder> builder) {
		AnyAryBuilder b = new AnyAryBuilder();
		builder.accept(b);
		return b.build();
	}
	
	private final String scalar;
	private final Map<String, Any> map;
	private final List<Any> list;
	
	protected Any(String scalar) {
		this.scalar = scalar;
		this.list = null;
		this.map = null;
	}

	protected Any(Map<String, Any> map) {
		this.scalar = null;
		this.list = null;
		this.map = map;
	}
	
	protected Any(List<Any> list) {
		this.scalar = null;
		this.list = list;
		this.map = null;
	}

	protected Any() {
		this.scalar = null;
		this.list = null;
		this.map = null;
	}
	
	public int cardinality() {
		if (scalar != null) {
			return 1;
		} else if (map != null) {
			return map.size();
		} else if (list != null) {
			return list.size();
		}
		return 0;
	}
	
	public Any get(String key) {
		if (map != null) {
			Any val = map.get(key); // TODO case insensitive please
			if (val != null) {
				return val;
			}
		}
		return new Any();
	}
	
	public Any get(int index) {
		if (index >= 0 && list != null && list.size() > index) {
			return list.get(index);
		}
		return new Any();
	}
	
	@Override
	public String toString() {
		if (map != null) {
			return map.toString();
		} else if (list != null) {
			return list.toString();
		}
		return stripToEmpty(scalar);
	}
}
