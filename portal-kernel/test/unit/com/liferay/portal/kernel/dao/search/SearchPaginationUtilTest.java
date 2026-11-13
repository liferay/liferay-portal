/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import com.liferay.portal.kernel.dao.orm.QueryUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto Díaz
 */
public class SearchPaginationUtilTest {

	@Test
	public void testCalculateStartAndEndWhenAllPositions() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, 10);

		Assert.assertEquals(QueryUtil.ALL_POS, startAndEnd[0]);
		Assert.assertEquals(QueryUtil.ALL_POS, startAndEnd[1]);
	}

	@Test
	public void testCalculateStartAndEndWhenEmptyResultsPage() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			40, 60, 10);

		Assert.assertEquals(0, startAndEnd[0]);
		Assert.assertEquals(10, startAndEnd[1]);
	}

	@Test
	public void testCalculateStartAndEndWhenEmptyResultsPageAndPartialLastPage() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			100, 120, 50);

		Assert.assertEquals(40, startAndEnd[0]);
		Assert.assertEquals(50, startAndEnd[1]);
	}

	@Test
	public void testCalculateStartAndEndWhenFullResultsPage() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			20, 40, 20);

		Assert.assertEquals(0, startAndEnd[0]);
		Assert.assertEquals(20, startAndEnd[1]);
	}

	@Test
	public void testCalculateStartAndEndWhenNegativeDelta() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			40, 20, 10);

		Assert.assertEquals(0, startAndEnd[0]);
		Assert.assertEquals(0, startAndEnd[1]);
	}

	@Test
	public void testCalculateStartAndEndWhenNoResults() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			20, 40, 0);

		Assert.assertEquals(0, startAndEnd[0]);
		Assert.assertEquals(0, startAndEnd[1]);
	}

	@Test
	public void testCalculateStartAndEndWhenPartialResultsPage() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			40, 60, 50);

		Assert.assertEquals(40, startAndEnd[0]);
		Assert.assertEquals(50, startAndEnd[1]);
	}

	@Test
	public void testCalculateStartAndEndWhenResultsPage() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			20, 40, 80);

		Assert.assertEquals(20, startAndEnd[0]);
		Assert.assertEquals(40, startAndEnd[1]);
	}

	@Test
	public void testCalculateStartAndEndWhenUnalignedEmptyResultsPage() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			45, 65, 30);

		Assert.assertEquals(20, startAndEnd[0]);
		Assert.assertEquals(30, startAndEnd[1]);
	}

	@Test
	public void testNotCalculateStartAndEndWhenNoResultsAndInitialPage() {
		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(0, 20, 0);

		Assert.assertEquals(0, startAndEnd[0]);
		Assert.assertEquals(0, startAndEnd[1]);
	}

}