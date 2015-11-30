package any;

import io.github.gimmi.Any;
import io.github.gimmi.AnyXml;
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
		assertThat(any.get("strProp").toString()).isEqualTo("val");
		assertThat(any.get("numProp").toBigDecimal()).isEqualByComparingTo(new BigDecimal("-314"));
		assertThat(any.get("trueProp").toBoolean()).isTrue();
		assertThat(any.get("falseProp").toBoolean()).isFalse();
		assertThat(any.get("aryProp").count()).isEqualTo(5);
		assertThat(any.get("aryProp").get(0).toString()).isEmpty();
		assertThat(any.get("aryProp").get(1).toString()).isEqualTo("val");
		assertThat(any.get("aryProp").get(2).toString()).isEqualTo("-3.14e2");
		assertThat(any.get("aryProp").get(3).toString()).isEqualTo("true");
		assertThat(any.get("aryProp").get(4).toString()).isEqualTo("false");
		assertThat(any.get("objProp").count()).isEqualTo(4);
		assertThat(any.get("objProp").keys()).containsOnly("StrProp", "NumProp", "TrueProp", "FalseProp");
		assertThat(any.get("objProp").get("strProp").toString()).isEqualTo("val");
		assertThat(any.get("objProp").get("numProp").toString()).isEqualTo("-3.14e2");
		assertThat(any.get("objProp").get("trueProp").toString()).isEqualTo("true");
		assertThat(any.get("objProp").get("falseProp").toString()).isEqualTo("false");
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
		assertThat(any.get("attr1").toString()).isEqualTo("val1");
		assertThat(any.get("attr2").toString()).isEqualTo("val2");
		assertThat(any.get("text").toString()).isEqualTo("val3");
	}
}
