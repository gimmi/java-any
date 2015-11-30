package any;

import io.github.gimmi.Any;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AnyTest {
	@Test
	public void should_handle_string_scalar() {
		assertThat(Any.scalar("hello world").toString()).isEqualTo("hello world");
		assertThat(Any.scalar((String) null).toString()).isEqualTo("");
		assertThat(Any.scalar("  ").toString()).isEqualTo("");
		assertThat(Any.scalar(" x ").toString()).isEqualTo("x");
		assertThat(Any.scalar("\n\trow1\n\trow2\n").toString()).isEqualTo("row1\n\trow2");
	}

	@Test
	public void should_handle_number_scalar() {
		assertThat(Any.scalar(BigDecimal.valueOf(0)).toBigDecimal()).isEqualTo(BigDecimal.valueOf(0));
		assertThat(Any.map(x -> x.put("key", Any.scalar("3.14"))).toBigDecimal()).isEqualTo(new BigDecimal("3.14"));
		assertThat(Any.list(x -> x.put(Any.scalar("3.14"))).toBigDecimal()).isEqualTo(new BigDecimal("3.14"));
		assertThat(Any.scalar(BigDecimal.valueOf(314, 2)).toBigDecimal()).isEqualTo(BigDecimal.valueOf(314, 2));
		assertThat(Any.scalar((BigDecimal) null).toBigDecimal()).isEqualTo(BigDecimal.ZERO);
		assertThat(Any.scalar("   ").toBigDecimal()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	public void should_handle_boolean_scalar() {
		assertThat(Any.scalar(true).toBoolean()).isTrue();
		assertThat(Any.scalar(false).toBoolean()).isFalse();
		assertThat(Any.scalar((Boolean) null).toBoolean()).isFalse();
		assertThat(Any.scalar("   ").toBoolean()).isFalse();
		assertThat(Any.scalar(" true ").toBoolean()).isTrue();
		assertThat(Any.scalar(" FALSE ").toBoolean()).isFalse();
		assertThat(Any.scalar("").toBoolean()).isFalse();
	}

	@Test
	public void should_handle_empty_lists_like_null_objects() {
		Any list = Any.list(l -> {});
		Any nil = Any.NULL;
		assertThat(list.count()).isEqualTo(nil.count());
		assertThat(list.values()).containsExactlyElementsOf(nil.values());
		assertThat(list.keys()).containsExactlyElementsOf(nil.keys());
		assertThat(list.toString()).isEqualTo(nil.toString());
		assertThat(list.toBoolean()).isEqualTo(nil.toBoolean());
		assertThat(list.toBigDecimal()).isEqualTo(nil.toBigDecimal());
		assertThat(list.get("x").toString()).isEqualTo(nil.get("x").toString());
		assertThat(list.get((String) null).toString()).isEqualTo(nil.get((String) null).toString());
		assertThat(list.get("  ").toString()).isEqualTo(nil.get("  ").toString());
		assertThat(list.get(0).toString()).isEqualTo(nil.get(0).toString());
	}

	@Test
	public void should_handle_lists_containing_values() {
		Any list = Any.list(l -> {
			l.put(Any.scalar("1"));
			l.put(Any.scalar("2"));
		});

		assertThat(list.count()).isEqualTo(2);
		assertThat(list.values()).extracting(Any::toString).containsExactly("1", "2");
		assertThat(list.keys()).isEmpty();
		assertThat(list.toString()).isEqualTo("1");
		assertThat(list.toBoolean()).isFalse();
		assertThat(list.toBigDecimal()).isEqualByComparingTo(new BigDecimal("1"));
		assertThat(list.get(-1).toString()).isEmpty();
		assertThat(list.get(0).toString()).isEqualTo("1");
		assertThat(list.get(1).toString()).isEqualTo("2");
		assertThat(list.get(2).toString()).isEmpty();
		assertThat(list.get(100).toString()).isEmpty();
		assertThat(list.get("x").toString()).isEmpty();
	}

	@Test
	public void should_handle_empty_maps_like_null_objects() {
		Any map = Any.map(x -> {});
		Any nil = Any.NULL;
		assertThat(map.count()).isEqualTo(nil.count());
		assertThat(map.values()).containsExactlyElementsOf(nil.values());
		assertThat(map.keys()).containsExactlyElementsOf(nil.keys());
		assertThat(map.toString()).isEqualTo(nil.toString());
		assertThat(map.toBoolean()).isEqualTo(nil.toBoolean());
		assertThat(map.toBigDecimal()).isEqualTo(nil.toBigDecimal());
		assertThat(map.get("x").toString()).isEqualTo(nil.get("x").toString());
		assertThat(map.get((String) null).toString()).isEqualTo(nil.get((String) null).toString());
		assertThat(map.get("  ").toString()).isEqualTo(nil.get("  ").toString());
		assertThat(map.get(0).toString()).isEqualTo(nil.get(0).toString());
	}

	@Test
	public void should_handle_map_containing_values() {
		Any map = Any.map(x -> {
			x.put("k1", Any.scalar("1"));
			x.put("CAPITAL", Any.scalar("2"));
			x.put(" k3 ", Any.scalar("3"));
		});
		assertThat(map.count()).isEqualTo(3);
		assertThat(map.values()).extracting(Any::toString).containsExactly("2", "1", "3");
		assertThat(map.keys()).containsExactly("CAPITAL", "k1", "k3");
		assertThat(map.toString()).isEqualTo("2");
		assertThat(map.toBoolean()).isFalse();
		assertThat(map.toBigDecimal()).isEqualByComparingTo(new BigDecimal("2"));
		assertThat(map.get("x").toString()).isEmpty();
		assertThat(map.get((String) null).toString()).isEmpty();
		assertThat(map.get("  ").toString()).isEmpty();
		assertThat(map.get(0).toString()).isEmpty();
		assertThat(map.get("  k1  ").toString()).isEqualTo("1");
		assertThat(map.get("  capital  ").toString()).isEqualTo("2");
	}

	@Test
	public void maps_should_act_as_lists() {
		Any map = Any.map(x -> {
			x.put("0", Any.scalar("zero"));
			x.put("2", Any.scalar("two"));
		});

		assertThat(map.get(0).toString()).isEqualTo("zero");
		assertThat(map.get(1).toString()).isEmpty();
		assertThat(map.get(2).toString()).isEqualTo("two");
		assertThat(map.get(3).toString()).isEmpty();
	}

	@Test
	public void lists_should_get_by_string() {
		Any map = Any.list(x -> {
			x.put(Any.scalar("zero"));
			x.put(Any.scalar("one"));
		});

		assertThat(map.get("0").toString()).isEqualTo("zero");
		assertThat(map.get("+001").toString()).isEqualTo("one");
		assertThat(map.get("2").toString()).isEmpty();
		assertThat(map.get("not a number").toString()).isEmpty();
	}

	@Test
	public void should_collect_all_values_for_the_same_key() {
		Any any = Any.map(b -> {
			b.put("k", Any.NULL);
			b.put("k", Any.scalar("v1"));
			b.put("k", Any.NULL);
			b.put("k", Any.scalar("v2"));
			b.put("k", Any.NULL);
		});

		assertThat(any.count()).isEqualTo(1);
		assertThat(any.get("k").count()).isEqualTo(4);
		assertThat(any.get("k").get(0).count()).isZero();
		assertThat(any.get("k").get(1).toString()).isEqualTo("v1");
		assertThat(any.get("k").get(2).count()).isZero();
		assertThat(any.get("k").get(3).toString()).isEqualTo("v2");
	}

	@Test
	public void should_skip_trailing_nulls_from_list() {
		Any any = Any.list(b -> {
			b.put(Any.NULL);
			b.put(Any.NULL);
			b.put(Any.scalar("val"));
			b.put(Any.NULL);
			b.put(Any.NULL);
		});

		assertThat(any.count()).isEqualTo(3);
		assertThat(any.get(0).count()).isZero();
		assertThat(any.get(1).count()).isZero();
		assertThat(any.get(2).toString()).isEqualTo("val");
	}
}
