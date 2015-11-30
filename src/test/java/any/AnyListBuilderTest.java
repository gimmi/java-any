package any;

import io.github.gimmi.Any;
import io.github.gimmi.AnyListBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnyListBuilderTest {
	@Test
	public void should_keep_track_of_length() {
		AnyListBuilder b = new AnyListBuilder();
		assertThat(b.count()).isZero();
		assertThat(b.build().count()).isZero();
		b.put(Any.NULL);
		assertThat(b.count()).isZero();
		assertThat(b.build().count()).isZero();
		b.put(Any.NULL);
		assertThat(b.count()).isZero();
		assertThat(b.build().count()).isZero();
		b.put(Any.scalar("val"));
		assertThat(b.count()).isEqualTo(3);
		assertThat(b.build().count()).isEqualTo(3);
		b.put(Any.NULL);
		assertThat(b.count()).isEqualTo(3);
		assertThat(b.build().count()).isEqualTo(3);
	}
}
