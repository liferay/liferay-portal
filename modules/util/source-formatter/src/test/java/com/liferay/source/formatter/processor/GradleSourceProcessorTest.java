/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import org.junit.Test;

/**
 * @author Alan Huang
 */
public class GradleSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testBouncyCastleFIPS() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"BouncyCastleFIPS.testgradle"
			).addExpectedMessage(
				"Do not use non-FIPS BouncyCastle, see LPD-90318", 3
			).addExpectedMessage(
				"Do not use non-FIPS BouncyCastle, see LPD-90318", 4
			));
	}

	@Test
	public void testMissingLineBreaksAroundCurlyBraces() throws Exception {
		test("MissingLineBreaksAroundCurlyBraces.testgradle");
	}

	@Test
	public void testSortDependencies() throws Exception {
		test("SortDependencies.testgradle");
	}

	@Test
	public void testSortFileNames() throws Exception {
		test("SortFileNames.testgradle");
	}

	@Test
	public void testSortMapKeys() throws Exception {
		test("SortMapKeys1.testgradle");
		test("SortMapKeys2.testgradle");
		test("SortMapKeys3.testgradle");
		test("SortMapKeys4.testgradle");
		test("SortMapKeys5.testgradle");
	}

	@Test
	public void testStylingCheck() throws Exception {
		test("StylingCheck.testgradle");
	}

}