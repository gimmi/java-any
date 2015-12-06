package com.github.gimmi;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AnyTest {
	@Test
	public void should_handle_string_scalar() {
		assertThat(Any.from("hello world").toString()).isEqualTo("hello world");
		assertThat(Any.from((String) null).toString()).isEqualTo("");
		assertThat(Any.from("  ").toString()).isEqualTo("");
		assertThat(Any.from(" x ").toString()).isEqualTo("x");
		assertThat(Any.from("\n\trow1\n\trow2\n").toString()).isEqualTo("row1\n\trow2");
	}

	@Test
	public void should_handle_number_scalar() {
		assertThat(Any.from(BigDecimal.valueOf(0)).val(BigDecimal.ZERO)).isEqualTo(BigDecimal.valueOf(0));
		Any any1 = Any.map(x -> x.put("key", Any.from("3.14")));
		assertThat(any1.val(BigDecimal.ZERO)).isEqualTo(new BigDecimal("3.14"));
		Any any = Any.list(x -> x.put(Any.from("3.14")));
		assertThat(any.val(BigDecimal.ZERO)).isEqualTo(new BigDecimal("3.14"));
		assertThat(Any.from(BigDecimal.valueOf(314, 2)).val(BigDecimal.ZERO)).isEqualTo(BigDecimal.valueOf(314, 2));
		assertThat(Any.from((BigDecimal) null).val(BigDecimal.ZERO)).isEqualTo(BigDecimal.ZERO);
		assertThat(Any.from("   ").val(BigDecimal.ZERO)).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	public void should_handle_boolean_scalar() {
		assertThat(Any.from(true).val(false)).isTrue();
		assertThat(Any.from(false).val(false)).isFalse();
		assertThat(Any.from((Boolean)null).val(false)).isFalse();
		assertThat(Any.from("   ").val(false)).isFalse();
		assertThat(Any.from(" true ").val(false)).isTrue();
		assertThat(Any.from(" FALSE ").val(false)).isFalse();
		assertThat(Any.from("").val(false)).isFalse();
	}

	@Test
	public void should_handle_empty_lists_like_null_objects() {
		Any list = Any.list(l -> {});
		Any nil = Any.NULL;
		assertThat(list.count()).isEqualTo(nil.count());
		assertThat(list.values()).containsExactlyElementsOf(nil.values());
		assertThat(list.keys()).containsExactlyElementsOf(nil.keys());
		assertThat(list.toString()).isEqualTo(nil.toString());
		assertThat(list.val(false)).isEqualTo(nil.val(false));
		assertThat(list.val(BigDecimal.ZERO)).isEqualTo(nil.val(BigDecimal.ZERO));
		assertThat(list.key("x").toString()).isEqualTo(nil.key("x").toString());
		assertThat(list.key((String) null).toString()).isEqualTo(nil.key((String) null).toString());
		assertThat(list.key("  ").toString()).isEqualTo(nil.key("  ").toString());
		assertThat(list.at(0).toString()).isEqualTo(nil.at(0).toString());
	}

	@Test
	public void should_handle_lists_containing_values() {
		Any list = Any.list(l -> {
			l.put(Any.from("1"));
			l.put(Any.from("2"));
		});

		assertThat(list.count()).isEqualTo(2);
		assertThat(list.values()).extracting(Any::toString).containsExactly("1", "2");
		assertThat(list.keys()).isEmpty();
		assertThat(list.toString()).isEqualTo("1");
		assertThat(list.val(false)).isFalse();
		assertThat(list.val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("1"));
		assertThat(list.at(-1).toString()).isEmpty();
		assertThat(list.at(0).toString()).isEqualTo("1");
		assertThat(list.at(1).toString()).isEqualTo("2");
		assertThat(list.at(2).toString()).isEmpty();
		assertThat(list.at(100).toString()).isEmpty();
		assertThat(list.key("x").toString()).isEmpty();
	}

	@Test
	public void should_handle_empty_maps_like_null_objects() {
		Any map = Any.map(x -> {});
		Any nil = Any.NULL;
		assertThat(map.count()).isEqualTo(nil.count());
		assertThat(map.values()).containsExactlyElementsOf(nil.values());
		assertThat(map.keys()).containsExactlyElementsOf(nil.keys());
		assertThat(map.toString()).isEqualTo(nil.toString());
		assertThat(map.val(false)).isEqualTo(nil.val(false));
		assertThat(map.val(BigDecimal.ZERO)).isEqualTo(nil.val(BigDecimal.ZERO));
		assertThat(map.key("x").toString()).isEqualTo(nil.key("x").toString());
		assertThat(map.key((String) null).toString()).isEqualTo(nil.key((String) null).toString());
		assertThat(map.key("  ").toString()).isEqualTo(nil.key("  ").toString());
		assertThat(map.at(0).toString()).isEqualTo(nil.at(0).toString());
	}

	@Test
	public void should_handle_map_containing_values() {
		Any map = Any.map(x -> {
			x.put("k1", Any.from("1"));
			x.put("CAPITAL", Any.from("2"));
			x.put(" k3 ", Any.from("3"));
		});
		assertThat(map.count()).isEqualTo(3);
		assertThat(map.values()).extracting(Any::toString).containsExactly("2", "1", "3");
		assertThat(map.keys()).containsExactly("CAPITAL", "k1", "k3");
		assertThat(map.toString()).isEqualTo("2");
		assertThat(map.val(false)).isFalse();
		assertThat(map.val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("2"));
		assertThat(map.key("x").toString()).isEmpty();
		assertThat(map.key((String) null).toString()).isEmpty();
		assertThat(map.key("  ").toString()).isEmpty();
		assertThat(map.at(0).toString()).isEmpty();
		assertThat(map.key("  k1  ").toString()).isEqualTo("1");
		assertThat(map.key("  capital  ").toString()).isEqualTo("2");
	}

	@Test
	public void should_collect_all_values_for_the_same_key() {
		Any any = Any.map(b -> {
			b.put("k", Any.NULL);
			b.put("k", Any.from("v1"));
			b.put("k", Any.NULL);
			b.put("k", Any.from("v2"));
			b.put("k", Any.NULL);
		});

		assertThat(any.count()).isEqualTo(1);
		assertThat(any.key("k").count()).isEqualTo(4);
		assertThat(any.key("k").at(0).count()).isZero();
		assertThat(any.key("k").at(1).toString()).isEqualTo("v1");
		assertThat(any.key("k").at(2).count()).isZero();
		assertThat(any.key("k").at(3).toString()).isEqualTo("v2");
	}

	@Test
	public void should_skip_trailing_nulls_from_list() {
		Any any = Any.list(b -> {
			b.put(Any.NULL);
			b.put(Any.NULL);
			b.put(Any.from("val"));
			b.put(Any.NULL);
			b.put(Any.NULL);
		});

		assertThat(any.count()).isEqualTo(3);
		assertThat(any.at(0).count()).isZero();
		assertThat(any.at(1).count()).isZero();
		assertThat(any.at(2).toString()).isEqualTo("val");
	}

	@Test
	public void should_handle_localdate_scalar() {
		assertThat(Any.from(LocalDate.of(2015, 12, 2)).count()).isEqualTo(1);
		assertThat(Any.from(LocalDate.of(2015, 12, 2)).val(LocalDate.MIN)).isEqualTo(LocalDate.of(2015, 12, 2));
		assertThat(Any.from((LocalDate) null).count()).isZero();
		assertThat(Any.from((LocalDate) null).val(LocalDate.MIN)).isEqualTo(LocalDate.MIN);
		assertThat(Any.from("2015-12-02").val(LocalDate.MIN)).isEqualTo(LocalDate.of(2015, 12, 2));
	}

	@Test
	public void should_handle_localtime_scalar() {
		assertThat(Any.from(LocalTime.of(19, 30, 40)).count()).isEqualTo(1);
		assertThat(Any.from(LocalTime.of(19, 30, 40)).val(LocalTime.MIN)).isEqualTo(LocalTime.of(19, 30, 40));
		assertThat(Any.from((LocalTime) null).count()).isZero();
		assertThat(Any.from((LocalTime) null).val(LocalTime.MIN)).isEqualTo(LocalTime.MIN);
		assertThat(Any.from("19:30:40").val(LocalTime.MIN)).isEqualTo(LocalTime.of(19, 30, 40));
	}

	@Test
	public void should_handle_localdatetime_scalar() {
		assertThat(Any.from(LocalDateTime.of(2015, 12, 2, 19, 30, 40)).count()).isEqualTo(1);
		assertThat(Any.from(LocalDateTime.of(2015, 12, 2, 19, 30, 40)).val(LocalDateTime.MIN)).isEqualTo(LocalDateTime.of(2015, 12, 2, 19, 30, 40));
		assertThat(Any.from((LocalDateTime) null).count()).isZero();
		assertThat(Any.from((LocalDateTime) null).val(LocalDateTime.MIN)).isEqualTo(LocalDateTime.MIN);
		assertThat(Any.from("2015-12-02T19:30:40").val(LocalDateTime.MIN)).isEqualTo(LocalDateTime.of(2015, 12, 2, 19, 30, 40));
	}

	@Test
	public void should_treat_scalar_like_single_value_list() {
		Any scalar = Any.from("hello");
		Any list = Any.list(b -> b.put(scalar));
		assertThat(scalar.count()).isEqualTo(list.count());
		assertThat(scalar.keys()).containsExactlyElementsOf(list.keys());
		assertThat(scalar.values()).containsExactlyElementsOf(list.values());
		assertThat(scalar.at(0)).isSameAs(list.at(0));
		assertThat(scalar.at(1)).isSameAs(list.at(1));
		assertThat(scalar.key("0")).isSameAs(list.key("0"));
		assertThat(scalar.key("other")).isSameAs(list.key("other"));
	}
}
