package com.github.gimmi.any;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class AnyTest {
   @Test
   public void should_handle_string_scalar() {
      assertThat(Any.of("hello world").or("")).isEqualTo("hello world");
      assertThat(Any.of((String) null).or("")).isEqualTo("");
      assertThat(Any.of("  ").or("")).isEqualTo("");
      assertThat(Any.of(" x ").or("")).isEqualTo("x");
      assertThat(Any.of("\n\trow1\n\trow2\n").or("")).isEqualTo("row1\n\trow2");
   }

   @Test
   public void should_handle_bigdecimal_scalar() {
      assertThat(Any.of(BigDecimal.valueOf(0)).or(BigDecimal.ZERO)).isEqualTo(BigDecimal.valueOf(0));
      Any any1 = Any.map(x -> x.append("key", Any.of("3.14")));
      assertThat(any1.or(BigDecimal.ZERO)).isEqualTo(BigDecimal.ZERO);
      Any any = Any.list(x -> x.append(Any.of("3.14")));
      assertThat(any.or(BigDecimal.ZERO)).isEqualTo(new BigDecimal("3.14"));
      assertThat(Any.of(BigDecimal.valueOf(314, 2)).or(BigDecimal.ZERO)).isEqualTo(BigDecimal.valueOf(314, 2));
      assertThat(Any.of((BigDecimal) null).or(BigDecimal.ZERO)).isEqualTo(BigDecimal.ZERO);
      assertThat(Any.of("   ").or(BigDecimal.ZERO)).isEqualTo(BigDecimal.ZERO);
   }

   @Test
   public void should_handle_integer_scalar() {
      assertThat(Any.of(0).or(-1)).isZero();
      Any map = Any.map(x -> x.append("key", Any.of("314")));
      assertThat(map.or(-1)).isEqualTo(-1);
      Any list = Any.list(x -> x.append(Any.of("314")));
      assertThat(list.or(0)).isEqualTo(314);
      assertThat(Any.of((Integer) null).or(-1)).isEqualTo(-1);
      assertThat(Any.of("   ").or(-1)).isEqualTo(-1);
      assertThat(Any.of(-314).or(0)).isEqualTo(-314);
   }

   @Test
   public void should_handle_boolean_scalar() {
      assertThat(Any.of(true).or(false)).isTrue();
      assertThat(Any.of(false).or(false)).isFalse();
      assertThat(Any.of((Boolean) null).or(false)).isFalse();
      assertThat(Any.of("   ").or(false)).isFalse();
      assertThat(Any.of(" true ").or(false)).isTrue();
      assertThat(Any.of(" FALSE ").or(false)).isFalse();
      assertThat(Any.of("").or(false)).isFalse();
   }

   @Test
   public void should_return_optional_when_no_default_specified() {
      assertThat(Any.of("abc").opt().isPresent()).isTrue();
      assertThat(Any.of("abc").opt().get()).isEqualTo("abc");
      assertThat(Any.of((String) null).opt().isPresent()).isFalse();
      assertThat(Any.of("  ").opt().isPresent()).isFalse();
   }

   @Test
   public void should_handle_empty_lists_like_null_objects() {
      Any list = Any.list(l -> {});
      Any nil = Any.NULL;
      assertThat(list.count()).isEqualTo(nil.count());
      assertThat(list.values()).containsExactlyElementsOf(nil.values());
      assertThat(list.keys()).containsExactlyElementsOf(nil.keys());
      assertThat(list.or("")).isEqualTo(nil.or(""));
      assertThat(list.or(false)).isEqualTo(nil.or(false));
      assertThat(list.or(BigDecimal.ZERO)).isEqualTo(nil.or(BigDecimal.ZERO));
      assertThat(list.key("x").or("")).isEqualTo(nil.key("x").or(""));
      assertThat(list.key(null).or("")).isEqualTo(nil.key(null).or(""));
      assertThat(list.key("  ").or("")).isEqualTo(nil.key("  ").or(""));
      assertThat(list.at(0).or("")).isEqualTo(nil.at(0).or(""));
   }

   @Test
   public void should_handle_lists_containing_values() {
      Any list = Any.list(l -> {
         l.append(Any.of("1"));
         l.append(Any.of("2"));
      });

      assertThat(list.count()).isEqualTo(2);
      assertThat(list.values()).extracting(x -> x.or("")).containsExactly("1", "2");
      assertThat(list.keys()).isEmpty();
      assertThat(list.or("")).isEqualTo("1");
      assertThat(list.or(false)).isFalse();
      assertThat(list.or(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("1"));
      Any any = list.at(-1);
      assertThat(any.or("")).isEmpty();
      assertThat(list.at(0).or("")).isEqualTo("1");
      assertThat(list.at(1).or("")).isEqualTo("2");
      assertThat(list.at(2).or("")).isEmpty();
      assertThat(list.at(100).or("")).isEmpty();
      assertThat(list.key("x").or("")).isEmpty();
   }

   @Test
   public void should_handle_empty_maps_like_null_objects() {
      Any map = Any.map(x -> {});
      Any nil = Any.NULL;
      assertThat(map.count()).isEqualTo(nil.count());
      assertThat(map.values()).containsExactlyElementsOf(nil.values());
      assertThat(map.keys()).containsExactlyElementsOf(nil.keys());
      assertThat(map.or("")).isEqualTo(nil.or(""));
      assertThat(map.or(false)).isEqualTo(nil.or(false));
      assertThat(map.or(BigDecimal.ZERO)).isEqualTo(nil.or(BigDecimal.ZERO));
      assertThat(map.key("x").or("")).isEqualTo(nil.key("x").or(""));
      assertThat(map.key(null).or("")).isEqualTo(nil.key(null).or(""));
      assertThat(map.key("  ").or("")).isEqualTo(nil.key("  ").or(""));
      assertThat(map.at(0).or("")).isEqualTo(nil.at(0).or(""));
   }

   @Test
   public void should_handle_map_containing_values() {
      Any map = Any.map(x -> {
         x.append("k1", Any.of("1"));
         x.append("CAPITAL", Any.of("2"));
         x.append(" k3 ", Any.of("3"));
      });
      assertThat(map.count()).isEqualTo(3);
      assertThat(map.values()).extracting(x -> x.or("")).containsExactly("2", "1", "3");
      assertThat(map.keys()).containsExactly("CAPITAL", "k1", "k3");
      assertThat(map.or("")).isEmpty();
      assertThat(map.or(false)).isFalse();
      assertThat(map.or(BigDecimal.ZERO)).isEqualByComparingTo(BigDecimal.ZERO);
      assertThat(map.key("x").or("")).isEmpty();
      assertThat(map.key(null).or("")).isEmpty();
      assertThat(map.key("  ").or("")).isEmpty();
      assertThat(map.at(0).or("")).isEmpty();
      assertThat(map.key("  k1  ").or("")).isEqualTo("1");
      assertThat(map.key("  capital  ").or("")).isEqualTo("2");
   }

   @Test
   public void should_collect_all_values_for_the_same_key() {
      Any any = Any.map(b -> {
         b.append("k", Any.NULL);
         b.append("k", Any.of("v1"));
         b.append("k", Any.NULL);
         b.append("k", Any.of("v2"));
         b.append("k", Any.NULL);
      });

      assertThat(any.count()).isEqualTo(1);
      assertThat(any.key("k").count()).isEqualTo(4);
      assertThat(any.key("k").at(0).count()).isZero();
      assertThat(any.key("k").at(1).or("")).isEqualTo("v1");
      assertThat(any.key("k").at(2).count()).isZero();
      assertThat(any.key("k").at(3).or("")).isEqualTo("v2");
   }

   @Test
   public void should_skip_trailing_nulls_from_list() {
      Any any = Any.list(b -> {
         b.append(Any.NULL);
         b.append(Any.NULL);
         b.append(Any.of("val"));
         b.append(Any.NULL);
         b.append(Any.NULL);
      });

      assertThat(any.count()).isEqualTo(3);
      assertThat(any.at(0).count()).isZero();
      assertThat(any.at(1).count()).isZero();
      assertThat(any.at(2).or("")).isEqualTo("val");
   }

   @Test
   public void should_handle_localdate_scalar() {
      assertThat(Any.of(LocalDate.of(2015, 12, 2)).count()).isEqualTo(1);
      assertThat(Any.of(LocalDate.of(2015, 12, 2)).or(LocalDate.MIN)).isEqualTo(LocalDate.of(2015, 12, 2));
      assertThat(Any.of((LocalDate) null).count()).isZero();
      assertThat(Any.of((LocalDate) null).or(LocalDate.MIN)).isEqualTo(LocalDate.MIN);
      assertThat(Any.of("2015-12-02").or(LocalDate.MIN)).isEqualTo(LocalDate.of(2015, 12, 2));
   }

   @Test
   public void should_handle_localtime_scalar() {
      assertThat(Any.of(LocalTime.of(19, 30, 40)).count()).isEqualTo(1);
      assertThat(Any.of(LocalTime.of(19, 30, 40)).or(LocalTime.MIN)).isEqualTo(LocalTime.of(19, 30, 40));
      assertThat(Any.of((LocalTime) null).count()).isZero();
      assertThat(Any.of((LocalTime) null).or(LocalTime.MIN)).isEqualTo(LocalTime.MIN);
      assertThat(Any.of("19:30:40").or(LocalTime.MIN)).isEqualTo(LocalTime.of(19, 30, 40));
   }

   @Test
   public void should_handle_localdatetime_scalar() {
      assertThat(Any.of(LocalDateTime.of(2015, 12, 2, 19, 30, 40)).count()).isEqualTo(1);
      assertThat(Any.of(LocalDateTime.of(2015, 12, 2, 19, 30, 40)).or(LocalDateTime.MIN)).isEqualTo(LocalDateTime.of(2015, 12, 2, 19, 30, 40));
      assertThat(Any.of((LocalDateTime) null).count()).isZero();
      assertThat(Any.of((LocalDateTime) null).or(LocalDateTime.MIN)).isEqualTo(LocalDateTime.MIN);
      assertThat(Any.of("2015-12-02T19:30:40").or(LocalDateTime.MIN)).isEqualTo(LocalDateTime.of(2015, 12, 2, 19, 30, 40));
   }

   @Test
   public void should_treat_scalar_like_single_value_list() {
      Any scalar = Any.of("hello");
      Any list = Any.list(b -> b.append(scalar));

      assertThat(scalar.count()).isEqualTo(1);
      assertThat(list.count()).isEqualTo(1);

      assertThat(scalar.keys()).isEmpty();
      assertThat(list.keys()).isEmpty();

      assertThat(scalar.valueStream().map(x -> x.or("")).toArray()).containsExactly("hello");
      assertThat(list.valueStream().map(x -> x.or("")).toArray()).containsExactly("hello");

      assertThat(scalar.at(0).or("")).isEqualTo("hello");
      assertThat(list.at(0).or("")).isEqualTo("hello");

      assertThat(scalar.at(1).or("")).isEmpty();
      assertThat(list.at(1).or("")).isEmpty();

      assertThat(scalar.key("0").or("")).isEmpty();
      assertThat(list.key("0").or("")).isEmpty();

      assertThat(scalar.key("other").or("")).isEmpty();
      assertThat(list.key("other").or("")).isEmpty();
   }

   @Test
   public void should_iterate_using_stream() {
      Any map = Any.map(b -> {
         b.append("a", Any.of(1));
         b.append("b", Any.of(2));
         b.append("c", Any.of(3));
      });

      assertThat(map.keyStream().collect(toList())).containsExactly("a", "b", "c");
      assertThat(map.valueStream().map(x -> x.or(0)).collect(toList())).containsExactly(1, 2, 3);

      Any list = Any.list(b -> {
         b.append(Any.of(1));
         b.append(Any.of(2));
         b.append(Any.of(3));
      });

      assertThat(list.keyStream().collect(toList())).isEmpty();
      assertThat(list.valueStream().map(x -> x.or(0)).collect(toList())).containsExactly(1, 2, 3);

      Any scalar = Any.of(1);

      assertThat(scalar.keyStream().collect(toList())).isEmpty();
      assertThat(scalar.valueStream().map(x -> x.or(0)).collect(toList())).containsExactly(1);
   }

   @Test
   public void should_return_string_debug_representation() {
      assertThat(Any.NULL.toString()).isEqualTo("");
      assertThat(Any.of("abc").toString()).isEqualTo("abc");
      assertThat(Any.of(123).toString()).isEqualTo("123");
      assertThat(Any.map(b -> {
         b.append("a", Any.of("b"));
         b.append("c", Any.of("d"));
      }).toString()).isEqualTo("{a=b, c=d}");
      assertThat(Any.list(b -> {
         b.append(Any.of(""));
         b.append(Any.of("b"));
         b.append(Any.of("d"));
      }).toString()).isEqualTo("[, b, d]");
   }
}
