package com.github.gimmi.any;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.gimmi.any.Utils.stripToNull;

public class Any {
   public static final Any NULL = new Any(null, null, null);

   public static Any of(BigDecimal value) {
      if (value == null) {
         return Any.NULL;
      }
      return new Any(value.toPlainString(), null, null);
   }

   public static Any of(Integer value) {
      if (value == null) {
         return Any.NULL;
      }
      return new Any(value.toString(), null, null);
   }

   public static Any of(Boolean value) {
      if (value == null) {
         return Any.NULL;
      }
      return new Any(value.toString(), null, null);
   }

   public static Any of(LocalDate value) {
      if (value == null) {
         return Any.NULL;
      }
      return new Any(value.toString(), null, null);
   }

   public static Any of(LocalTime value) {
      if (value == null) {
         return Any.NULL;
      }
      return new Any(value.toString(), null, null);
   }

   public static Any of(LocalDateTime value) {
      if (value == null) {
         return Any.NULL;
      }
      return new Any(value.toString(), null, null);
   }

   public static Any of(String value) {
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

   public int keyCount() {
      if (map != null) {
         return map.size();
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
      } else if (scalar != null && index == 0) {
         return this;
      }
      return Any.NULL;
   }

   public Optional<String> opt() {
      return Optional.ofNullable(getScalar());
   }

   public String or(String def) {
      String val = getScalar();
      return val == null ? def : val;
   }

   public BigDecimal or(BigDecimal def) {
      String v = getScalar();
      if (v == null) {
         return def;
      }
      return new BigDecimal(v);
   }

   public Integer or(Integer def) {
      String v = getScalar();
      if (v == null) {
         return def;
      }
      return Integer.parseInt(v);
   }

   public Boolean or(Boolean def) {
      String v = getScalar();
      if (v == null) {
         return def;
      }
      return Boolean.parseBoolean(v);
   }

   public LocalDate or(LocalDate def) {
      String v = getScalar();
      if (v == null) {
         return def;
      }
      return LocalDate.parse(v);
   }

   public LocalTime or(LocalTime def) {
      String v = getScalar();
      if (v == null) {
         return def;
      }
      return LocalTime.parse(v);
   }

   public LocalDateTime or(LocalDateTime def) {
      String v = getScalar();
      if (v == null) {
         return def;
      }
      return LocalDateTime.parse(v);
   }

   public Iterable<String> keys() {
      if (map != null) {
         return map.keySet();
      }
      return Collections.emptySet();
   }

   public Stream<String> keyStream() {
      if (map != null) {
         return map.keySet().stream();
      }
      return Stream.empty();
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

   public Stream<Any> valueStream() {
      if (map != null) {
         return map.values().stream();
      } else if (list != null) {
         return list.stream();
      } else if (scalar != null) {
         return Stream.of(this);
      }
      return Stream.empty();
   }

   @Override
   public String toString() {
      if (map != null) {
         return map.toString();
      } else if (list != null) {
         return list.toString();
      } else if (scalar != null) {
         return scalar;
      }
      return "";
   }

   private String getScalar() {
      if (list != null) {
         return list.get(0).scalar;
      }
      return scalar;
   }
}
