package com.github.gimmi.any;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AnyJsonSerializerTest {
   private AnyJsonSerializer sut;

   @Before
   public void before() {
      sut = new AnyJsonSerializer();
   }

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

      Any any = sut.fromJson(json);

      assertThat(any.count()).isEqualTo(6);
      assertThat(any.key("strProp").or("")).isEqualTo("val");
      assertThat(any.key("numProp").or(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
      assertThat(any.key("trueProp").or(false)).isTrue();
      assertThat(any.key("falseProp").or(false)).isFalse();
      assertThat(any.key("aryProp").count()).isEqualTo(7);
      assertThat(any.key("aryProp").at(0).count()).isZero();
      assertThat(any.key("aryProp").at(1).count()).isZero();
      assertThat(any.key("aryProp").at(2).count()).isZero();
      assertThat(any.key("aryProp").at(3).or("")).isEqualTo("val");
      assertThat(any.key("aryProp").at(4).or(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
      assertThat(any.key("aryProp").at(5).or(false)).isTrue();
      assertThat(any.key("aryProp").at(6).or(false)).isFalse();
      assertThat(any.key("objProp").count()).isEqualTo(4);
      assertThat(any.key("objProp").key("strProp").or("")).isEqualTo("val");
      assertThat(any.key("objProp").key("numProp").or(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
      assertThat(any.key("objProp").key("trueProp").or(false)).isTrue();
      assertThat(any.key("objProp").key("falseProp").or(false)).isFalse();
   }

   @Test
   public void should_serialize_complex_obj() {
      String json = sut.toJson(Any.map(b -> {
         b.append("strProp", Any.of("val"));
         b.append("numProp", Any.of(new BigDecimal("-3.14e2")));
         b.append("trueProp", Any.of(true));
         b.append("falseProp", Any.of(false));
         b.append("nullProp", Any.NULL);
         b.append("emptyAry", Any.list(bb -> {}));
         b.append("emptyObj", Any.map(bb -> {}));
         b.append("aryProp", Any.list(bb -> {
            bb.append(Any.NULL);
            bb.append(Any.list(bbb -> {}));
            bb.append(Any.map(bbb -> {}));
            bb.append(Any.of("val"));
            bb.append(Any.of(new BigDecimal("-3.14e2")));
            bb.append(Any.of(true));
            bb.append(Any.of(false));
            bb.append(Any.NULL);
            bb.append(Any.list(bbb -> {}));
            bb.append(Any.map(bbb -> {}));
         }));
         b.append("objProp", Any.map(bb -> {
            bb.append("strProp", Any.of("val"));
            bb.append("numProp", Any.of(new BigDecimal("-3.14e2")));
            bb.append("trueProp", Any.of(true));
            bb.append("falseProp", Any.of(false));
            bb.append("nullProp", Any.NULL);
            bb.append("emptyAry", Any.list(bbb -> {}));
            bb.append("emptyObj", Any.map(bbb -> {}));
         }));
      })).replace('"', '\'');

      assertThat(json).isEqualTo("{'aryProp':['','','','val','-314','true','false'],'falseProp':'false','numProp':'-314','objProp':{'falseProp':'false','numProp':'-314','strProp':'val','trueProp':'true'},'strProp':'val','trueProp':'true'}");
   }

   @Test
   public void should_serialize_special_chars() {
      assertThat(sut.toJson(Any.of("-\"-"))).isEqualTo("\"-\\\"-\"");
      assertThat(sut.toJson(Any.of("-/-"))).isEqualTo("\"-\\/-\"");
      assertThat(sut.toJson(Any.of("-\b-"))).isEqualTo("\"-\\b-\"");
      assertThat(sut.toJson(Any.of("-\f-"))).isEqualTo("\"-\\f-\"");
      assertThat(sut.toJson(Any.of("-\n-"))).isEqualTo("\"-\\n-\"");
      assertThat(sut.toJson(Any.of("-\t-"))).isEqualTo("\"-\\t-\"");
      assertThat(sut.toJson(Any.of("-\r-"))).isEqualTo("\"-\\r-\"");
   }

   @Test
   public void should_deserialize_special_chars() {
      assertThat(sut.fromJson("\"-\\\"-\"").or("")).isEqualTo("-\"-");
      assertThat(sut.fromJson("\"-\\/-\"").or("")).isEqualTo("-/-");
      assertThat(sut.fromJson("\"-\\b-\"").or("")).isEqualTo("-\b-");
      assertThat(sut.fromJson("\"-\\f-\"").or("")).isEqualTo("-\f-");
      assertThat(sut.fromJson("\"-\\n-\"").or("")).isEqualTo("-\n-");
      assertThat(sut.fromJson("\"-\\t-\"").or("")).isEqualTo("-\t-");
      assertThat(sut.fromJson("\"-\\r-\"").or("")).isEqualTo("-\r-");
      assertThat(sut.fromJson("\"-\\u0058-\"").or("")).isEqualTo("-X-");
   }

   @Test
   public void should_serialize_nulls_as_empty_strings() {
      assertThat(sut.toJson(Any.NULL)).isEqualTo("\"\"");

      assertThat(sut.toJson(Any.list(b -> {
         b.append(Any.NULL);
         b.append(Any.of("abc"));
      }))).isEqualTo("[\"\",\"abc\"]");
   }
}
