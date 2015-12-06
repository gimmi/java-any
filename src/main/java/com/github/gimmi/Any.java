package com.github.gimmi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.gimmi.Utils.stripToNull;

public class Any {
	public static final Any NULL = new Any(null, null, null);

	public static Any from(BigDecimal value) {
		if (value == null) {
			return Any.NULL;
		}
		return new Any(value.toPlainString(), null, null);
	}

	public static Any from(Boolean value) {
		if (value == null) {
			return Any.NULL;
		}
		return new Any(value.toString(), null, null);
	}

	public static Any from(LocalDate value) {
		if (value == null) {
			return Any.NULL;
		}
		return new Any(value.toString(), null, null);
	}

	public static Any from(LocalTime value) {
		if (value == null) {
			return Any.NULL;
		}
		return new Any(value.toString(), null, null);
	}

	public static Any from(LocalDateTime value) {
		if (value == null) {
			return Any.NULL;
		}
		return new Any(value.toString(), null, null);
	}

	public static Any from(String value) {
		value = stripToNull(value);
		if (value == null) {
			return Any.NULL;
		}
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

	public int count() {
		if (map != null) {
			return map.size();
		} else if (list != null) {
			return list.size();
		} else if (scalar != null) {
			return 1;
		}
		return 0;
	}

	public Any key(String key) {
		key = stripToNull(key);
		if (key == null) {
			return Any.NULL;
		} else if (map != null) {
			Any val = map.get(key);
			if (val != null) {
				return val;
			}
		}
		return Any.NULL;
	}

	public Any at(Integer index) {
		if (index == null) {
			return Any.NULL;
		} else if (list != null) {
			if (index >= 0 && index < list.size()) {
				return list.get(index);
			}
		}
		return Any.NULL;
	}

	@Override
	public String toString() {
		return val("");
	}

	public Optional<String> val() {
		return Optional.ofNullable(getScalar(null));
	}

	public String val(String def) {
		return getScalar(def);
	}

	public BigDecimal val(BigDecimal def) {
		String v = getScalar(null);
		if (v == null) {
			return def;
		}
		return new BigDecimal(v);
	}

	public Integer val(Integer def) {
		String v = getScalar(null);
		if (v == null) {
			return def;
		}
		return Integer.parseInt(v);
	}

	public Boolean val(Boolean def) {
		String v = getScalar(null);
		if (v == null) {
			return def;
		}
		return Boolean.parseBoolean(v);
	}

	public LocalDate val(LocalDate def) {
		String v = getScalar(null);
		if (v == null) {
			return def;
		}
		return LocalDate.parse(v);
	}

	public LocalTime val(LocalTime def) {
		String v = getScalar(null);
		if (v == null) {
			return def;
		}
		return LocalTime.parse(v);
	}

	public LocalDateTime val(LocalDateTime def) {
		String v = getScalar(null);
		if (v == null) {
			return def;
		}
		return LocalDateTime.parse(v);
	}

	public <T> T val(Function<String, T> converter) {
		return converter.apply(getScalar(null));
	}

	public Iterable<String> keys() {
		if (map != null) {
			return map.keySet();
		}
		return Collections.emptySet();
	}

	public Iterable<Any> values() {
		if (map != null) {
			return map.values();
		} else if (list != null) {
			return list;
		} else if (scalar != null) {
			return Collections.singletonList(this);
		}
		return Collections.emptyList();
	}

	private String getScalar(String def) {
		if (map != null && map.size() > 0) {
			return map.firstEntry().getValue().scalar;
		} else if (list != null && list.size() > 0) {
			return list.get(0).scalar;
		} else if (scalar != null) {
			return scalar;
		}
		return def;
	}
}
