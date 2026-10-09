/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.string;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Function;

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
	 * Returns <code>true</code> if the strings are equal, ignoring case.
	 *
	 * @param  s1 the first string to compare
	 * @param  s2 the second string to compare
	 * @return <code>true</code> if the strings are equal, ignoring case;
	 *         <code>false</code> otherwise
	 */
	public static boolean equalsIgnoreCase(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}

		if ((s1 == null) || (s2 == null) || (s1.length() != s2.length())) {
			return false;
		}

		for (int i = 0; i < s1.length(); i++) {
			if (!equalsIgnoreCase(s1.charAt(i), s2.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public static String merge(boolean[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(byte[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(char[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T> String merge(
		Collection<T> collection, Function<T, String> toStringFunction,
		String delimiter) {

		if (collection instanceof List && collection instanceof RandomAccess) {
			return merge(
				(List<T> & RandomAccess)collection, toStringFunction,
				delimiter);
		}

		if (collection == null) {
			return null;
		}

		if (collection.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(2 * collection.size());

		for (T t : collection) {
			sb.append(toStringFunction.apply(t));
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T> String merge(Collection<T> collection, String delimiter) {
		return merge(collection, String::valueOf, delimiter);
	}

	public static String merge(double[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(float[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(int[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T, L extends List<T> & RandomAccess> String merge(
		L list, Function<T, String> toStringFunction, String delimiter) {

		if (list == null) {
			return null;
		}

		if (list.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(2 * list.size());

		for (int i = 0; i < list.size(); i++) {
			sb.append(toStringFunction.apply(list.get(i)));
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T, L extends List<T> & RandomAccess> String merge(
		L list, String delimiter) {

		return merge(list, String::valueOf, delimiter);
	}

	public static String merge(long[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(short[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(String[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return array[0];
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T> String merge(
		T[] array, Function<T, String> toStringFunction, String delimiter) {

		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return toStringFunction.apply(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(toStringFunction.apply(array[i]));
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T> String merge(T[] array, String delimiter) {
		return merge(array, String::valueOf, delimiter);
	}

	public static String read(ClassLoader classLoader, String name)
		throws IOException {

		return read(classLoader, name, false);
	}

	public static String read(ClassLoader classLoader, String name, boolean all)
		throws IOException {

		if (all) {
			StringBundler sb = new StringBundler();

			Enumeration<URL> enumeration = classLoader.getResources(name);

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				try (InputStream inputStream = url.openStream()) {
					String s = read(inputStream);

					if (s != null) {
						sb.append(s);
						sb.append(StringPool.NEW_LINE);
					}
				}
			}

			String s = sb.toString();

			return s.trim();
		}

		try (InputStream inputStream = classLoader.getResourceAsStream(name)) {
			if (inputStream == null) {
				throw new IOException(
					StringBundler.concat(
						"Unable to open resource ", name, " in class loader ",
						classLoader));
			}

			return read(inputStream);
		}
	}

	public static String read(InputStream inputStream) throws IOException {
		String s = _read(inputStream);

		s = replace(s, "\r\n", StringPool.NEW_LINE);

		s = replace(s, CharPool.RETURN, CharPool.NEW_LINE);

		return s.trim();
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

	public static List<String> split(String s) {
		return split(s, CharPool.COMMA);
	}

	public static List<String> split(String s, char delimiter) {
		if ((s == null) || s.isEmpty()) {
			return Collections.emptyList();
		}

		s = s.trim();

		if (s.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> elements = new ArrayList<>();

		_split(elements, s, delimiter);

		return elements;
	}

	private static String _read(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[8192];
		int offset = 0;

		while (true) {
			int count = inputStream.read(
				buffer, offset, buffer.length - offset);

			if (count == -1) {
				break;
			}

			offset += count;

			if (offset != buffer.length) {
				continue;
			}

			byte[] newBuffer = new byte[buffer.length << 1];

			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);

			buffer = newBuffer;
		}

		if (offset == 0) {
			return StringPool.BLANK;
		}

		return new String(buffer, 0, offset, StringPool.UTF8);
	}

	private static void _split(List<String> values, String s, char delimiter) {
		int offset = 0;
		int pos;

		while ((pos = s.indexOf(delimiter, offset)) != -1) {
			if (offset < pos) {
				values.add(s.substring(offset, pos));
			}

			offset = pos + 1;
		}

		if (offset < s.length()) {
			values.add(s.substring(offset));
		}
	}

}