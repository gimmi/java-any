package any;

import static org.assertj.core.api.StrictAssertions.assertThat;

import org.junit.Test;

import io.github.gimmi.Any;

public class AnyTest {

	@Test
	public void should_handle_string_scalar() {
		assertThat(Any.scalar("hello world").toString()).isEqualTo("hello world");
		assertThat(Any.scalar(null).toString()).isEqualTo("");
		assertThat(Any.scalar("  ").toString()).isEqualTo("");
		assertThat(Any.scalar(" x ").toString()).isEqualTo("x");
		assertThat(Any.scalar("\n\trow1\n\trow2\n").toString()).isEqualTo("row1\n\trow2");
	}
	
	@Test
	public void should_handle_lists() {
		assertThat(Any.list(x -> {}).cardinality()).isEqualTo(0);

		assertThat(Any.list(l -> l
			.put(Any.scalar("1"))
			.put(Any.scalar("2"))
		).toString()).isEqualTo("[1, 2]");

		assertThat(Any.list(l -> l
			.put(Any.scalar("1"))
			.put(Any.scalar("2"))
		).cardinality()).isEqualTo(2);
	}
	
	@Test
	public void should_handle_maps() {
		assertThat(Any.map(x -> {}).cardinality()).isEqualTo(0);
		
		assertThat(Any.map(x -> x
			.put("k1", Any.scalar("1"))
			.put("k2", Any.scalar("2"))
		).toString()).isEqualTo("{k1=1, k2=2}");

		assertThat(Any.map(x -> x
			.put("k1", Any.scalar("1"))
			.put("k2", Any.scalar("2"))
		).cardinality()).isEqualTo(2);
	}
}
