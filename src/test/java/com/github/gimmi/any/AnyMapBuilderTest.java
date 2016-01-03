package com.github.gimmi.any;

import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class AnyMapBuilderTest {
   @Test
   public void should_keep_track_of_length() {
      AnyMapBuilder b = new AnyMapBuilder();
      assertThat(b.count()).isZero();
      assertThat(b.build().count()).isZero();
      b.append("k1", Any.NULL);
      assertThat(b.count()).isZero();
      assertThat(b.build().count()).isZero();
      b.append("k1", Any.of("val1"));
      assertThat(b.count()).isEqualTo(1);
      assertThat(b.build().count()).isEqualTo(1);
      b.append("K1", Any.of("val2"));
      assertThat(b.count()).isEqualTo(1);
      assertThat(b.build().count()).isEqualTo(1);
      b.append("k2", Any.of("val3"));
      assertThat(b.count()).isEqualTo(2);
      assertThat(b.build().count()).isEqualTo(2);
   }
}
