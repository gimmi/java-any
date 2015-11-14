package io.github.gimmi;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.commons.lang3.BooleanUtils;

public class Any {
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
		} else if(isNotBlank(scalar)) {
			return 1;
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
		return new Any(null, null, null);
	}
	
	public Any get(int index) {
		if (index >= 0 && list != null && list.size() > index) {
			return list.get(index);
		}
		return new Any(null, null, null);
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
				String comma = "";
				w.write('{');
				for (Map.Entry<String, Any> entry : map.entrySet()) {
					w.write(comma);
					comma = ",";
					writeJsonString(entry.getKey(), w);
					w.write(':');
					entry.getValue().toJson(w);
				}
				w.write('}');
			} else if (list != null) {
				String comma = "";
				w.write('[');
				for (Any entry : list) {
					w.write(comma);
					comma = ",";
					entry.toJson(w);
				}
				w.write(']');
			} else {
				writeJsonString(scalar, w);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to serialize as JSON", e);
		}
	}
	
	private void writeJsonString(String str, Writer w) throws IOException {
		str = stripToEmpty(str);
		w.write('"');
		for (int i = 0; i < str.length(); i++) {
			// TODO special chars
			w.write(str.charAt(i));
		}
		w.write('"');
	}
}
