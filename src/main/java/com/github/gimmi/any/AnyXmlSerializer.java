package com.github.gimmi.any;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
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
         return handler.elements.pop().build();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      } catch (ParserConfigurationException | SAXException e) {
         throw new RuntimeException("Error parsing XML", e);
      }
   }

   public String toXml(String root, Any any) {
      StringWriter writer = new StringWriter();
      toXml(root, any, writer);
      return writer.toString();
   }

   public void toXml(String root, Any any, Writer writer) {
      try {
         XMLOutputFactory xof = XMLOutputFactory.newInstance();
         XMLStreamWriter xml = xof.createXMLStreamWriter(writer);
         write(root, any, xml);
         xml.flush();
         xml.close();
      } catch (XMLStreamException e) {
         throw new RuntimeException(e);
      }
   }

   private void write(String currentKey, Any current, XMLStreamWriter xml) throws XMLStreamException {
      if (current.keyCount() > 0) {
         xml.writeStartElement(currentKey);
         for (String innerKey : current.keys()) {
            write(innerKey, current.key(innerKey), xml);
         }
         xml.writeEndElement();
      } else if (current.count() > 1) {
         for (Any inner : current.values()) {
            write(currentKey, inner, xml);
         }
      } else {
         xml.writeStartElement(currentKey);
         xml.writeCharacters(current.or(""));
         xml.writeEndElement();
      }
   }

   private static class Handler extends DefaultHandler {
      private final Stack<AnyMapBuilder> elements;
      private final StringBuilder sb = new StringBuilder();

      public Handler() {
         elements = new Stack<>();
         elements.push(new AnyMapBuilder());
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         AnyMapBuilder builder = new AnyMapBuilder();
         for (int i = 0; i < attributes.getLength(); i++) {
            builder.append(attributes.getLocalName(i), Any.of(attributes.getValue(i)));
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
         Any any = Any.of(sb.toString());
         sb.setLength(0);
         if (builder.count() > 0) {
            any = builder.append("text", any).build();
         }

         elements.peek().append(localName, any);
      }
   }
}
