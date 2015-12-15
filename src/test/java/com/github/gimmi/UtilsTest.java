package com.github.gimmi;

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


	@Test
	public void should_parse_time() {
		assertThat(Utils.parseTime("2015").toString()).isEqualTo("2015-01-01T00:00");
		assertThat(Utils.parseTime("2015-12").toString()).isEqualTo("2015-12-01T00:00");
		assertThat(Utils.parseTime("2015-12-15").toString()).isEqualTo("2015-12-15T00:00");
		assertThat(Utils.parseTime("2015-12-15T20").toString()).isEqualTo("2015-12-15T20:00");
		assertThat(Utils.parseTime("2015-12-15T20:30").toString()).isEqualTo("2015-12-15T20:30");
		assertThat(Utils.parseTime("2015-12-15T20:30").toString()).isEqualTo("2015-12-15T20:30");
		assertThat(Utils.parseTime("2015/12/15 at 20.30").toString()).isEqualTo("2015-12-15T20:30");
	}
}
