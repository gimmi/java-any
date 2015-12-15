package com.github.gimmi;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Utils {
	private static final int INDEX_NOT_FOUND = -1;
	private static final String EMPTY = "";

	public static String stripToEmpty(final String str) {
		return str == null ? EMPTY : strip(str, null);
	}

	public static String stripToNull(String str) {
		if (str == null) {
			return null;
		}
		str = strip(str, null);
		return str.isEmpty() ? null : str;
	}

	public static boolean toBoolean(final String str) {
		return toBooleanObject(str) == Boolean.TRUE;
	}

	private static Boolean toBooleanObject(final String str) {
		// Previously used equalsIgnoreCase, which was fast for interned 'true'.
		// Non interned 'true' matched 15 times slower.
		//
		// Optimisation provides same performance as before for interned 'true'.
		// Similar performance for null, 'false', and other strings not length 2/3/4.
		// 'true'/'TRUE' match 4 times slower, 'tRUE'/'True' 7 times slower.
		if (str == "true") {
			return Boolean.TRUE;
		}
		if (str == null) {
			return null;
		}
		switch (str.length()) {
			case 1: {
				final char ch0 = str.charAt(0);
				if (ch0 == 'y' || ch0 == 'Y' ||
						ch0 == 't' || ch0 == 'T') {
					return Boolean.TRUE;
				}
				if (ch0 == 'n' || ch0 == 'N' ||
						ch0 == 'f' || ch0 == 'F') {
					return Boolean.FALSE;
				}
				break;
			}
			case 2: {
				final char ch0 = str.charAt(0);
				final char ch1 = str.charAt(1);
				if ((ch0 == 'o' || ch0 == 'O') &&
						(ch1 == 'n' || ch1 == 'N') ) {
					return Boolean.TRUE;
				}
				if ((ch0 == 'n' || ch0 == 'N') &&
						(ch1 == 'o' || ch1 == 'O') ) {
					return Boolean.FALSE;
				}
				break;
			}
			case 3: {
				final char ch0 = str.charAt(0);
				final char ch1 = str.charAt(1);
				final char ch2 = str.charAt(2);
				if ((ch0 == 'y' || ch0 == 'Y') &&
						(ch1 == 'e' || ch1 == 'E') &&
						(ch2 == 's' || ch2 == 'S') ) {
					return Boolean.TRUE;
				}
				if ((ch0 == 'o' || ch0 == 'O') &&
						(ch1 == 'f' || ch1 == 'F') &&
						(ch2 == 'f' || ch2 == 'F') ) {
					return Boolean.FALSE;
				}
				break;
			}
			case 4: {
				final char ch0 = str.charAt(0);
				final char ch1 = str.charAt(1);
				final char ch2 = str.charAt(2);
				final char ch3 = str.charAt(3);
				if ((ch0 == 't' || ch0 == 'T') &&
						(ch1 == 'r' || ch1 == 'R') &&
						(ch2 == 'u' || ch2 == 'U') &&
						(ch3 == 'e' || ch3 == 'E') ) {
					return Boolean.TRUE;
				}
				break;
			}
			case 5: {
				final char ch0 = str.charAt(0);
				final char ch1 = str.charAt(1);
				final char ch2 = str.charAt(2);
				final char ch3 = str.charAt(3);
				final char ch4 = str.charAt(4);
				if ((ch0 == 'f' || ch0 == 'F') &&
						(ch1 == 'a' || ch1 == 'A') &&
						(ch2 == 'l' || ch2 == 'L') &&
						(ch3 == 's' || ch3 == 'S') &&
						(ch4 == 'e' || ch4 == 'E') ) {
					return Boolean.FALSE;
				}
				break;
			}
			default:
				break;
		}

		return null;
	}

	private static String strip(String str, final String stripChars) {
		if (isEmpty(str)) {
			return str;
		}
		str = stripStart(str, stripChars);
		return stripEnd(str, stripChars);
	}

	private static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	private static String stripStart(final String str, final String stripChars) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		int start = 0;
		if (stripChars == null) {
			while (start != strLen && Character.isWhitespace(str.charAt(start))) {
				start++;
			}
		} else if (stripChars.isEmpty()) {
			return str;
		} else {
			while (start != strLen && stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND) {
				start++;
			}
		}
		return str.substring(start);
	}

	private static String stripEnd(final String str, final String stripChars) {
		int end;
		if (str == null || (end = str.length()) == 0) {
			return str;
		}

		if (stripChars == null) {
			while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
				end--;
			}
		} else if (stripChars.isEmpty()) {
			return str;
		} else {
			while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND) {
				end--;
			}
		}
		return str.substring(0, end);
	}

	public static LocalDateTime parseTime(String val) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(val);
		LocalDateTime ret = LocalDateTime.of(0, 1, 1, 0, 0, 0);
		if (!matcher.find()) return ret;
		ret = ret.plusYears(Integer.valueOf(matcher.group()));
		if (!matcher.find()) return ret;
		ret = ret.plusMonths(Integer.valueOf(matcher.group()) - 1);
		if (!matcher.find()) return ret;
		ret = ret.plusDays(Integer.valueOf(matcher.group()) - 1);
		if (!matcher.find()) return ret;
		ret = ret.plusHours(Integer.valueOf(matcher.group()));
		if (!matcher.find()) return ret;
		ret = ret.plusMinutes(Integer.valueOf(matcher.group()));
		if (!matcher.find()) return ret;
		ret = ret.plusSeconds(Integer.valueOf(matcher.group()));
		return ret;
	}
}
