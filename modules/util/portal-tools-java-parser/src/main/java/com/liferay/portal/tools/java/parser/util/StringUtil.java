/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The String utility class.
 *
 * @author Brian Wing Shun Chan
 * @author Sandeep Soni
 * @author Ganesh Ram
 * @author Shuyang Zhou
 * @author Hugo Huijser
 */
public class StringUtil {

	public static int count(String s, char c) {
		return count(s, 0, s.length(), c);
	}

	public static int count(String s, int start, int end, char c) {
		if ((s == null) || s.isEmpty() || ((end - start) < 1)) {
			return 0;
		}

		int count = 0;

		int pos = start;

		while ((pos < end) && ((pos = s.indexOf(c, pos)) != -1)) {
			if (pos < end) {
				count++;
			}

			pos++;
		}

		return count;
	}

	public static int count(String s, int start, int end, String text) {
		if ((s == null) || s.isEmpty() || ((end - start) < 1) ||
			(text == null) || text.isEmpty()) {

			return 0;
		}

		int count = 0;

		int pos = start;

		while ((pos < end) && ((pos = s.indexOf(text, pos)) != -1)) {
			if (pos < end) {
				count++;
			}

			pos += text.length();
		}

		return count;
	}

	/**
	 * Returns the number of times the text appears in the string.
	 *
	 * @param  s the string in which to search
	 * @param  text the text to search for in the string
	 * @return the number of times the text appears in the string
	 */
	public static int count(String s, String text) {
		return count(s, 0, s.length(), text);
	}

	/**
	 * Returns <code>true</code> if the string ends with the specified
	 * character, ignoring case.
	 *
	 * @param  s the string in which to search
	 * @param  end the character to search for at the end of the string
	 * @return <code>true</code> if the string ends with the specified
	 *         character, ignoring case; <code>false</code> otherwise
	 */
	public static boolean endsWith(String s, char end) {
		if ((s == null) || s.isEmpty()) {
			return false;
		}

		return equalsIgnoreCase(s.charAt(s.length() - 1), end);
	}

	public static boolean equalsIgnoreCase(char c1, char c2) {
		if (c1 == c2) {
			return true;
		}

		// Fast fallback for non-acsii code.

		if ((c1 > 127) || (c2 > 127)) {

			// Georgian alphabet needs to check both upper and lower case

			if ((Character.toLowerCase(c1) == Character.toLowerCase(c2)) ||
				(Character.toUpperCase(c1) == Character.toUpperCase(c2))) {

				return true;
			}

			return false;
		}

		// Fast fallback for non-letter ascii code

		if ((c1 < CharPool.UPPER_CASE_A) || (c1 > CharPool.LOWER_CASE_Z) ||
			(c2 < CharPool.UPPER_CASE_A) || (c2 > CharPool.LOWER_CASE_Z)) {

			return false;
		}

		int delta = c1 - c2;

		if ((delta != 32) && (delta != -32)) {
			return false;
		}

		return true;
	}

	/**
	 * Inserts one string into the other at the specified offset index.
	 *
	 * @param  s the original string
	 * @param  insert the string to be inserted into the original string
	 * @param  offset the index of the original string where the insertion
	 *         should take place
	 * @return a string representing the original string with the other string
	 *         inserted at the specified offset index, or <code>null</code> if
	 *         the original string is <code>null</code>
	 */
	public static String insert(String s, String insert, int offset) {
		if (s == null) {
			return null;
		}

		if (insert == null) {
			return s;
		}

		if (offset > s.length()) {
			return s.concat(insert);
		}

		String prefix = s.substring(0, offset);
		String postfix = s.substring(offset);

		return StringBundler.concat(prefix, insert, postfix);
	}

	/**
	 * Returns <code>true</code> if all the characters in string <code>s</code>
	 * are lower case, ignoring any non-alphabetic characters.
	 *
	 * @param  s the string in which to search
	 * @return <code>true</code> if all the characters in string <code>s</code>
	 *         are lower case, ignoring any non-alphabetic characters;
	 *         <code>false</code> otherwise
	 */
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

	/**
	 * Returns <code>true</code> if all the characters in string <code>s</code>
	 * are upper case, ignoring any non-alphabetic characters.
	 *
	 * @param  s the string in which to search
	 * @return <code>true</code> if all the characters in string <code>s</code>
	 *         are upper case, ignoring any non-alphabetic characters;
	 *         <code>false</code> otherwise
	 */
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

	public static String removeChar(String s, char oldSub) {
		if (s == null) {
			return null;
		}

		int y = s.indexOf(oldSub);

		if (y >= 0) {
			StringBundler sb = new StringBundler();

			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));

				x = y + 1;

				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));

			return sb.toString();
		}

		return s;
	}

	/**
	 * Replaces all occurrences of the character with the new character.
	 *
	 * @param  s the original string
	 * @param  oldSub the character to be searched for and replaced in the
	 *         original string
	 * @param  newSub the character with which to replace the
	 *         <code>oldSub</code> character
	 * @return a string representing the original string with all occurrences of
	 *         the <code>oldSub</code> character replaced with the
	 *         <code>newSub</code> character, or <code>null</code> if the
	 *         original string is <code>null</code>
	 */
	public static String replace(String s, char oldSub, char newSub) {
		if (s == null) {
			return null;
		}

		return s.replace(oldSub, newSub);
	}

	/**
	 * Replaces all occurrences of the string with the new string.
	 *
	 * @param  s the original string
	 * @param  oldSub the string to be searched for and replaced in the original
	 *         string
	 * @param  newSub the string with which to replace the <code>oldSub</code>
	 *         string
	 * @return a string representing the original string with all occurrences of
	 *         the <code>oldSub</code> string replaced with the string
	 *         <code>newSub</code>, or <code>null</code> if the original string
	 *         is <code>null</code>
	 */
	public static String replace(String s, String oldSub, String newSub) {
		return replace(s, oldSub, newSub, 0);
	}

	/**
	 * Replaces all occurrences of the string with the new string, starting from
	 * the specified index.
	 *
	 * @param  s the original string
	 * @param  oldSub the string to be searched for and replaced in the original
	 *         string
	 * @param  newSub the string with which to replace the <code>oldSub</code>
	 *         string
	 * @param  fromIndex the index of the original string from which to begin
	 *         searching
	 * @return a string representing the original string with all occurrences of
	 *         the <code>oldSub</code> string occurring after the specified
	 *         index replaced with the string <code>newSub</code>, or
	 *         <code>null</code> if the original string is <code>null</code>
	 */
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
			StringBundler sb = new StringBundler();

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

	/**
	 * Replaces the first occurrence of the character with the new string.
	 *
	 * @param  s the original string
	 * @param  oldSub the character whose first occurrence in the original
	 *         string is to be searched for and replaced
	 * @param  newSub the string with which to replace the first occurrence of
	 *         the <code>oldSub</code> character
	 * @return a string representing the original string except with the first
	 *         occurrence of the character <code>oldSub</code> replaced with the
	 *         string <code>newSub</code>
	 */
	public static String replaceFirst(String s, char oldSub, String newSub) {
		if ((s == null) || (newSub == null)) {
			return null;
		}

		return replaceFirst(s, String.valueOf(oldSub), newSub);
	}

	/**
	 * Replaces the first occurrence of the string with the new string.
	 *
	 * @param  s the original string
	 * @param  oldSub the string whose first occurrence in the original string
	 *         is to be searched for and replaced
	 * @param  newSub the string with which to replace the first occurrence of
	 *         the <code>oldSub</code> string
	 * @return a string representing the original string except with the first
	 *         occurrence of the string <code>oldSub</code> replaced with the
	 *         string <code>newSub</code>
	 */
	public static String replaceFirst(String s, String oldSub, String newSub) {
		return replaceFirst(s, oldSub, newSub, 0);
	}

	/**
	 * Replaces the first occurrences of the elements of the string array with
	 * the corresponding elements of the new string array, beginning the element
	 * search from the index position.
	 *
	 * @param  s the original string
	 * @param  oldSub the strings whose first occurrences are to be searched for
	 *         and replaced in the original string
	 * @param  newSub the strings with which to replace the first occurrences of
	 *         the <code>oldSubs</code> strings
	 * @param  fromIndex the start index within the string
	 * @return a string representing the original string with the first
	 *         occurrences of the <code>oldSubs</code> strings replaced with the
	 *         corresponding <code>newSubs</code> strings, or <code>null</code>
	 *         if the original string, the <code>oldSubs</code> string, or the
	 *         <code>newSubs</code> string is <code>null</code>
	 */
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
			return StringBundler.concat(
				s.substring(0, y), newSub, s.substring(y + oldSub.length()));
		}

		return s;
	}

	/**
	 * Replaces the last occurrence of the string <code>oldSub</code> in the
	 * string <code>s</code> with the string <code>newSub</code>.
	 *
	 * @param  s the original string
	 * @param  oldSub the string whose last occurrence in the original string is
	 *         to be searched for and replaced
	 * @param  newSub the string with which to replace the last occurrence of
	 *         the <code>oldSub</code> string
	 * @return a string representing the original string except with the last
	 *         occurrence of the string <code>oldSub</code> replaced with the
	 *         string <code>newSub</code>
	 */
	public static String replaceLast(String s, String oldSub, String newSub) {
		if ((s == null) || (oldSub == null) || (newSub == null)) {
			return null;
		}

		if (oldSub.equals(newSub)) {
			return s;
		}

		int y = s.lastIndexOf(oldSub);

		if (y >= 0) {
			return StringBundler.concat(
				s.substring(0, y), newSub, s.substring(y + oldSub.length()));
		}

		return s;
	}

	/**
	 * Splits string <code>s</code> around return and newline characters.
	 *
	 * <p>
	 * Example:
	 * </p>
	 *
	 * <p>
	 * <pre>
	 * <code>
	 * splitLines("Red\rBlue\nGreen") returns {"Red","Blue","Green"}
	 * </code>
	 * </pre></p>
	 *
	 * @param  s the string to split
	 * @return the array of strings resulting from splitting string
	 *         <code>s</code> around return and newline characters, or an empty
	 *         string array if string <code>s</code> is <code>null</code>
	 */
	public static String[] splitLines(String s) {
		if (Validator.isNull(s)) {
			return _EMPTY_STRING_ARRAY;
		}

		s = s.trim();

		List<String> lines = new ArrayList<>();

		_splitLines(s, lines);

		return lines.toArray(new String[0]);
	}

	/**
	 * Returns <code>true</code> if, ignoring case, the string starts with the
	 * specified character.
	 *
	 * @param  s the string
	 * @param  begin the character against which the initial character of the
	 *         string is to be compared
	 * @return <code>true</code> if, ignoring case, the string starts with the
	 *         specified character; <code>false</code> otherwise
	 */
	public static boolean startsWith(String s, char begin) {
		if ((s == null) || s.isEmpty()) {
			return false;
		}

		return equalsIgnoreCase(s.charAt(0), begin);
	}

	/**
	 * Returns <code>true</code> if, ignoring case, the string starts with the
	 * specified start string.
	 *
	 * @param  s the original string
	 * @param  start the string against which the beginning of string
	 *         <code>s</code> are to be compared
	 * @return <code>true</code> if, ignoring case, the string starts with the
	 *         specified start string; <code>false</code> otherwise
	 */
	public static boolean startsWith(String s, String start) {
		if ((s == null) || (start == null) || (start.length() > s.length())) {
			return false;
		}

		for (int i = 0; i < start.length(); i++) {
			if (!equalsIgnoreCase(s.charAt(i), start.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Trims all leading and trailing whitespace from the string.
	 *
	 * @param  s the original string
	 * @return a string representing the original string with all leading and
	 *         trailing whitespace removed
	 */
	public static String trim(String s) {
		if (s == null) {
			return null;
		}

		int len = s.length();

		if (len == 0) {
			return s;
		}

		int x = 0;

		while (x < len) {
			char c = s.charAt(x);

			if (((c > CharPool.SPACE) && (c < 128)) ||
				!Character.isWhitespace(c)) {

				break;
			}

			x++;
		}

		if (x == len) {
			return StringPool.BLANK;
		}

		int y = len - 1;

		while (x < y) {
			char c = s.charAt(y);

			if (((c > CharPool.SPACE) && (c < 128)) ||
				!Character.isWhitespace(c)) {

				break;
			}

			y--;
		}

		y++;

		if ((x > 0) || (y < len)) {
			return s.substring(x, y);
		}

		return s;
	}

	/**
	 * Trims all leading whitespace from the string.
	 *
	 * @param  s the original string
	 * @return a string representing the original string with all leading
	 *         whitespace removed
	 */
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

	/**
	 * Trims all trailing whitespace from the string.
	 *
	 * @param  s the original string
	 * @return a string representing the original string with all trailing
	 *         whitespace removed
	 */
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

	private static void _split(
		Collection<String> values, String s, int offset, char delimiter) {

		int pos = s.indexOf(delimiter, offset);

		while (pos != -1) {
			values.add(s.substring(offset, pos));

			offset = pos + 1;

			pos = s.indexOf(delimiter, offset);
		}

		if (offset < s.length()) {
			values.add(s.substring(offset));
		}
	}

	private static void _splitLines(String s, Collection<String> lines) {
		int lastIndex = 0;

		while (true) {
			int returnIndex = s.indexOf(CharPool.RETURN, lastIndex);

			if (returnIndex == -1) {
				_split(lines, s, lastIndex, CharPool.NEW_LINE);

				return;
			}

			int newLineIndex = s.indexOf(CharPool.NEW_LINE, lastIndex);

			if (newLineIndex == -1) {
				_split(lines, s, lastIndex, CharPool.RETURN);

				return;
			}

			if (newLineIndex < returnIndex) {
				lines.add(s.substring(lastIndex, newLineIndex));

				lastIndex = newLineIndex + 1;
			}
			else {
				lines.add(s.substring(lastIndex, returnIndex));

				lastIndex = returnIndex + 1;

				if (lastIndex == newLineIndex) {
					lastIndex++;
				}
			}
		}
	}

	private static final String[] _EMPTY_STRING_ARRAY = new String[0];

}