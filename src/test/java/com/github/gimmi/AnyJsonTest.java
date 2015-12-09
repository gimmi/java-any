package com.github.gimmi;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AnyJsonTest {
	@Test
	public void should_parse_complex_json() {
		String json = String.join("",
			"{",
			"    'strProp': 'val',",
			"    'numProp': -3.14e2,",
			"    'trueProp': true,",
			"    'falseProp': false,",
			"    'nullProp': null,",
			"    'emptyAry': [],",
			"    'emptyObj': {},",
			"    'aryProp': [",
			"        null, ",
			"        [], ",
			"        {},",
			"        'val', ",
			"        -3.14e2, ",
			"        true, ",
			"        false, ",
			"        null, ",
			"        [], ",
			"        {}",
			"    ],",
			"    'objProp': {",
			"        'strProp': 'val',",
			"        'numProp': -3.14e2,",
			"        'trueProp': true,",
			"        'falseProp': false,",
			"        'nullProp': null,",
			"        'emptyAry': [],",
			"        'emptyObj': {}",
			"    }",
			"}"
		).replace('\'', '"');

		Any any = AnyJson.fromJson(json);

		assertThat(any.count()).isEqualTo(6);
		assertThat(any.key("strProp").toString()).isEqualTo("val");
		assertThat(any.key("numProp").val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.key("trueProp").val(false)).isTrue();
		assertThat(any.key("falseProp").val(false)).isFalse();
		assertThat(any.key("aryProp").count()).isEqualTo(7);
		assertThat(any.key("aryProp").at(0).count()).isZero();
		assertThat(any.key("aryProp").at(1).count()).isZero();
		assertThat(any.key("aryProp").at(2).count()).isZero();
		assertThat(any.key("aryProp").at(3).toString()).isEqualTo("val");
		assertThat(any.key("aryProp").at(4).val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.key("aryProp").at(5).val(false)).isTrue();
		assertThat(any.key("aryProp").at(6).val(false)).isFalse();
		assertThat(any.key("objProp").count()).isEqualTo(4);
		assertThat(any.key("objProp").key("strProp").toString()).isEqualTo("val");
		assertThat(any.key("objProp").key("numProp").val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.key("objProp").key("trueProp").val(false)).isTrue();
		assertThat(any.key("objProp").key("falseProp").val(false)).isFalse();
	}

	@Test
	public void should_serialize_complex_obj() {
		String json = AnyJson.toJson(Any.map(b -> {
			b.put("strProp", Any.of("val"));
			b.put("numProp", Any.of(new BigDecimal("-3.14e2")));
			b.put("trueProp", Any.of(true));
			b.put("falseProp", Any.of(false));
			b.put("nullProp", Any.NULL);
			b.put("emptyAry", Any.list(bb -> {}));
			b.put("emptyObj", Any.map(bb -> {}));
			b.put("aryProp", Any.list(bb -> {
				bb.put(Any.NULL);
				bb.put(Any.list(bbb -> {}));
				bb.put(Any.map(bbb -> {}));
				bb.put(Any.of("val"));
				bb.put(Any.of(new BigDecimal("-3.14e2")));
				bb.put(Any.of(true));
				bb.put(Any.of(false));
				bb.put(Any.NULL);
				bb.put(Any.list(bbb -> {}));
				bb.put(Any.map(bbb -> {}));
			}));
			b.put("objProp", Any.map(bb -> {
				bb.put("strProp", Any.of("val"));
				bb.put("numProp", Any.of(new BigDecimal("-3.14e2")));
				bb.put("trueProp", Any.of(true));
				bb.put("falseProp", Any.of(false));
				bb.put("nullProp", Any.NULL);
				bb.put("emptyAry", Any.list(bbb -> {}));
				bb.put("emptyObj", Any.map(bbb -> {}));
			}));
		})).replace('"', '\'');

		assertThat(json).isEqualTo("{'aryProp':['','','','val','-314','true','false'],'falseProp':'false','numProp':'-314','objProp':{'falseProp':'false','numProp':'-314','strProp':'val','trueProp':'true'},'strProp':'val','trueProp':'true'}");
	}

	@Test
	public void should_serialize_special_chars() {
		assertThat(AnyJson.toJson(Any.of("-\"-"))).isEqualTo("\"-\\\"-\"");
		assertThat(AnyJson.toJson(Any.of("-/-"))).isEqualTo("\"-\\/-\"");
		assertThat(AnyJson.toJson(Any.of("-\b-"))).isEqualTo("\"-\\b-\"");
		assertThat(AnyJson.toJson(Any.of("-\f-"))).isEqualTo("\"-\\f-\"");
		assertThat(AnyJson.toJson(Any.of("-\n-"))).isEqualTo("\"-\\n-\"");
		assertThat(AnyJson.toJson(Any.of("-\t-"))).isEqualTo("\"-\\t-\"");
		assertThat(AnyJson.toJson(Any.of("-\r-"))).isEqualTo("\"-\\r-\"");
	}

	@Test
	public void should_deserialize_special_chars() {
		assertThat(AnyJson.fromJson("\"-\\\"-\"").toString()).isEqualTo("-\"-");
		assertThat(AnyJson.fromJson("\"-\\/-\"").toString()).isEqualTo("-/-");
		assertThat(AnyJson.fromJson("\"-\\b-\"").toString()).isEqualTo("-\b-");
		assertThat(AnyJson.fromJson("\"-\\f-\"").toString()).isEqualTo("-\f-");
		assertThat(AnyJson.fromJson("\"-\\n-\"").toString()).isEqualTo("-\n-");
		assertThat(AnyJson.fromJson("\"-\\t-\"").toString()).isEqualTo("-\t-");
		assertThat(AnyJson.fromJson("\"-\\r-\"").toString()).isEqualTo("-\r-");
		assertThat(AnyJson.fromJson("\"-\\u0058-\"").toString()).isEqualTo("-X-");
	}

	@Test
	public void should_serialize_nulls_as_empty_strings() {
		assertThat(AnyJson.toJson(Any.NULL)).isEqualTo("\"\"");

		assertThat(AnyJson.toJson(Any.list(b -> {
			b.put(Any.NULL);
			b.put(Any.of("abc"));
		}))).isEqualTo("[\"\",\"abc\"]");
	}
}
