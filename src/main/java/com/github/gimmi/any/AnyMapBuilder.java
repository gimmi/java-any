package com.github.gimmi.any;

import java.util.Map;
import java.util.TreeMap;

import static com.github.gimmi.any.Utils.stripToEmpty;

public class AnyMapBuilder {
   private final TreeMap<String, AnyListBuilder> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

   public AnyMapBuilder append(String key, Any value) {
      key = stripToEmpty(key);
      if (key != null) {
         map.computeIfAbsent(key, k -> new AnyListBuilder()).append(value);
      }
      return this;
   }

   public int count() {
      return map.values().stream()
            .filter(x -> x.count() > 0)
            .mapToInt(x -> 1)
            .sum();
   }

   public Any build() {
      if (map.isEmpty()) {
         return Any.NULL;
      } else {
         TreeMap<String, Any> mapCopy = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
         for (Map.Entry<String, AnyListBuilder> entry : map.entrySet()) {
            if (entry.getValue().count() > 0) {
               mapCopy.put(entry.getKey(), entry.getValue().build());
            }
         }
         return new Any(null, mapCopy, null);
      }
   }
}
