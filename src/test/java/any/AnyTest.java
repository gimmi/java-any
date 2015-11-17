package any;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.Test;

import io.github.gimmi.Any;
import static org.junit.Assert.*;

public class AnyTest {
	@Test
	public void should_handle_string_scalar() {
		assertThat(Any.scalar("hello world").toString()).isEqualTo("hello world");
		assertThat(Any.scalar((String)null).toString()).isEqualTo("");
		assertThat(Any.scalar("  ").toString()).isEqualTo("");
		assertThat(Any.scalar(" x ").toString()).isEqualTo("x");
		assertThat(Any.scalar("\n\trow1\n\trow2\n").toString()).isEqualTo("row1\n\trow2");
	}
	
	@Test
	public void should_handle_number_scalar() {
		assertThat(Any.scalar(BigDecimal.valueOf(0)).toBigDecimal()).isEqualTo(BigDecimal.valueOf(0));
		assertThat(Any.map(x -> x.put("key", Any.scalar("val"))).toBigDecimal()).isEqualTo(BigDecimal.ZERO);
		assertThat(Any.list(x -> x.put(Any.scalar("3.14"))).toBigDecimal()).isEqualTo(BigDecimal.ZERO);
		assertThat(Any.scalar(BigDecimal.valueOf(314, 2)).toBigDecimal()).isEqualTo(BigDecimal.valueOf(314, 2));
		assertThat(Any.scalar((BigDecimal)null).toBigDecimal()).isEqualTo(BigDecimal.ZERO);
		assertThat(Any.scalar("   ").toBigDecimal()).isEqualTo(BigDecimal.ZERO);
	}
	
	@Test
	public void should_handle_boolean_scalar() {
		assertThat(Any.scalar(Boolean.valueOf(true)).toBoolean()).isTrue();
		assertThat(Any.scalar(true).toBoolean()).isTrue();
		assertThat(Any.scalar(Boolean.valueOf(false)).toBoolean()).isFalse();
		assertThat(Any.scalar(false).toBoolean()).isFalse();
		assertThat(Any.scalar((Boolean)null).toBoolean()).isFalse();
		assertThat(Any.scalar("   ").toBoolean()).isFalse();
		assertThat(Any.scalar(" true ").toBoolean()).isTrue();
		assertThat(Any.scalar(" FALSE ").toBoolean()).isFalse();
		assertThat(Any.scalar("").toBoolean()).isFalse();
	}
	
	@Test
	public void should_handle_empty_lists() {
		Any list = Any.list(l -> {});
		assertThat(list.cardinality()).isZero();
		assertThat(list.values()).isEmpty();
		assertThat(list.keys()).isEmpty();
		assertThat(list.toString()).isEmpty();
		assertThat(list.toBoolean()).isFalse();
		assertThat(list.toBigDecimal()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(list.get(-1).toString()).isEmpty();
		assertThat(list.get(0).toString()).isEmpty();
		assertThat(list.get(1).toString()).isEmpty();
		assertThat(list.get(2).toString()).isEmpty();
		assertThat(list.get(100).toString()).isEmpty();
	}
	
	@Test
	public void should_handle_lists_containing_values() {
		Any list = Any.list(l -> l
			.put(Any.scalar("1"))
			.put(Any.scalar("2"))
		);
		
		assertThat(list.cardinality()).isEqualTo(2);
		assertThat(list.values()).extracting(x -> x.toString()).containsExactly("1", "2");
		assertThat(list.keys()).isEmpty();
		assertThat(list.toString()).isEmpty();
		assertThat(list.toBoolean()).isTrue();
		assertThat(list.toBigDecimal()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(list.get(-1).toString()).isEmpty();
		assertThat(list.get(0).toString()).isEqualTo("1");
		assertThat(list.get(1).toString()).isEqualTo("2");
		assertThat(list.get(2).toString()).isEmpty();
		assertThat(list.get(100).toString()).isEmpty();
	}
	
	@Test
	public void should_handle_maps() {
		assertThat(Any.map(x -> {}).cardinality()).isEqualTo(0);
		
		assertThat(Any.map(x -> {
			x.put("k1", Any.scalar("1"));
			x.put("k2", Any.scalar("2"));
		}).toString()).isEmpty();

		assertThat(Any.map(x -> x
			.put("k1", Any.scalar("1"))
			.put("k2", Any.scalar("2"))
		).cardinality()).isEqualTo(2);
	}
	
	@Test
	public void empty_array_should_behave_like_null_value() {
		fail();
	}
}
