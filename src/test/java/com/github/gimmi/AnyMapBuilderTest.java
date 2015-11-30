package com.github.gimmi;

import com.github.gimmi.Any;
import com.github.gimmi.AnyMapBuilder;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class AnyMapBuilderTest {
	@Test
	public void should_keep_track_of_length() {
		AnyMapBuilder b = new AnyMapBuilder();
		assertThat(b.count()).isZero();
		assertThat(b.build().count()).isZero();
		b.put("k1", Any.NULL);
		assertThat(b.count()).isZero();
		assertThat(b.build().count()).isZero();
		b.put("k1", Any.scalar("val1"));
		assertThat(b.count()).isEqualTo(1);
		assertThat(b.build().count()).isEqualTo(1);
		b.put("K1", Any.scalar("val2"));
		assertThat(b.count()).isEqualTo(1);
		assertThat(b.build().count()).isEqualTo(1);
		b.put("k2", Any.scalar("val3"));
		assertThat(b.count()).isEqualTo(2);
		assertThat(b.build().count()).isEqualTo(2);
	}
}
