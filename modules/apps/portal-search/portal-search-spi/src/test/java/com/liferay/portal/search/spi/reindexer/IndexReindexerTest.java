/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.spi.reindexer;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Felipe Lorenz
 */
public class IndexReindexerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testParseExecutionModeDefaultsToFull() {
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.FULL,
			IndexReindexer.ExecutionMode.parse("   "));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.FULL,
			IndexReindexer.ExecutionMode.parse(""));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.FULL,
			IndexReindexer.ExecutionMode.parse("unknown"));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.FULL,
			IndexReindexer.ExecutionMode.parse(null));
	}

	@Test
	public void testParseExecutionModeFromValidValues() {
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.CONCURRENT,
			IndexReindexer.ExecutionMode.parse("CONCURRENT"));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.CONCURRENT,
			IndexReindexer.ExecutionMode.parse("concurrent"));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.FULL,
			IndexReindexer.ExecutionMode.parse("FULL"));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.FULL,
			IndexReindexer.ExecutionMode.parse("full"));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.SYNC,
			IndexReindexer.ExecutionMode.parse("SYNC"));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.SYNC,
			IndexReindexer.ExecutionMode.parse("SyNc"));
		Assert.assertEquals(
			IndexReindexer.ExecutionMode.SYNC,
			IndexReindexer.ExecutionMode.parse("sync"));
	}

}