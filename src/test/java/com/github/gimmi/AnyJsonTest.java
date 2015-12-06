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
		assertThat(any.get("strProp").toString()).isEqualTo("val");
		assertThat(any.get("numProp").val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.get("trueProp").val(false)).isTrue();
		assertThat(any.get("falseProp").val(false)).isFalse();
		assertThat(any.get("aryProp").count()).isEqualTo(7);
		assertThat(any.get("aryProp").get(0).count()).isZero();
		assertThat(any.get("aryProp").get(1).count()).isZero();
		assertThat(any.get("aryProp").get(2).count()).isZero();
		assertThat(any.get("aryProp").get(3).toString()).isEqualTo("val");
		assertThat(any.get("aryProp").get(4).val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.get("aryProp").get(5).val(false)).isTrue();
		assertThat(any.get("aryProp").get(6).val(false)).isFalse();
		assertThat(any.get("objProp").count()).isEqualTo(4);
		assertThat(any.get("objProp").get("strProp").toString()).isEqualTo("val");
		assertThat(any.get("objProp").get("numProp").val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.get("objProp").get("trueProp").val(false)).isTrue();
		assertThat(any.get("objProp").get("falseProp").val(false)).isFalse();
	}

	@Test
	public void should_serialize_complex_obj() {
		String json = AnyJson.toJson(Any.map(b -> {
			b.put("strProp", Any.from("val"));
			b.put("numProp", Any.from(new BigDecimal("-3.14e2")));
			b.put("trueProp", Any.from(true));
			b.put("falseProp", Any.from(false));
			b.put("nullProp", Any.NULL);
			b.put("emptyAry", Any.list(bb -> {}));
			b.put("emptyObj", Any.map(bb -> {}));
			b.put("aryProp", Any.list(bb -> {
				bb.put(Any.NULL);
				bb.put(Any.list(bbb -> {}));
				bb.put(Any.map(bbb -> {}));
				bb.put(Any.from("val"));
				bb.put(Any.from(new BigDecimal("-3.14e2")));
				bb.put(Any.from(true));
				bb.put(Any.from(false));
				bb.put(Any.NULL);
				bb.put(Any.list(bbb -> {}));
				bb.put(Any.map(bbb -> {}));
			}));
			b.put("objProp", Any.map(bb -> {
				bb.put("strProp", Any.from("val"));
				bb.put("numProp", Any.from(new BigDecimal("-3.14e2")));
				bb.put("trueProp", Any.from(true));
				bb.put("falseProp", Any.from(false));
				bb.put("nullProp", Any.NULL);
				bb.put("emptyAry", Any.list(bbb -> {}));
				bb.put("emptyObj", Any.map(bbb -> {}));
			}));
		})).replace('"', '\'');

		assertThat(json).isEqualTo("{'aryProp':['','','','val','-314','true','false'],'falseProp':'false','numProp':'-314','objProp':{'falseProp':'false','numProp':'-314','strProp':'val','trueProp':'true'},'strProp':'val','trueProp':'true'}");
	}

	@Test
	public void should_serialize_special_chars() {
		assertThat(AnyJson.toJson(Any.from("-\"-"))).isEqualTo("\"-\\\"-\"");
		assertThat(AnyJson.toJson(Any.from("-/-"))).isEqualTo("\"-\\/-\"");
		assertThat(AnyJson.toJson(Any.from("-\b-"))).isEqualTo("\"-\\b-\"");
		assertThat(AnyJson.toJson(Any.from("-\f-"))).isEqualTo("\"-\\f-\"");
		assertThat(AnyJson.toJson(Any.from("-\n-"))).isEqualTo("\"-\\n-\"");
		assertThat(AnyJson.toJson(Any.from("-\t-"))).isEqualTo("\"-\\t-\"");
		assertThat(AnyJson.toJson(Any.from("-\r-"))).isEqualTo("\"-\\r-\"");
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
}
