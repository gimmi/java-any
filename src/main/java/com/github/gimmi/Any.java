package com.github.gimmi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.function.Consumer;

import static com.github.gimmi.Utils.stripToNull;

public class Any {
    public static Any scalar(BigDecimal value) {
        if (value == null) {
            return Any.NULL;
        }
        return new Any(value.toPlainString(), null, null);
    }

    public static Any scalar(Boolean value) {
        if (value == null) {
            return Any.NULL;
        }
        return new Any(value.toString(), null, null);
    }

    public static Any scalar(String value) {
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

    public static Any NULL = new Any(null, null, null);
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

    public Any get(String key) {
        key = stripToNull(key);
        if (key == null) {
            return Any.NULL;
        } else if (map != null) {
            Any val = map.get(key);
            if (val != null) {
                return val;
            }
        } else if (list != null) {
            Integer index;
            try {
                index = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                index = null;
            }
            return get(index);
        }
        return Any.NULL;
    }

    public Any get(Integer index) {
        if (index == null) {
            return Any.NULL;
        } else if (list != null) {
            if (index >= 0 && index < list.size()) {
                return list.get(index);
            }
        } else if (map != null) {
            return get(index.toString());
        }
        return Any.NULL;
    }

    @Override
    public String toString() {
        return getScalar("");
    }

    public BigDecimal toBigDecimal() {
        return new BigDecimal(getScalar("0"));
    }

    public boolean toBoolean() {
        return Utils.toBoolean(getScalar("false"));
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
