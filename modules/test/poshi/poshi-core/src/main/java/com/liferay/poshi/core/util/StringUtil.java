/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * The String utility class.
 *
 * @author Brian Wing Shun Chan
 * @author Sandeep Soni
 * @author Ganesh Ram
 * @author Shuyang Zhou
 * @author Hugo Huijser
 * @author Michael Hashimoto
 */
public class StringUtil {

	public static String add(String s, String add) {
		return add(s, add, StringPool.COMMA);
	}

	public static String add(String s, String add, String delimiter) {
		return add(s, add, delimiter, false);
	}

	public static String add(
		String s, String add, String delimiter, boolean allowDuplicates) {

		if ((add == null) || (delimiter == null)) {
			return null;
		}

		if (s == null) {
			s = StringPool.BLANK;
		}

		if (allowDuplicates || !contains(s, add, delimiter)) {
			StringBuilder sb = new StringBuilder(4);

			sb.append(s);

			if (Validator.isNull(s) || s.endsWith(delimiter)) {
				sb.append(add);
				sb.append(delimiter);
			}
			else {
				sb.append(delimiter);
				sb.append(add);
				sb.append(delimiter);
			}

			s = sb.toString();
		}

		return s;
	}

	public static void assertContains(String expectedText, String actualText) {
		if (!contains(expectedText, actualText)) {
			throw new RuntimeException(
				"Expected text \"" + expectedText +
					"\" does not contain atual text \"" + actualText + "\"");
		}
	}

	public static void assertEquals(String expectedText, String actualText) {
		if (!equals(expectedText, actualText)) {
			throw new RuntimeException(
				"Expected text \"" + expectedText +
					"\" does not equal actual text \"" + actualText + "\"");
		}
	}

	public static String capitalize(String s) {
		if ((s == null) || s.isEmpty()) {
			return "";
		}

		char firstChar = s.charAt(0);

		if (Character.isLowerCase(firstChar)) {
			s = Character.toUpperCase(firstChar) + s.substring(1);
		}

		return s;
	}

	public static String center(String s, String size) {
		return StringUtils.center(s, GetterUtil.getInteger(size));
	}

	public static String combine(String... strings) {
		if (ArrayUtil.isEmpty(strings)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (String string : strings) {
			sb.append(string);
		}

		return sb.toString();
	}

	public static int compareTo(String s, String comparison) {
		return s.compareTo(comparison);
	}

	public static String concat(String s, String text) {
		return s.concat(text);
	}

	public static boolean contains(String s, String text) {
		return contains(s, text, StringPool.COMMA);
	}

	public static boolean contains(String s, String text, String delimiter) {
		if ((s == null) || (text == null) || (delimiter == null)) {
			return false;
		}

		if (!s.endsWith(delimiter)) {
			s = s.concat(delimiter);
		}

		String dtd = combine(delimiter, text, delimiter);

		int pos = s.indexOf(dtd);

		if (pos == -1) {
			String td = text.concat(delimiter);

			return s.startsWith(td);
		}

		return true;
	}

	public static boolean contentEquals(String s, String charSequence) {
		return s.contentEquals(charSequence);
	}

	public static int count(String s, String text) {
		return count(s, text, s.length());
	}

	public static int count(String s, String text, int index) {
		if ((s == null) || (s.length() == 0) || (text == null) ||
			(text.length() == 0)) {

			return 0;
		}

		int count = 0;

		int pos = s.indexOf(text);

		while ((pos != -1) && (pos < index)) {
			pos = s.indexOf(text, pos + text.length());

			count++;
		}

		return count;
	}

	public static int countStartingNewLines(String s) {
		String[] snippets = s.split("\n\\h*", -1);

		if (snippets.length == 1) {
			return 0;
		}

		for (int i = 0; i < snippets.length; i++) {
			String snippet = snippets[i];

			if (snippet.isEmpty()) {
				continue;
			}

			return i;
		}

		return snippets.length - 1;
	}

	public static boolean endsWith(String s, String end) {
		if ((s == null) || (end == null) || (end.length() > s.length())) {
			return false;
		}

		String temp = s.substring(s.length() - end.length());

		return equalsIgnoreCase(temp, end);
	}

	public static boolean equals(String s, String text) {
		return s.equals(text);
	}

	public static boolean equalsIgnoreCase(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}

		if ((s1 == null) || (s2 == null) || (s1.length() != s2.length())) {
			return false;
		}

		for (int i = 0; i < s1.length(); i++) {
			char c1 = s1.charAt(i);

			char c2 = s2.charAt(i);

			if (c1 == c2) {
				continue;
			}

			if ((c1 > 127) || (c2 > 127)) {

				// Georgian alphabet needs to check both upper and lower case

				if ((Character.toLowerCase(c1) == Character.toLowerCase(c2)) ||
					(Character.toUpperCase(c1) == Character.toUpperCase(c2))) {

					continue;
				}

				return false;
			}

			int delta = c1 - c2;

			if ((delta != 32) && (delta != -32)) {
				return false;
			}
		}

		return true;
	}

	public static String extractChars(String s) {
		if (s == null) {
			return StringPool.BLANK;
		}

		StringBuilder sb = new StringBuilder();

		char[] chars = s.toCharArray();

		for (char c : chars) {
			if (!Validator.isChar(c)) {
				continue;
			}

			sb.append(c);
		}

		return sb.toString();
	}

	public static String extractDigits(String s) {
		if (s == null) {
			return StringPool.BLANK;
		}

		StringBuilder sb = new StringBuilder();

		char[] chars = s.toCharArray();

		for (char c : chars) {
			if (!Validator.isDigit(c)) {
				continue;
			}

			sb.append(c);
		}

		return sb.toString();
	}

	public static String extractFirst(String s, String delimiter) {
		if (s == null) {
			return null;
		}

		int index = s.indexOf(delimiter);

		if (index < 0) {
			return null;
		}

		return s.substring(0, index);
	}

	public static String extractLast(String s, String delimiter) {
		if (s == null) {
			return null;
		}

		int index = s.lastIndexOf(delimiter);

		if (index < 0) {
			return null;
		}

		return s.substring(index + delimiter.length());
	}

	public static String extractLeadingDigits(String s) {
		if (s == null) {
			return StringPool.BLANK;
		}

		StringBuilder sb = new StringBuilder();

		char[] chars = s.toCharArray();

		for (char c : chars) {
			if (!Validator.isDigit(c)) {
				return sb.toString();
			}

			sb.append(c);
		}

		return sb.toString();
	}

	public static String format(String format, String s) {
		try {
			return String.format(format, s);
		}
		catch (IllegalFormatConversionException
					illegalFormatConversionException) {

			System.out.println(
				"Please use a format specifier that utilizes a string");

			throw illegalFormatConversionException;
		}
	}

	public static boolean isEmpty(String s) {
		return s.isEmpty();
	}

	public static boolean isLowerCase(String s) {
		if (s == null) {
			return false;
		}

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			// Fast path for ascii code, fallback to the slow unicode detection

			if (c <= 127) {
				if ((c >= CharPool.UPPER_CASE_A) &&
					(c <= CharPool.UPPER_CASE_Z)) {

					return false;
				}

				continue;
			}

			if (Character.isLetter(c) && Character.isUpperCase(c)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isUpperCase(String s) {
		if (s == null) {
			return false;
		}

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			// Fast path for ascii code, fallback to the slow unicode detection

			if (c <= 127) {
				if ((c >= CharPool.LOWER_CASE_A) &&
					(c <= CharPool.LOWER_CASE_Z)) {

					return false;
				}

				continue;
			}

			if (Character.isLetter(c) && Character.isLowerCase(c)) {
				return false;
			}
		}

		return true;
	}

	public static String join(String[] array, String delimiter) {
		StringBuilder sb = new StringBuilder();

		boolean start = true;

		for (String string : array) {
			if (!start) {
				sb.append(delimiter);
			}

			sb.append(string);

			start = false;
		}

		return sb.toString();
	}

	public static int length(String s) {
		return s.length();
	}

	public static String lowerCase(String s) {
		return toLowerCase(s);
	}

	public static void lowerCase(String... array) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				array[i] = toLowerCase(array[i]);
			}
		}
	}

	public static String lowerCaseFirstLetter(String s) {
		char[] chars = s.toCharArray();

		if ((chars[0] >= 65) && (chars[0] <= 90)) {
			chars[0] = (char)(chars[0] + 32);
		}

		return new String(chars);
	}

	public static boolean matches(String s, String pattern) {
		return s.matches(pattern);
	}

	public static List<String> partition(String s, String[] delimiters) {
		List<Integer> delimiterIndexes = new ArrayList<>();

		for (String delimiter : delimiters) {
			int index = s.indexOf(delimiter);

			while (index >= 0) {
				delimiterIndexes.add(index);

				index = s.indexOf(delimiter, index + 1);
			}
		}

		if (!delimiterIndexes.contains(0)) {
			delimiterIndexes.add(0);
		}

		if (!delimiterIndexes.contains(s.length())) {
			delimiterIndexes.add(s.length());
		}

		Collections.sort(delimiterIndexes);

		List<String> substrings = new ArrayList<>();

		for (int i = 0; i < delimiterIndexes.size(); i++) {
			if ((i + 1) == delimiterIndexes.size()) {
				continue;
			}

			String substring = s.substring(
				delimiterIndexes.get(i), delimiterIndexes.get(i + 1));

			substrings.add(substring);
		}

		return substrings;
	}

	public static String quote(String s, String quote) {
		if (s == null) {
			return null;
		}

		return combine(quote, s, quote);
	}

	public static String randomString(String lengthString) {
		int length = GetterUtil.getInteger(lengthString);

		Random random = new Random();

		char[] chars = new char[length];

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(_RANDOM_STRING_CHAR_TABLE.length);

			chars[i] = _RANDOM_STRING_CHAR_TABLE[index];
		}

		return new String(chars);
	}

	public static String regexReplaceAll(
		String s, String regex, String replacement) {

		return s.replaceAll(regex, replacement);
	}

	public static String regexReplaceFirst(
		String s, String regex, String replacement) {

		return s.replaceFirst(regex, replacement);
	}

	public static String removeSpaces(String s) {
		return s.replaceAll(" ", "");
	}

	public static String replace(String s, char oldSub, char newSub) {
		if (s == null) {
			return null;
		}

		return s.replace(oldSub, newSub);
	}

	public static String replace(String s, char oldSub, String newSub) {
		if ((s == null) || (newSub == null)) {
			return null;
		}

		int index = s.indexOf(oldSub);

		if (index == -1) {
			return s;
		}

		int previousIndex = index;

		StringBuilder sb = new StringBuilder();

		if (previousIndex != 0) {
			sb.append(s.substring(0, previousIndex));
		}

		sb.append(newSub);

		while ((index = s.indexOf(oldSub, previousIndex + 1)) != -1) {
			sb.append(s.substring(previousIndex + 1, index));
			sb.append(newSub);

			previousIndex = index;
		}

		index = previousIndex + 1;

		if (index < s.length()) {
			sb.append(s.substring(index));
		}

		return sb.toString();
	}

	public static String replace(String s, char[] oldSubs, char[] newSubs) {
		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		StringBuilder sb = new StringBuilder(s.length());

		sb.append(s);

		boolean modified = false;

		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);

			for (int j = 0; j < oldSubs.length; j++) {
				if (c == oldSubs[j]) {
					sb.setCharAt(i, newSubs[j]);

					modified = true;

					break;
				}
			}
		}

		if (modified) {
			return sb.toString();
		}

		return s;
	}

	public static String replace(String s, char[] oldSubs, String[] newSubs) {
		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		StringBuilder sb = null;

		int lastReplacementIndex = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			for (int j = 0; j < oldSubs.length; j++) {
				if (c == oldSubs[j]) {
					if (sb == null) {
						sb = new StringBuilder();
					}

					if (i > lastReplacementIndex) {
						sb.append(s.substring(lastReplacementIndex, i));
					}

					sb.append(newSubs[j]);

					lastReplacementIndex = i + 1;

					break;
				}
			}
		}

		if (sb == null) {
			return s;
		}

		if (lastReplacementIndex < s.length()) {
			sb.append(s.substring(lastReplacementIndex));
		}

		return sb.toString();
	}

	public static String replace(String s, String oldSub, String newSub) {
		return replace(s, oldSub, newSub, 0);
	}

	public static String replace(
		String s, String oldSub, String newSub, int fromIndex) {

		if (s == null) {
			return null;
		}

		if ((oldSub == null) || oldSub.equals(StringPool.BLANK)) {
			return s;
		}

		if (newSub == null) {
			newSub = StringPool.BLANK;
		}

		int y = s.indexOf(oldSub, fromIndex);

		if (y >= 0) {
			StringBuilder sb = new StringBuilder();

			int length = oldSub.length();
			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));
				sb.append(newSub);

				x = y + length;

				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));

			return sb.toString();
		}

		return s;
	}

	public static String replace(String s, String[] oldSubs, String[] newSubs) {
		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		for (int i = 0; i < oldSubs.length; i++) {
			s = replace(s, oldSubs[i], newSubs[i]);
		}

		return s;
	}

	public static String replace(
		String s, String[] oldSubs, String[] newSubs, boolean exactMatch) {

		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		if (!exactMatch) {
			return replace(s, oldSubs, newSubs);
		}

		for (int i = 0; i < oldSubs.length; i++) {
			s = s.replaceAll("\\b" + oldSubs[i] + "\\b", newSubs[i]);
		}

		return s;
	}

	public static String replaceFirst(String s, char oldSub, char newSub) {
		if (s == null) {
			return null;
		}

		return replaceFirst(s, String.valueOf(oldSub), String.valueOf(newSub));
	}

	public static String replaceFirst(String s, char oldSub, String newSub) {
		if ((s == null) || (newSub == null)) {
			return null;
		}

		return replaceFirst(s, String.valueOf(oldSub), newSub);
	}

	public static String replaceFirst(String s, String oldSub, String newSub) {
		return replaceFirst(s, oldSub, newSub, 0);
	}

	public static String replaceFirst(
		String s, String oldSub, String newSub, int fromIndex) {

		if ((s == null) || (oldSub == null) || (newSub == null)) {
			return null;
		}

		if (oldSub.equals(newSub)) {
			return s;
		}

		int y = s.indexOf(oldSub, fromIndex);

		if (y >= 0) {
			return combine(
				s.substring(0, y), newSub, s.substring(y + oldSub.length()));
		}

		return s;
	}

	public static String replaceFirst(
		String s, String[] oldSubs, String[] newSubs) {

		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		for (int i = 0; i < oldSubs.length; i++) {
			s = replaceFirst(s, oldSubs[i], newSubs[i]);
		}

		return s;
	}

	public static String replacePropertyVariables(String s) {
		Properties properties = PropsUtil.getProperties();

		for (Object object : properties.keySet()) {
			String propertyName = (String)object;

			System.out.println(propertyName);

			s = s.replace(
				"${" + propertyName + "}", PropsUtil.get(propertyName));
		}

		return s;
	}

	public static String reverse(String s) {
		if (s == null) {
			return null;
		}

		char[] chars = s.toCharArray();

		char[] reverse = new char[chars.length];

		for (int i = 0; i < chars.length; i++) {
			reverse[i] = chars[chars.length - i - 1];
		}

		return new String(reverse);
	}

	public static String[] split(String s) {
		return split(s, StringPool.COMMA);
	}

	public static String[] split(String s, String delimiter) {
		if (s == null) {
			return null;
		}

		return s.split(delimiter);
	}

	public static List<String> split(String s, String[] delimiters) {
		Set<Integer> splitIndexSet = new HashSet<>();

		splitIndexSet.add(0);
		splitIndexSet.add(s.length());

		List<String> delimiterList = Arrays.asList(
			_removeDuplicates(delimiters));

		for (String delimiter : delimiterList) {
			int index = s.indexOf(delimiter);

			while (index >= 0) {
				splitIndexSet.add(index);

				index = s.indexOf(delimiter, index + 1);
			}
		}

		Integer[] splitIndexArray = splitIndexSet.toArray(new Integer[0]);

		Arrays.sort(splitIndexArray);

		List<String> substrings = new ArrayList<>();

		for (int i = 0; i < splitIndexArray.length; i++) {
			if ((i + 1) == splitIndexArray.length) {
				continue;
			}

			String substring = s.substring(
				splitIndexArray[i], splitIndexArray[i + 1]);

			substring = substring.trim();

			if (delimiterList.contains(substring)) {
				continue;
			}

			substrings.add(substring);
		}

		return substrings;
	}

	public static boolean startsWith(String s, String start) {
		if ((s == null) || (start == null) || (start.length() > s.length())) {
			return false;
		}

		String temp = s.substring(0, start.length());

		return equalsIgnoreCase(temp, start);
	}

	public static String stripBetween(String s, String begin, String end) {
		if (Validator.isBlank(s) || Validator.isBlank(begin) ||
			Validator.isBlank(end)) {

			return s;
		}

		StringBuilder sb = new StringBuilder(s.length());

		int pos = 0;

		while (true) {
			int x = s.indexOf(begin, pos);

			int y = s.indexOf(end, x + begin.length());

			if ((x == -1) || (y == -1)) {
				sb.append(s.substring(pos));

				break;
			}

			sb.append(s.substring(pos, x));

			pos = y + end.length();
		}

		return sb.toString();
	}

	public static String substring(
		String s, String startIndex, String endIndex) {

		if (s == null) {
			return StringPool.BLANK;
		}

		try {
			return s.substring(
				Integer.parseInt(startIndex), Integer.parseInt(endIndex));
		}
		catch (IndexOutOfBoundsException | NumberFormatException exception) {
			exception.printStackTrace();

			return s;
		}
	}

	public static String toLowerCase(String s) {
		if (s == null) {
			return null;
		}

		return StringUtils.lowerCase(s);
	}

	public static String toUpperCase(String s) {
		if (s == null) {
			return null;
		}

		return StringUtils.upperCase(s);
	}

	public static String trim(String s) {
		if (s == null) {
			return null;
		}

		if (s.length() == 0) {
			return s;
		}

		int len = s.length();

		int x = len;

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);

			if (!Character.isWhitespace(c)) {
				x = i;

				break;
			}
		}

		if (x == len) {
			return StringPool.BLANK;
		}

		int y = x + 1;

		for (int i = len - 1; i > x; i--) {
			char c = s.charAt(i);

			if (!Character.isWhitespace(c)) {
				y = i + 1;

				break;
			}
		}

		if ((x == 0) && (y == len)) {
			return s;
		}

		return s.substring(x, y);
	}

	public static String trimLeading(String s) {
		if (s == null) {
			return null;
		}

		if (s.length() == 0) {
			return s;
		}

		int len = s.length();

		int x = len;

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);

			if (!Character.isWhitespace(c)) {
				x = i;

				break;
			}
		}

		if (x == len) {
			return StringPool.BLANK;
		}
		else if (x == 0) {
			return s;
		}

		return s.substring(x);
	}

	public static String trimTrailing(String s) {
		if (s == null) {
			return null;
		}

		if (s.length() == 0) {
			return s;
		}

		int len = s.length();
		int x = 0;

		for (int i = len - 1; i >= 0; i--) {
			char c = s.charAt(i);

			if (!Character.isWhitespace(c)) {
				x = i + 1;

				break;
			}
		}

		if (x == 0) {
			return StringPool.BLANK;
		}
		else if (x == len) {
			return s;
		}

		return s.substring(0, x);
	}

	public static String unquote(String s) {
		if (Validator.isNull(s)) {
			return s;
		}

		if ((s.charAt(0) == CharPool.APOSTROPHE) &&
			(s.charAt(s.length() - 1) == CharPool.APOSTROPHE)) {

			return s.substring(1, s.length() - 1);
		}
		else if ((s.charAt(0) == CharPool.QUOTE) &&
				 (s.charAt(s.length() - 1) == CharPool.QUOTE)) {

			return s.substring(1, s.length() - 1);
		}

		return s;
	}

	public static String upperCase(String s) {
		return toUpperCase(s);
	}

	public static String upperCaseFirstLetter(String s) {
		if ((s == null) || s.isEmpty()) {
			return s;
		}

		char[] chars = s.toCharArray();

		if ((chars[0] >= 97) && (chars[0] <= 122)) {
			chars[0] = (char)(chars[0] - 32);
		}

		return new String(chars);
	}

	public static String valueOf(Object object) {
		return String.valueOf(object);
	}

	private static String[] _removeDuplicates(String[] stringArray) {
		List<String> stringList = new ArrayList<>();

		for (String string : stringArray) {
			if (stringList.contains(string)) {
				continue;
			}

			stringList.add(string);
		}

		return stringList.toArray(new String[0]);
	}

	private static final char[] _RANDOM_STRING_CHAR_TABLE = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
		'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z'
	};

}