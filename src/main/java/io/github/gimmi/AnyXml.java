package io.github.gimmi;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

public class AnyXml {
	public static Any fromXml(String xml) {
		return fromXml(new StringReader(xml));
	}

	public static Any fromXml(Reader reader) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			Handler handler = new Handler();
			saxParser.parse(new InputSource(reader), handler);
			return handler.elements.pop().build();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException("Erro parsing XML", e);
		}
	}

	private static class Handler extends DefaultHandler {
		private final Stack<AnyMapBuilder> elements = new Stack<>();

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			AnyMapBuilder builder = new AnyMapBuilder();
			for (int i = 0; i < attributes.getLength(); i++) {
				builder.put(attributes.getLocalName(i), Any.scalar(attributes.getValue(i)));
			}
			elements.push(builder);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (elements.size() > 1) {
				elements.peek().put(localName, elements.pop().build());
			}
		}
	}
}
