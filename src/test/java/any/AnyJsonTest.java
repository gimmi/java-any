package any;

import io.github.gimmi.Any;
import io.github.gimmi.AnyJson;
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
		assertThat(any.get("numProp").toBigDecimal()).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.get("trueProp").toBoolean()).isTrue();
		assertThat(any.get("falseProp").toBoolean()).isFalse();
		assertThat(any.get("aryProp").count()).isEqualTo(7);
		assertThat(any.get("aryProp").get(0).count()).isZero();
		assertThat(any.get("aryProp").get(1).count()).isZero();
		assertThat(any.get("aryProp").get(2).count()).isZero();
		assertThat(any.get("aryProp").get(3).toString()).isEqualTo("val");
		assertThat(any.get("aryProp").get(4).toBigDecimal()).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.get("aryProp").get(5).toBoolean()).isTrue();
		assertThat(any.get("aryProp").get(6).toBoolean()).isFalse();
		assertThat(any.get("objProp").count()).isEqualTo(4);
		assertThat(any.get("objProp").get("strProp").toString()).isEqualTo("val");
		assertThat(any.get("objProp").get("numProp").toBigDecimal()).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.get("objProp").get("trueProp").toBoolean()).isTrue();
		assertThat(any.get("objProp").get("falseProp").toBoolean()).isFalse();
	}

	@Test
	public void should_serialize_complex_obj() {
		String json = AnyJson.toJson(Any.map(b -> {
			b.put("strProp", Any.scalar("val"));
			b.put("numProp", Any.scalar(new BigDecimal("-3.14e2")));
			b.put("trueProp", Any.scalar(true));
			b.put("falseProp", Any.scalar(false));
			b.put("nullProp", Any.NULL);
			b.put("emptyAry", Any.list(bb -> {}));
			b.put("emptyObj", Any.map(bb -> {}));
			b.put("aryProp", Any.list(bb -> {
				bb.put(Any.NULL);
				bb.put(Any.list(bbb -> {}));
				bb.put(Any.map(bbb -> {}));
				bb.put(Any.scalar("val"));
				bb.put(Any.scalar(new BigDecimal("-3.14e2")));
				bb.put(Any.scalar(true));
				bb.put(Any.scalar(false));
				bb.put(Any.NULL);
				bb.put(Any.list(bbb -> {}));
				bb.put(Any.map(bbb -> {}));
			}));
			b.put("objProp", Any.map(bb -> {
				bb.put("strProp", Any.scalar("val"));
				bb.put("numProp", Any.scalar(new BigDecimal("-3.14e2")));
				bb.put("trueProp", Any.scalar(true));
				bb.put("falseProp", Any.scalar(false));
				bb.put("nullProp", Any.NULL);
				bb.put("emptyAry", Any.list(bbb -> {}));
				bb.put("emptyObj", Any.map(bbb -> {}));
			}));
		})).replace('"', '\'');

		assertThat(json).isEqualTo("{'aryProp':['','','','val','-314','true','false'],'falseProp':'false','numProp':'-314','objProp':{'falseProp':'false','numProp':'-314','strProp':'val','trueProp':'true'},'strProp':'val','trueProp':'true'}");
	}

	@Test
	public void should_serialize_special_chars() {
		assertThat(AnyJson.toJson(Any.scalar("-\"-"))).isEqualTo("\"-\\\"-\"");
		assertThat(AnyJson.toJson(Any.scalar("-/-"))).isEqualTo("\"-\\/-\"");
		assertThat(AnyJson.toJson(Any.scalar("-\b-"))).isEqualTo("\"-\\b-\"");
		assertThat(AnyJson.toJson(Any.scalar("-\f-"))).isEqualTo("\"-\\f-\"");
		assertThat(AnyJson.toJson(Any.scalar("-\n-"))).isEqualTo("\"-\\n-\"");
		assertThat(AnyJson.toJson(Any.scalar("-\t-"))).isEqualTo("\"-\\t-\"");
		assertThat(AnyJson.toJson(Any.scalar("-\r-"))).isEqualTo("\"-\\r-\"");
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
