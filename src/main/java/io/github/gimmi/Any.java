package io.github.gimmi;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.commons.lang3.BooleanUtils;

public class Any {
	public static Any scalar(BigDecimal value) {
		return new Any(value == null ? null : value.toPlainString());
	}
	
	public static Any scalar(Boolean value) {
		return new Any(value == null ? null : value.toString());
	}
	
	public static Any scalar(String value) {
		return new Any(value);
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
	private final Map<String, Any> map;
	private final List<Any> list;
	
	protected Any(String scalar) {
		this.scalar = scalar;
		this.list = null;
		this.map = null;
	}
	
	protected Any(List<Any> list) {
		this.scalar = null;
		this.list = new ArrayList<>(list);
		this.map = null;
	}

	protected Any(Map<String, Any> map) {
		this.scalar = null;
		this.list = null;
		this.map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		this.map.putAll(map);
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
			Any val = map.get(key);
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
		// TODO handle negative index python style
		return new Any();
	}
	
	@Override
	public String toString() {
		if (map != null) {
			return toJson();
		} else if (list != null) {
			return toJson();
		}
		return stripToEmpty(scalar);
	}
	
	public BigDecimal toBigDecimal() {
		if (map != null) {
			return BigDecimal.ZERO;
		} else if (list != null) {
			if (list.isEmpty()) {
				return BigDecimal.ZERO;
			}
			return list.get(0).toBigDecimal();
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
	
	public String toJson() {
		StringWriter writer = new StringWriter();
		toJson(writer);
		return writer.toString();
	}
	
	public void toJson(Writer w) {
		try {
			if (map != null) {
				w.write('{');
				for (Map.Entry<String, Any> entry : map.entrySet()) {
					String value = entry.getValue().toString();
					if (isNotEmpty(value)) {
						writeJsonString(entry.getKey(), w);
						w.write(':');
						writeJsonString(value, w);
					}
				}
				w.write('}');
			} else if (list != null) {
				w.write('[');
				for (Any entry : list) {
					// TODO should trim empties at the end
					String value = entry.toString();
					writeJsonString(value, w);
				}
				w.write(']');
			} else if (isNotEmpty(scalar)) {
				writeJsonString(scalar, w);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to serialize as JSON", e);
		}
	}
	
	private void writeJsonString(String str, Writer w) throws IOException {
		w.write('"');
		for (int i = 0; i < str.length(); i++) {
			// TODO special chars
			w.write(str.charAt(i));
		}
		w.write('"');
	}
}
