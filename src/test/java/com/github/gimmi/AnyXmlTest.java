package com.github.gimmi;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AnyXmlTest {
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

		Any any = AnyXml.fromXml(xml);

		assertThat(any.count()).isEqualTo(6);
		assertThat(any.key("strProp").toString()).isEqualTo("val");
		assertThat(any.key("numProp").val(BigDecimal.ZERO)).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat((boolean) any.key("trueProp").val(false)).isTrue();
		assertThat((boolean) any.key("falseProp").val(false)).isFalse();
		assertThat(any.key("aryProp").count()).isEqualTo(5);
		assertThat(any.key("aryProp").at(0).toString()).isEmpty();
		assertThat(any.key("aryProp").at(1).toString()).isEqualTo("val");
		assertThat(any.key("aryProp").at(2).toString()).isEqualTo("-3.14e2");
		assertThat(any.key("aryProp").at(3).toString()).isEqualTo("true");
		assertThat(any.key("aryProp").at(4).toString()).isEqualTo("false");
		assertThat(any.key("objProp").count()).isEqualTo(4);
		assertThat(any.key("objProp").keys()).containsOnly("StrProp", "NumProp", "TrueProp", "FalseProp");
		assertThat(any.key("objProp").key("strProp").toString()).isEqualTo("val");
		assertThat(any.key("objProp").key("numProp").toString()).isEqualTo("-3.14e2");
		assertThat(any.key("objProp").key("trueProp").toString()).isEqualTo("true");
		assertThat(any.key("objProp").key("falseProp").toString()).isEqualTo("false");
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

		Any any = AnyXml.fromXml(xml);

		assertThat(any.keys()).containsOnly("StrProp", "ObjProp");
		assertThat(any.count()).isEqualTo(2);
	}

	@Test
	public void should_keep_attribute_and_text() {
		String xml = String.join("",
			"<Root attr1='val1' attr2='val2'>val3</Root>"
		).replace('\'', '"');

		Any any = AnyXml.fromXml(xml);

		assertThat(any.keys()).containsOnly("attr1", "attr2", "text");
		assertThat(any.key("attr1").toString()).isEqualTo("val1");
		assertThat(any.key("attr2").toString()).isEqualTo("val2");
		assertThat(any.key("text").toString()).isEqualTo("val3");
	}
}
