/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import java.text.Collator;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hugo Huijser
 */
public class NaturalOrderStringComparatorTest {

	@Test
	public void testSortAccentuation() {
		testSort(
			new String[] {
				"jo\u00E3o", "\u00DAtil", "uva", "Amor", "\u00FAnico",
				"\u00E1gua", "Urso", "abelha", "\u00C1mor", "Jos\u00E9",
				"\u00C2nimo", "\u00C1rvore"
			},
			new String[] {
				"abelha", "\u00E1gua", "Amor", "\u00C1mor", "\u00C2nimo",
				"\u00C1rvore", "jo\u00E3o", "Jos\u00E9", "\u00FAnico", "Urso",
				"\u00DAtil", "uva"
			},
			false, CollatorUtil.getInstance(LocaleUtil.getDefault()));
	}

	@Test
	public void testSortAccentuationCaseSensitive() {
		testSort(
			new String[] {
				"jo\u00E3o", "\u00DAtil", "uva", "Amor", "\u00FAnico",
				"\u00E1gua", "Urso", "abelha", "\u00C1mor", "Jos\u00E9",
				"\u00C2nimo", "\u00C1rvore"
			},
			new String[] {
				"Amor", "\u00C1mor", "\u00C2nimo", "\u00C1rvore", "Jos\u00E9",
				"Urso", "\u00DAtil", "abelha", "\u00E1gua", "jo\u00E3o",
				"\u00FAnico", "uva"
			},
			true, CollatorUtil.getInstance(LocaleUtil.getDefault()));
	}

	@Test
	public void testSortCaseSensitive() {
		testSort(
			new String[] {"hello", "world", "Hello", "World", "HELLO", "WORLD"},
			new String[] {"HELLO", "Hello", "WORLD", "World", "hello", "world"},
			true);
	}

	@Test
	public void testSortDateStrings() {
		testSort(
			new String[] {
				"20250729214517", "20250728214613", "20250729214527",
				"20240729214614", "20250629214610", "20250729204606",
				"20250729214528"
			},
			new String[] {
				"20240729214614", "20250629214610", "20250728214613",
				"20250729204606", "20250729214517", "20250729214527",
				"20250729214528"
			},
			true);
	}

	@Test
	public void testSortJarVersions() {
		testSort(
			new String[] {
				"com.liferay.module-2.0.7.jar", "com.liferay.module-2.0.11.jar",
				"com.liferay.module-2.0.1.jar"
			},
			new String[] {
				"com.liferay.module-2.0.1.jar", "com.liferay.module-2.0.7.jar",
				"com.liferay.module-2.0.11.jar"
			},
			false);
	}

	@Test
	public void testSortNumericalString() {
		testSort(
			new String[] {
				"1 book", "100 dollar", "25 shoes", "04:00", "4:00", "04:30",
				"hello07world", "hello8world", "hello007world"
			},
			new String[] {
				"1 book", "04:00", "4:00", "04:30", "25 shoes", "100 dollar",
				"hello007world", "hello07world", "hello8world"
			},
			false);
	}

	@Test
	public void testSortRegularString() {
		testSort(
			new String[] {"hello", "world", "helloworld"},
			new String[] {"hello", "helloworld", "world"}, false);
	}

	@Test
	public void testSortSpecialChars() {
		testSort(
			new String[] {
				"hello_world", "helloworld", "hello world", "~hello_world"
			},
			new String[] {
				"~hello_world", "hello world", "hello_world", "helloworld"
			},
			false);
	}

	protected void testSort(
		String[] array, String[] sortedArray, boolean caseSensitive) {

		testSort(array, sortedArray, caseSensitive, null);
	}

	protected void testSort(
		String[] array, String[] sortedArray, boolean caseSensitive,
		Collator collator) {

		Arrays.sort(
			array,
			new NaturalOrderStringComparator(true, caseSensitive, collator));

		Assert.assertEquals(
			Arrays.toString(sortedArray), array.length, sortedArray.length);

		for (int i = 0; i < array.length; i++) {
			Assert.assertEquals(sortedArray[i], array[i]);
		}

		Arrays.sort(
			array,
			new NaturalOrderStringComparator(false, caseSensitive, collator));

		for (int i = 0; i < array.length; i++) {
			Assert.assertEquals(
				sortedArray[sortedArray.length - (i + 1)], array[i]);
		}
	}

}