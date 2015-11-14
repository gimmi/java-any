package io.github.gimmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;

public class AnyJson {
	public static Any parse(String str) {
		return parse(new StringReader(str));
	}
	
    public static Any parse(Reader reader) {
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
			        mapb.put(key, parse(reader));
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
			        listb.put(parse(reader));
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
			    return Any.scalar("");
			} else if (ch == 't') {
				readExpected(reader, "true");
				return Any.scalar(true);
			} else if (ch == 'f') {
				readExpected(reader, "false");
				return Any.scalar(false);
			} else if (ch == '"') {
			    return Any.scalar(parseString(reader));
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
			    return Any.scalar(new BigDecimal(sb.toString()));
			}
			throw new RuntimeException("Unexpected char '" + ch + "'");
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON", e);
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
                switch (ch) {
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    default:
                        sb.append(ch);
                        break;
                }
            } else if (ch == '"') {
                return sb.toString();
            } else {
                sb.append(ch);
            }
        }
    }

	public static void readExpected(Reader tr, String str) throws IOException {
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
		return (char)character;
	}

    private static char peekOrFail(Reader reader) throws IOException {
		int character = peek(reader);
		if (character == -1) {
			throw new RuntimeException("Unexpected end of stream");
		}
		return (char)character;
	}

    private static void eatWhitespaces(Reader reader) throws IOException {
		int next = peek(reader);
		while (next != -1 && Character.isWhitespace((char)next)) {
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
}
