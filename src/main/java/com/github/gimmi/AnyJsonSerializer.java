package com.github.gimmi;

import java.io.*;
import java.util.Iterator;

import static com.github.gimmi.Utils.stripToEmpty;

public class AnyJsonSerializer {
	public Any fromJson(String str) {
		return fromJson(new StringReader(str));
	}

	public Any fromJson(Reader reader) {
		try {
			if (!reader.markSupported()) {
				reader = new BufferedReader(reader);
			}
			eatWhitespaces(reader);
			char ch = peekOrFail(reader);
			if (ch == '{') {
				reader.read();
				AnyMapBuilder mapb = new AnyMapBuilder();
				eatWhitespaces(reader);
				while (peekOrFail(reader) != '}') {
					String key = parseString(reader);
					eatWhitespaces(reader);
					readExpected(reader, ":");
					mapb.put(key, fromJson(reader));
					eatWhitespaces(reader);
					if (peekOrFail(reader) == ',') {
						reader.read();
						eatWhitespaces(reader);
					}
				}
				reader.read();
				return mapb.build();
			} else if (ch == '[') {
				reader.read();
				AnyListBuilder listb = new AnyListBuilder();
				eatWhitespaces(reader);
				while (peekOrFail(reader) != ']') {
					listb.put(fromJson(reader));
					eatWhitespaces(reader);
					if (peekOrFail(reader) == ',') {
						reader.read();
						eatWhitespaces(reader);
					}
				}
				reader.read();
				return listb.build();
			} else if (ch == 'n') {
				readExpected(reader, "null");
				return Any.NULL;
			} else if (ch == 't') {
				readExpected(reader, "true");
				return Any.of(true);
			} else if (ch == 'f') {
				readExpected(reader, "false");
				return Any.of(false);
			} else if (ch == '"') {
				return Any.of(parseString(reader));
			} else if (ch == '-' || Character.isDigit(ch)) {
				StringBuilder sb = new StringBuilder();
				while (ch == '+' || ch == '.' || ch == '-' || ch == 'e' || ch == 'E' || Character.isDigit(ch)) {
					sb.append(readOrFail(reader));
					int peek = peek(reader);
					if (peek == -1) {
						break;
					}
					ch = (char) peek;
				}
				return Any.of(sb.toString());
			}
			throw new RuntimeException("Unexpected char '" + ch + "'");
		} catch (IOException e) {
			throw new RuntimeException("Error parsing JSON", e);
		}
	}

	public String toJson(Any any) {
		StringWriter writer = new StringWriter();
		toJson(any, writer);
		return writer.toString();
	}

	public void toJson(Any any, Writer w) {
		try {
			String comma = "";
			Iterator<String> keys = any.keys().iterator();
			if (keys.hasNext()) {
				w.write('{');
				while (keys.hasNext()) {
					String key = keys.next();
					write(w, comma);
					comma = ",";
					writeJsonString(key, w);
					write(w, ':');
					toJson(any.key(key), w);
				}
				w.write('}');
			} else if (any.count() > 1) {
				w.write('[');
				for (Any value : any.values()) {
					write(w, comma);
					comma = ",";
					toJson(value, w);
				}
				w.write(']');
			} else {
				writeJsonString(any.or(""), w);
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Unable to serialize as JSON", e);
		}
	}

	private static String parseString(Reader en) throws IOException {
		eatWhitespaces(en);
		char ch = readOrFail(en);
		if (ch != '"') {
			throw new IOException("Expected '\"', found '" + ch + "'");
		}
		StringBuilder sb = new StringBuilder();
		while (true) {
			ch = readOrFail(en);
			if (ch == '\\') {
				ch = readOrFail(en);
				if (ch == 'b') {
					sb.append('\b');
				} else if (ch == 'f') {
					sb.append('\f');
				} else if (ch == 'n') {
					sb.append('\n');
				} else if (ch == 'r') {
					sb.append('\r');
				} else if (ch == 't') {
					sb.append('\t');
				} else if (ch == 'u') {
					char[] unicodeChars = {readOrFail(en), readOrFail(en), readOrFail(en), readOrFail(en)};
					String unicodeString = new String(unicodeChars, 0, 4);
					int unicodeInt = Integer.parseInt(unicodeString, 16);
					sb.append((char) unicodeInt);
				} else {
					sb.append(ch);
				}
			} else if (ch == '"') {
				return sb.toString();
			} else {
				sb.append(ch);
			}
		}
	}

	private static void writeJsonString(String str, Writer w) {
		str = stripToEmpty(str);
		write(w, '"');
		for (int i = 0; i < str.length(); i++) {
			// TODO does not write unicode chars as \x0000
			char ch = str.charAt(i);
			if (ch == '"') {
				write(w, "\\\"");
			} else if (ch == '/') {
				write(w, "\\/");
			} else if (ch == '\b') {
				write(w, "\\b");
			} else if (ch == '\f') {
				write(w, "\\f");
			} else if (ch == '\n') {
				write(w, "\\n");
			} else if (ch == '\t') {
				write(w, "\\t");
			} else if (ch == '\r') {
				write(w, "\\r");
			} else {
				write(w, ch);
			}
		}
		write(w, '"');
	}

	private static void readExpected(Reader tr, String str) throws IOException {
		for (int i = 0; i < str.length(); i++) {
			char expected = str.charAt(i);
			char actual = readOrFail(tr);
			if (actual != expected) {
				throw new RuntimeException("Expected '" + expected + "', found '" + actual + "'");
			}
		}
	}

	private static char readOrFail(Reader tr) throws IOException {
		int character = tr.read();
		if (character == -1) {
			throw new RuntimeException("Unexpected end of stream");
		}
		return (char) character;
	}

	private static char peekOrFail(Reader reader) throws IOException {
		int character = peek(reader);
		if (character == -1) {
			throw new RuntimeException("Unexpected end of stream");
		}
		return (char) character;
	}

	private static void eatWhitespaces(Reader reader) throws IOException {
		int next = peek(reader);
		while (next != -1 && Character.isWhitespace((char) next)) {
			reader.read();
			next = peek(reader);
		}
	}

	private static int peek(Reader reader) throws IOException {
		reader.mark(1);
		int character = reader.read();
		reader.reset();
		return character;
	}

	public static void write(Writer writer, int c) {
		try {
			writer.write(c);
		} catch (IOException e) {
			throw new UncheckedIOException("Unable to write JSON", e);
		}
	}

	public static void write(Writer writer, String str) {
		try {
			writer.write(str);
		} catch (IOException e) {
			throw new UncheckedIOException("Unable to write JSON", e);
		}
	}
}
