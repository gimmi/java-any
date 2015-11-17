package io.github.gimmi;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;
import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.commons.lang3.BooleanUtils;

public class Any {
	public static Any NULL = new Any(null, null, null);
	
	public static Any scalar(BigDecimal value) {
		return new Any(value == null ? null : value.toPlainString(), null, null);
	}
	
	public static Any scalar(Boolean value) {
		return new Any(value == null ? null : value.toString(), null, null);
	}
	
	public static Any scalar(String value) {
		return new Any(value, null, null);
	}
	
	public static Any map(Consumer<AnyMapBuilder> builder) {
		AnyMapBuilder b = new AnyMapBuilder();
		builder.accept(b);
		return b.build();
	}

	public static Any list(Consumer<AnyListBuilder> builder) {
		AnyListBuilder b = new AnyListBuilder();
		builder.accept(b);
		return b.build();
	}
	
	private final String scalar;
	private final TreeMap<String, Any> map;
	private final ArrayList<Any> list;
	
	protected Any(String scalar, TreeMap<String, Any> map, ArrayList<Any> list) {
		this.scalar = scalar;
		this.list = list;
		this.map = map;
	}
	
	public int cardinality() {
		if (map != null) {
			return map.size();
		} else if (list != null) {
			return list.size();
		} else if (isNotBlank(scalar)) {
			return 1;
		}
		return 0;
	}
	
	public Any get(String key) {
		key = stripToNull(key);
		if (map != null && key != null) {
			Any val = map.get(key);
			if (val != null) {
				return val;
			}
		}
		return new Any(null, null, null);
	}
	
	public Any get(Integer index) {
		if (list != null && index != null && index >= 0 && index < list.size()) {
			return list.get(index);
		}
		return new Any(null, null, null);
	}
	
	@Override
	public String toString() {
		if (map != null) {
			return "";
		} else if (list != null) {
			return "";
		}
		return stripToEmpty(scalar);
	}
	
	public BigDecimal toBigDecimal() {
		if (map != null) {
			return BigDecimal.ZERO;
		} else if (list != null) {
			return BigDecimal.ZERO;
		} else if (isBlank(scalar)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(scalar);
	}
	
	public boolean toBoolean() {
		if (map != null) {
			return !map.isEmpty();
		} else if (list != null) {
			return !list.isEmpty();
		}
		return BooleanUtils.toBoolean(stripToEmpty(scalar));
	}
	
	public Set<String> keys() {
		if (map != null) {
			return  map.keySet();
		}
		return Collections.emptySet();
	}

	public Iterable<Any> values() {
		if (map != null) {
			return  map.values();
		} else if (list != null) {
			return list;
		}
		return Collections.emptyList();
	}
}
