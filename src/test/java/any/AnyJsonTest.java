package any;

import static org.apache.commons.lang3.StringUtils.join;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import io.github.gimmi.Any;
import io.github.gimmi.AnyJson;

public class AnyJsonTest {
	@Test
	public void should_parse_complex_json() {
		String json = join(
			"{",
            "    'strProp': 'val',",
            "    'numProp': -3.14e2,",
            "    'trueProp': true,",
            "    'falseProp': false,",
            "    'nullProp': null,",
            "    'emptyAry': [],",
            "    'emptyObj': {},",
            "    'aryProp': [",
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
		
        assertThat(any.cardinality()).isEqualTo(9);
        assertThat(any.get("strProp").toString()).isEqualTo("val");
        assertThat(any.get("numProp").toBigDecimal()).isEqualByComparingTo(new BigDecimal("-314"));
        assertThat(any.get("trueProp").toBoolean()).isTrue();
        assertThat(any.get("falseProp").toBoolean()).isFalse();
        assertThat(any.get("nullProp").toString()).isEmpty();
        assertThat(any.get("nullProp").cardinality()).isZero();
        assertThat(any.get("emptyAry").cardinality()).isZero();
        assertThat(any.get("emptyObj").cardinality()).isZero();
        assertThat(any.get("aryProp").cardinality()).isEqualTo(7);
        assertThat(any.get("aryProp").get(0).toString()).isEqualTo("val");
        assertThat(any.get("aryProp").get(1).toBigDecimal()).isEqualByComparingTo(new BigDecimal("-314"));
        assertThat(any.get("aryProp").get(2).toBoolean()).isTrue();
        assertThat(any.get("aryProp").get(3).toBoolean()).isFalse();
        assertThat(any.get("aryProp").get(4).toString()).isEmpty();
        assertThat(any.get("aryProp").get(5).cardinality()).isZero();
        assertThat(any.get("aryProp").get(6).cardinality()).isZero();
        assertThat(any.get("objProp").cardinality()).isEqualTo(7);
        assertThat(any.get("objProp").get("strProp").toString()).isEqualTo("val");
        assertThat(any.get("objProp").get("numProp").toBigDecimal()).isEqualByComparingTo(new BigDecimal("-314"));
        assertThat(any.get("objProp").get("trueProp").toBoolean()).isTrue();
        assertThat(any.get("objProp").get("falseProp").toBoolean()).isFalse();
        assertThat(any.get("objProp").get("nullProp").toString()).isEmpty();
        assertThat(any.get("objProp").get("emptyAry").cardinality()).isZero();
        assertThat(any.get("objProp").get("emptyObj").cardinality()).isZero();
	}
	
	@Test
	public void should_serialize_complex_obj() {
		String json = AnyJson.toJson(Any.map(b -> {
			b.put("strProp", Any.scalar("val"));
			b.put("numProp", Any.scalar(new BigDecimal("-3.14e2")));
			b.put("trueProp", Any.scalar(true));
			b.put("falseProp", Any.scalar(false));
			b.put("nullProp", Any.scalar(""));
			b.put("emptyAry", Any.list(bb -> {}));
			b.put("emptyObj", Any.map(bb -> {}));
			b.put("aryProp", Any.list(bb -> {
				bb.put(Any.scalar("val"));
				bb.put(Any.scalar(new BigDecimal("-3.14e2")));
				bb.put(Any.scalar(true));
				bb.put(Any.scalar(false));
				bb.put(Any.scalar(""));
				bb.put(Any.list(bbb -> {}));
				bb.put(Any.map(bbb -> {}));
			}));
			b.put("objProp", Any.map(bb -> {
				bb.put("strProp", Any.scalar("val"));
				bb.put("numProp", Any.scalar(new BigDecimal("-3.14e2")));
				bb.put("trueProp", Any.scalar(true));
				bb.put("falseProp", Any.scalar(false));
				bb.put("nullProp", Any.scalar(""));
				bb.put("emptyAry", Any.list(bbb -> {}));
				bb.put("emptyObj", Any.map(bbb -> {}));
			}));
		})).replace('"', '\'');
		
		assertThat(json).isEqualTo("{'aryProp':['val','-314','true','false','',[],{}],'emptyAry':[],'emptyObj':{},'falseProp':'false','nullProp':'','numProp':'-314','objProp':{'emptyAry':[],'emptyObj':{},'falseProp':'false','nullProp':'','numProp':'-314','strProp':'val','trueProp':'true'},'strProp':'val','trueProp':'true'}");
	}
}
