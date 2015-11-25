package any;

import io.github.gimmi.Any;
import io.github.gimmi.AnyListBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnyListBuilderTest {
	@Test
	public void should_keep_track_of_length() {
		AnyListBuilder b = new AnyListBuilder();
		assertThat(b.length()).isZero();
		assertThat(b.build().size()).isZero();
		b.put(Any.NULL);
		assertThat(b.length()).isZero();
		assertThat(b.build().size()).isZero();
		b.put(Any.NULL);
		assertThat(b.length()).isZero();
		assertThat(b.build().size()).isZero();
		b.put(Any.scalar("val"));
		assertThat(b.length()).isEqualTo(3);
		assertThat(b.build().size()).isEqualTo(3);
		b.put(Any.NULL);
		assertThat(b.length()).isEqualTo(3);
		assertThat(b.build().size()).isEqualTo(3);
	}
}
