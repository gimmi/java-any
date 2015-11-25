package any;

import io.github.gimmi.Any;
import io.github.gimmi.AnyMapBuilder;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class AnyMapBuilderTest {
	@Test
	public void should_keep_track_of_length() {
		AnyMapBuilder b = new AnyMapBuilder();
		assertThat(b.length()).isZero();
		assertThat(b.build().size()).isZero();
		b.put("k1", Any.NULL);
		assertThat(b.length()).isZero();
		assertThat(b.build().size()).isZero();
		b.put("k1", Any.scalar("val1"));
		assertThat(b.length()).isEqualTo(1);
		assertThat(b.build().size()).isEqualTo(1);
		b.put("K1", Any.scalar("val2"));
		assertThat(b.length()).isEqualTo(1);
		assertThat(b.build().size()).isEqualTo(1);
		b.put("k2", Any.scalar("val3"));
		assertThat(b.length()).isEqualTo(2);
		assertThat(b.build().size()).isEqualTo(2);
	}
}
