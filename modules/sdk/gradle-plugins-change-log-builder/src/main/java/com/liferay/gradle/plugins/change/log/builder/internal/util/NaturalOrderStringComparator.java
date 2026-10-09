/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.change.log.builder.internal.util;

import com.liferay.gradle.util.Validator;

import java.util.Comparator;

/**
 * @author Hugo Huijser
 * @author Andrea Di Giorgi
 */
public class NaturalOrderStringComparator implements Comparator<String> {

	public NaturalOrderStringComparator() {
		this(true, false);
	}

	public NaturalOrderStringComparator(
		boolean ascending, boolean caseSensitive) {

		_ascending = ascending;
		_caseSensitive = caseSensitive;
	}

	@Override
	public int compare(String s1, String s2) {
		if (s1 == null) {
			s1 = "";
		}

		if (s2 == null) {
			s2 = "";
		}

		int value = 0;

		int i1 = 0;
		int i2 = 0;

		int length1 = s1.length();
		int length2 = s2.length();

		while ((i1 < length1) && (i2 < length2)) {
			char c1 = s1.charAt(i1);
			char c2 = s2.charAt(i2);

			if (Character.isDigit(c1) && Character.isDigit(c2)) {
				String leadingDigitsAsString1 = _extractLeadingDigits(
					s1.substring(i1));
				String leadingDigitsAsString2 = _extractLeadingDigits(
					s2.substring(i2));

				int leadingNumber1 = Integer.parseInt(leadingDigitsAsString1);
				int leadingNumber2 = Integer.parseInt(leadingDigitsAsString2);

				if (leadingNumber1 != leadingNumber2) {
					value = leadingNumber1 - leadingNumber2;

					break;
				}

				i1 += leadingDigitsAsString1.length();
				i2 += leadingDigitsAsString2.length();

				continue;
			}

			if (c1 == c2) {
				i1++;
				i2++;

				continue;
			}

			if (_caseSensitive) {
				value = c1 - c2;

				break;
			}

			char c1UpperCase = Character.toUpperCase(c1);
			char c2UpperCase = Character.toUpperCase(c2);

			if (c1UpperCase == c2UpperCase) {
				i1++;
				i2++;

				continue;
			}

			value = c1UpperCase - c2UpperCase;

			break;
		}

		if ((value == 0) && (length1 != length2)) {
			if ((length1 == i1) && (length2 == i2)) {
				value = length2 - length1;
			}
			else {
				value = length1 - length2;
			}
		}

		if (_ascending) {
			return value;
		}

		return -value;
	}

	private String _extractLeadingDigits(String s) {
		if (Validator.isNull(s)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		char[] chars = s.toCharArray();

		for (char c : chars) {
			if (!Character.isDigit(c)) {
				return sb.toString();
			}

			sb.append(c);
		}

		return sb.toString();
	}

	private final boolean _ascending;
	private final boolean _caseSensitive;

}