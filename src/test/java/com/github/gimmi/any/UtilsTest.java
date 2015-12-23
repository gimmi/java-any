package com.github.gimmi.any;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {
	@Test
	public void should_strip_to_null() {
		assertThat(Utils.stripToNull(null)).isNull();
		assertThat(Utils.stripToNull("")).isNull();
		assertThat(Utils.stripToNull("   ")).isEqualTo(null);
		assertThat(Utils.stripToNull("abc")).isEqualTo("abc");
		assertThat(Utils.stripToNull("  abc")).isEqualTo("abc");
		assertThat(Utils.stripToNull("abc  ")).isEqualTo("abc");
		assertThat(Utils.stripToNull(" abc ")).isEqualTo("abc");
		assertThat(Utils.stripToNull(" ab c ")).isEqualTo("ab c");
	}

	@Test
	public void should_strip_to_empty() {
		assertThat(Utils.stripToEmpty(null)).isEqualTo("");
		assertThat(Utils.stripToEmpty("")).isEqualTo("");
		assertThat(Utils.stripToEmpty("   ")).isEqualTo("");
		assertThat(Utils.stripToEmpty("abc")).isEqualTo("abc");
		assertThat(Utils.stripToEmpty("  abc")).isEqualTo("abc");
		assertThat(Utils.stripToEmpty("abc  ")).isEqualTo("abc");
		assertThat(Utils.stripToEmpty(" abc ")).isEqualTo("abc");
		assertThat(Utils.stripToEmpty(" ab c ")).isEqualTo("ab c");
	}

	@Test
	public void should_convert_to_boolean() {
		assertThat(Utils.toBoolean(null)).isFalse();
		assertThat(Utils.toBoolean("true")).isTrue();
		assertThat(Utils.toBoolean("TRUE")).isTrue();
		assertThat(Utils.toBoolean("tRUe")).isTrue();
		assertThat(Utils.toBoolean("on")).isTrue();
		assertThat(Utils.toBoolean("yes")).isTrue();
		assertThat(Utils.toBoolean("false")).isFalse();
		assertThat(Utils.toBoolean("x gti")).isFalse();
		assertThat(Utils.toBoolean("y")).isTrue();
		assertThat(Utils.toBoolean("n")).isFalse();
		assertThat(Utils.toBoolean("t")).isTrue();
		assertThat(Utils.toBoolean("f")).isFalse();
	}
}
