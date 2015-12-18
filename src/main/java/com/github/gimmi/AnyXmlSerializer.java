package com.github.gimmi;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Stack;

public class AnyXmlSerializer {
	public Any fromXml(String xml) {
		return fromXml(new StringReader(xml));
	}

	public Any fromXml(Reader reader) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
			Handler handler = new Handler();
			saxParser.parse(new InputSource(reader), handler);
			return handler.any;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (ParserConfigurationException | SAXException e) {
			throw new RuntimeException("Error parsing XML", e);
		}
	}

	private static class Handler extends DefaultHandler {
		private final Stack<AnyMapBuilder> elements = new Stack<>();
		private final StringBuilder sb = new StringBuilder();
		private Any any;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			AnyMapBuilder builder = new AnyMapBuilder();
			for (int i = 0; i < attributes.getLength(); i++) {
				builder.put(attributes.getLocalName(i), Any.of(attributes.getValue(i)));
			}
			elements.push(builder);
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			sb.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			AnyMapBuilder builder = elements.pop();
			any = Any.of(sb.toString());
			sb.setLength(0);
			if (builder.count() > 0) {
				any = builder.put("text", any).build();
			}

			if (!elements.empty()) {
				elements.peek().put(localName, any);
			}
		}
	}
}
