/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;

import java.io.Serializable;

import java.text.Collator;

import java.util.Comparator;

/**
 * @author Hugo Huijser
 */
public class NaturalOrderStringComparator
	implements Comparator<String>, Serializable {

	public NaturalOrderStringComparator() {
		this(true, false);
	}

	public NaturalOrderStringComparator(
		boolean ascending, boolean caseSensitive) {

		this(ascending, caseSensitive, null);
	}

	public NaturalOrderStringComparator(
		boolean ascending, boolean caseSensitive, Collator collator) {

		_ascending = ascending;
		_caseSensitive = caseSensitive;
		_collator = collator;
	}

	@Override
	public int compare(String s1, String s2) {
		if (s1 == null) {
			s1 = StringPool.BLANK;
		}

		if (s2 == null) {
			s2 = StringPool.BLANK;
		}

		int value = 0;

		int i1 = 0;
		int i2 = 0;

		int length1 = s1.length();
		int length2 = s2.length();

		while ((i1 < length1) && (i2 < length2)) {
			char c1 = s1.charAt(i1);
			char c2 = s2.charAt(i2);

			if (Validator.isDigit(c1) && Validator.isDigit(c2)) {
				String leadingDigitsAsString1 = StringUtil.extractLeadingDigits(
					s1.substring(i1));
				String leadingDigitsAsString2 = StringUtil.extractLeadingDigits(
					s2.substring(i2));

				long leadingNumber1 = GetterUtil.getLong(
					leadingDigitsAsString1);
				long leadingNumber2 = GetterUtil.getLong(
					leadingDigitsAsString2);

				if (leadingNumber1 != leadingNumber2) {
					if (leadingNumber1 < leadingNumber2) {
						value = -1;
					}
					else {
						value = 1;
					}

					break;
				}

				i1 += leadingDigitsAsString1.length();
				i2 += leadingDigitsAsString2.length();

				continue;
			}

			if (isCheckSpecialCharacters() && Validator.isAscii(c1) &&
				Validator.isAscii(c2)) {

				boolean digitOrLetter1 = _isDigitOrLetter(c1);
				boolean digitOrLetter2 = _isDigitOrLetter(c2);

				if (digitOrLetter1 ^ digitOrLetter2) {
					if (digitOrLetter1) {
						value = 1;
					}
					else {
						value = -1;
					}

					break;
				}
			}

			if (_caseSensitive) {
				if (Character.isUpperCase(c1) ^ Character.isUpperCase(c2)) {
					if (Character.isUpperCase(c1)) {
						value = -1;
					}
					else {
						value = 1;
					}

					break;
				}
			}
			else {
				c1 = Character.toUpperCase(c1);
				c2 = Character.toUpperCase(c2);
			}

			if (c1 == c2) {
				i1++;
				i2++;

				continue;
			}

			if (_collator != null) {
				value = _collator.compare(s1.substring(i1), s2.substring(i2));

				break;
			}

			value = c1 - c2;

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

	protected boolean isCheckSpecialCharacters() {
		return true;
	}

	private boolean _isDigitOrLetter(char c) {
		if (Validator.isChar(c) || Validator.isDigit(c)) {
			return true;
		}

		return false;
	}

	private final boolean _ascending;
	private final boolean _caseSensitive;
	private final Collator _collator;

}