/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import org.junit.Test;

/**
 * @author Alan Huang
 */
public class TFSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testEmbeddedJSON() throws Exception {
		test("EmbeddedJSON.testtf");
	}

	@Test
	public void testIncorrectEmptyLines() throws Exception {
		test("IncorrectEmptyLines.testtf");
	}

	@Test
	public void testIncorrectIndentation() throws Exception {
		test("IncorrectIndentation.testtf");
	}

	@Test
	public void testSortBlocks() throws Exception {
		test("SortBlocks.testtf");
	}

	@Test
	public void testSortBlocksWithComments() throws Exception {
		test("SortBlocksWithComments.testtf");
	}

	@Test
	public void testSortBlocksWithIgnoredBrackets() throws Exception {
		test("SortBlocksWithIgnoredBrackets.testtf");
	}

	@Test
	public void testSortBlocksWithIncorrectIndentation() throws Exception {
		test("SortBlocksWithIncorrectIndentation.testtf");
	}

	@Test
	public void testSortBlocksWithUnbalancedBrackets() throws Exception {
		test("SortBlocksWithUnbalancedBrackets.testtf");
	}

	@Test
	public void testSortEntries() throws Exception {
		test("SortEntries.testtf");
	}

	@Test
	public void testSortEntriesWithComments() throws Exception {
		test("SortEntriesWithComments.testtf");
	}

	@Test
	public void testSpacesAroundEqualSigns() throws Exception {
		test("SpacesAroundEqualSigns.testtf");
	}

	@Test
	public void testUnquotedBlockLabels() throws Exception {
		test("UnquotedBlockLabels.testtf");
	}

}