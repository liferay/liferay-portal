/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;

import java.text.NumberFormat;

import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class TextFormatter {

	// Web Search --> WEB_SEARCH

	public static final int A = 0;

	// Web Search --> websearch

	public static final int B = 1;

	// Web Search --> web_search

	public static final int C = 2;

	// Web Search --> WebSearch

	public static final int D = 3;

	// Web Search --> web search

	public static final int E = 4;

	// Web Search --> webSearch

	public static final int F = 5;

	// formatId --> FormatId, FriendlyURLMapper --> FriendlyURLMapper

	public static final int G = 6;

	// formatId --> format id, FriendlyURLMapper --> friendly url mapper

	public static final int H = 7;

	// FormatId --> formatId, FriendlyURLMapper --> friendlyURLMapper

	public static final int I = 8;

	// format-id --> Format Id, friendly-url-mapper --> Friendly Url Mapper

	public static final int J = 9;

	// formatId --> format-id, FriendlyURLMapper --> friendly-url-mapper

	public static final int K = 10;

	// FormatId --> formatId, FOrmatId --> FOrmatId

	public static final int L = 11;

	// format-id --> formatId, friendly-url-mapper --> friendlyUrlMapper

	public static final int M = 12;

	// format-id --> format_id, friendly-url-mapper --> friendly_url_mapper

	public static final int N = 13;

	// format_id --> format-id, friendly_url_mapper --> friendly-url-mapper

	public static final int O = 14;

	// FORMATId --> format-id

	public static final int Q = 16;

	public static String format(String s, int style) {
		if (Validator.isNull(s)) {
			return null;
		}

		s = s.trim();

		if (style == A) {
			return _formatA(s);
		}
		else if (style == B) {
			return _formatB(s);
		}
		else if (style == C) {
			return _formatC(s);
		}
		else if (style == D) {
			return _formatD(s);
		}
		else if (style == E) {
			return _formatE(s);
		}
		else if (style == F) {
			return _formatF(s);
		}
		else if (style == G) {
			return _formatG(s);
		}
		else if (style == H) {
			return _formatH(s);
		}
		else if (style == I) {
			return _formatI(s);
		}
		else if (style == J) {
			return _formatJ(s);
		}
		else if (style == K) {
			return _formatK(s);
		}
		else if (style == L) {
			return _formatL(s);
		}
		else if (style == M) {
			return _formatM(s);
		}
		else if (style == N) {
			return _formatN(s);
		}
		else if (style == O) {
			return _formatO(s);
		}
		else if (style == Q) {
			return _formatQ(s);
		}

		return s;
	}

	public static String formatName(String name) {
		if (Validator.isNull(name)) {
			return name;
		}

		String lowerCaseName = StringUtil.trim(StringUtil.toLowerCase(name));

		char[] chars = lowerCaseName.toCharArray();

		if (chars.length > 0) {
			chars[0] = Character.toUpperCase(chars[0]);
		}

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ' ') {
				chars[i + 1] = Character.toUpperCase(chars[i + 1]);
			}
		}

		return new String(chars);
	}

	public static String formatPlural(String s) {
		if (Validator.isNull(s)) {
			return s;
		}

		if (s.endsWith("ch") || s.endsWith("s") || s.endsWith("sh") ||
			s.endsWith("x") || s.endsWith("z")) {

			return s + "es";
		}

		if (s.endsWith("y") && !s.endsWith("ay") && !s.endsWith("ey") &&
			!s.endsWith("oy") && !s.endsWith("uy")) {

			return s.substring(0, s.length() - 1) + "ies";
		}

		return s + "s";
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             com.liferay.portal.kernel.language.LanguageUtil#formatStorageSize(
	 *             double, Locale) }
	 */
	@Deprecated
	public static String formatStorageSize(double size, Locale locale) {
		String suffix = _STORAGE_SIZE_SUFFIX_B;

		if (size >= _STORAGE_SIZE_DENOMINATOR) {
			suffix = _STORAGE_SIZE_SUFFIX_KB;

			size = size / _STORAGE_SIZE_DENOMINATOR;
		}

		if (size >= _STORAGE_SIZE_DENOMINATOR) {
			suffix = _STORAGE_SIZE_SUFFIX_MB;

			size = size / _STORAGE_SIZE_DENOMINATOR;
		}

		if (size >= _STORAGE_SIZE_DENOMINATOR) {
			suffix = _STORAGE_SIZE_SUFFIX_GB;

			size = size / _STORAGE_SIZE_DENOMINATOR;
		}

		NumberFormat numberFormat = NumberFormat.getInstance(locale);

		if (suffix.equals(_STORAGE_SIZE_SUFFIX_B) ||
			suffix.equals(_STORAGE_SIZE_SUFFIX_KB)) {

			numberFormat.setMaximumFractionDigits(0);
		}
		else {
			numberFormat.setMaximumFractionDigits(1);
		}

		numberFormat.setMinimumFractionDigits(0);

		return numberFormat.format(size) + StringPool.SPACE + suffix;
	}

	public static String formatStorageSize(int size, Locale locale) {
		return formatStorageSize((double)size, locale);
	}

	private static String _formatA(String s) {
		return StringUtil.replace(
			StringUtil.toUpperCase(s), CharPool.SPACE, CharPool.UNDERLINE);
	}

	private static String _formatB(String s) {
		return StringUtil.removeChar(StringUtil.toLowerCase(s), CharPool.SPACE);
	}

	private static String _formatC(String s) {
		return StringUtil.replace(
			StringUtil.toLowerCase(s), CharPool.SPACE, CharPool.UNDERLINE);
	}

	private static String _formatD(String s) {
		return StringUtil.removeChar(s, CharPool.SPACE);
	}

	private static String _formatE(String s) {
		return StringUtil.toLowerCase(s);
	}

	private static String _formatF(String s) {
		s = StringUtil.removeChar(s, CharPool.SPACE);

		if (Character.isUpperCase(s.charAt(0))) {
			String lowerCaseFirstChar = StringUtil.toLowerCase(
				s.substring(0, 1));

			s = lowerCaseFirstChar.concat(s.substring(1));
		}

		return s;
	}

	private static String _formatG(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			String upperCaseFirstChar = StringUtil.toUpperCase(
				s.substring(0, 1));

			s = upperCaseFirstChar.concat(s.substring(1));
		}

		return s;
	}

	private static String _formatH(String s) {
		StringBundler sb = new StringBundler(s.length() * 2);

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (!Character.isUpperCase(c)) {
				sb.append(c);

				continue;
			}

			if ((i > 0) &&
				(Character.isLowerCase(s.charAt(i - 1)) ||
				 ((i < (s.length() - 1)) &&
				  Character.isLowerCase(s.charAt(i + 1))))) {

				sb.append(CharPool.SPACE);
			}

			sb.append(Character.toLowerCase(c));
		}

		return sb.toString();
	}

	private static String _formatI(String s) {
		if (s.length() == 1) {
			return StringUtil.toLowerCase(s);
		}

		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		}

		if (Character.isUpperCase(s.charAt(0)) &&
			Character.isLowerCase(s.charAt(1))) {

			String lowerCaseFirstChar = StringUtil.toLowerCase(
				s.substring(0, 1));

			return lowerCaseFirstChar.concat(s.substring(1));
		}

		StringBuilder sb = new StringBuilder(s);

		for (int i = 0; i < s.length(); i++) {
			if (((i + 1) != s.length()) &&
				Character.isLowerCase(s.charAt(i + 1))) {

				break;
			}

			char c = Character.toLowerCase(s.charAt(i));

			sb.setCharAt(i, c);
		}

		return sb.toString();
	}

	private static String _formatJ(String s) {
		s = StringUtil.replace(s, CharPool.DASH, CharPool.SPACE);
		s = StringUtil.replace(s, CharPool.UNDERLINE, CharPool.SPACE);

		StringBuilder sb = new StringBuilder(StringUtil.toLowerCase(s));

		for (int i = 0; i < s.length(); i++) {
			if ((i == 0) || (s.charAt(i - 1) == ' ')) {
				sb.setCharAt(i, Character.toUpperCase(s.charAt(i)));
			}
		}

		return sb.toString();
	}

	private static String _formatK(String s) {
		s = _formatH(s);
		s = StringUtil.replace(s, CharPool.SPACE, CharPool.DASH);

		return s;
	}

	private static String _formatL(String s) {
		if (s.length() == 1) {
			return StringUtil.toLowerCase(s);
		}
		else if (Character.isLowerCase(s.charAt(0)) ||
				 (Character.isUpperCase(s.charAt(0)) &&
				  Character.isUpperCase(s.charAt(1)))) {

			return s;
		}

		String lowerCaseFirstChar = StringUtil.toLowerCase(s.substring(0, 1));

		return lowerCaseFirstChar.concat(s.substring(1));
	}

	private static String _formatM(String s) {
		StringBundler sb = new StringBundler(s.length());

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == '-') {
			}
			else if ((i > 0) && (s.charAt(i - 1) == '-')) {
				sb.append(Character.toUpperCase(c));
			}
			else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private static String _formatN(String s) {
		return StringUtil.replace(s, CharPool.DASH, CharPool.UNDERLINE);
	}

	private static String _formatO(String s) {
		return StringUtil.replace(s, CharPool.UNDERLINE, CharPool.DASH);
	}

	private static String _formatQ(String s) {
		StringBuilder sb = new StringBuilder(StringUtil.toLowerCase(s));

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (Character.isUpperCase(c) && (i > 0) && ((i + 1) < s.length())) {
				int delta = sb.length() - s.length();

				if (Character.isLowerCase(s.charAt(i + 1))) {
					sb.insert(i + delta, CharPool.DASH);
				}
				else if (Character.isLowerCase(s.charAt(i - 1))) {
					sb.insert(i + delta, CharPool.DASH);
				}
			}
		}

		return sb.toString();
	}

	private static final double _STORAGE_SIZE_DENOMINATOR = 1024.0;

	private static final String _STORAGE_SIZE_SUFFIX_B = "B";

	private static final String _STORAGE_SIZE_SUFFIX_GB = "GB";

	private static final String _STORAGE_SIZE_SUFFIX_KB = "KB";

	private static final String _STORAGE_SIZE_SUFFIX_MB = "MB";

}