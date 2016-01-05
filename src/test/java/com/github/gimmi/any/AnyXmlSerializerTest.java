package com.github.gimmi.any;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AnyXmlSerializerTest {

   private AnyXmlSerializer sut;

   @Before
   public void before() {
      sut = new AnyXmlSerializer();
   }

   @Test
   public void should_keep_root_element_with_attributes() {
      Any any = sut.fromXml("<Root attr='v1'><Elem>v2</Elem></Root>");

      assertThat(any.count()).isEqualTo(1);
      assertThat(any.key("Root").count()).isEqualTo(2);
      assertThat(any.key("Root").key("attr").or("")).isEqualTo("v1");
      assertThat(any.key("Root").key("Elem").or("")).isEqualTo("v2");
   }

   @Test
   public void empty_root_element_deserialize_to_null() {
      assertThat(sut.fromXml("<Root />").count()).isEqualTo(0);
   }

   @Test
   public void should_deserialize_xml() {
      String xml = String.join("",
            "<Root>",
            "  <StrProp>val</StrProp>",
            "  <NumProp>-3.14e2</NumProp>",
            "  <TrueProp>true</TrueProp>",
            "  <FalseProp>false</FalseProp>",
            "  <NullProp />",
            "  <AryProp />",
            "  <AryProp>val</AryProp>",
            "  <AryProp>-3.14e2</AryProp>",
            "  <AryProp>true</AryProp>",
            "  <AryProp>false</AryProp>",
            "  <AryProp />",
            "  <ObjProp>",
            "    <StrProp>val</StrProp>",
            "    <NumProp>-3.14e2</NumProp>",
            "    <TrueProp>true</TrueProp>",
            "    <FalseProp>false</FalseProp>",
            "    <NullProp />",
            "  </ObjProp>",
            "</Root>"
      ).replace('\'', '"');

      Any any = sut.fromXml(xml);

      assertThat(any.count()).isEqualTo(1);
      assertThat(any.key("Root").count()).isEqualTo(6);
      assertThat(any.key("Root").key("strProp").or("")).isEqualTo("val");
      assertThat(any.key("Root").key("numProp").or(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
      assertThat(any.key("Root").key("trueProp").or(false)).isTrue();
      assertThat(any.key("Root").key("falseProp").or(false)).isFalse();
      assertThat(any.key("Root").key("aryProp").count()).isEqualTo(5);
      assertThat(any.key("Root").key("aryProp").at(0).or("")).isEmpty();
      assertThat(any.key("Root").key("aryProp").at(1).or("")).isEqualTo("val");
      assertThat(any.key("Root").key("aryProp").at(2).or("")).isEqualTo("-3.14e2");
      assertThat(any.key("Root").key("aryProp").at(3).or("")).isEqualTo("true");
      assertThat(any.key("Root").key("aryProp").at(4).or("")).isEqualTo("false");
      assertThat(any.key("Root").key("objProp").count()).isEqualTo(4);
      assertThat(any.key("Root").key("objProp").keys()).containsOnly("StrProp", "NumProp", "TrueProp", "FalseProp");
      assertThat(any.key("Root").key("objProp").key("strProp").or("")).isEqualTo("val");
      assertThat(any.key("Root").key("objProp").key("numProp").or("")).isEqualTo("-3.14e2");
      assertThat(any.key("Root").key("objProp").key("trueProp").or("")).isEqualTo("true");
      assertThat(any.key("Root").key("objProp").key("falseProp").or("")).isEqualTo("false");
   }

   @Test
   public void should_support_namespaces() {
      String xml = String.join("",
            "<Root xmlns='urn:ns1' xmlns:ns2='urn:ns2'>",
            "  <StrProp>val</StrProp>",
            "  <ns2:ObjProp>",
            "    <ns2:StrProp>val</ns2:StrProp>",
            "  </ns2:ObjProp>",
            "</Root>"
      ).replace('\'', '"');

      Any any = sut.fromXml(xml);

      assertThat(any.key("Root").keys()).containsOnly("StrProp", "ObjProp");
      assertThat(any.key("Root").count()).isEqualTo(2);
   }

   @Test
   public void should_keep_attribute_and_text() {
      String xml = String.join("",
            "<Root attr1='val1' attr2='val2'>val3</Root>"
      ).replace('\'', '"');

      Any any = sut.fromXml(xml);

      assertThat(any.key("Root").keys()).containsOnly("attr1", "attr2", "text");
      assertThat(any.key("Root").key("attr1").or("")).isEqualTo("val1");
      assertThat(any.key("Root").key("attr2").or("")).isEqualTo("val2");
      assertThat(any.key("Root").key("text").or("")).isEqualTo("val3");
   }

   @Test
   public void should_write_empty_xml() {
      assertThat(sut.toXml("root", Any.NULL)).isEqualTo("<root></root>");
   }

   @Test
   public void should_write_xml_with_scalar_value() {
      assertThat(sut.toXml("root", Any.of("text"))).isEqualTo("<root>text</root>");
      assertThat(sut.toXml("root", Any.of("<html>fragment</html>"))).isEqualTo("<root>&lt;html&gt;fragment&lt;/html&gt;</root>");
      assertThat(sut.toXml("root", Any.of(123))).isEqualTo("<root>123</root>");
      assertThat(sut.toXml("root", Any.of(LocalDateTime.of(2016, 1, 5, 20, 30)))).isEqualTo("<root>2016-01-05T20:30</root>");
   }

   @Test
   public void should_flatten_nested_lists() {
      assertThat(sut.toXml("root", Any.list(b -> {
         b.append(Any.NULL);
         b.append(Any.of("v21"));
         b.append(Any.of("v22"));
         b.append(Any.list(bb -> {
            bb.append(Any.of("v23"));
            bb.append(Any.of("v24"));
         }));
         b.append(Any.NULL);
         b.append(Any.list(bb -> {
            bb.append(Any.of("v25"));
            bb.append(Any.of("v26"));
         }));
      }))).isEqualTo("<root></root><root>v21</root><root>v22</root><root>v23</root><root>v24</root><root></root><root>v25</root><root>v26</root>");
   }

   @Test
   public void should_serialize_nested_maps() {
      assertThat(sut.toXml("root", Any.map(b -> {
         b.append("k1", Any.of("v1"));
         b.append("k2", Any.list(bbb -> {
            bbb.append(Any.of("v21"));
            bbb.append(Any.of("v22"));
         }));
         b.append("k3", Any.map(bbb -> {
            bbb.append("k31", Any.of("v31"));
            bbb.append("k32", Any.of("v32"));
         }));
      }))).isEqualTo("<root><k1>v1</k1><k2>v21</k2><k2>v22</k2><k3><k31>v31</k31><k32>v32</k32></k3></root>");
   }
}
