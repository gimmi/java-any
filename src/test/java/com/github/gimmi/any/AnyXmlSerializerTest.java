package com.github.gimmi.any;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

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
}
