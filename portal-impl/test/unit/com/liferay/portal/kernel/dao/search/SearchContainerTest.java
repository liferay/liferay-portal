/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Roberto Díaz
 */
public class SearchContainerTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_searchContainer = null;
	}

	@Test
	public void testCalculateCurWhenEmptyResultsPage() {
		buildSearchContainer(2);

		_searchContainer.setResultsAndTotal(() -> _getResultsOfSize(10), 10);

		Assert.assertEquals(1, _searchContainer.getCur());
	}

	@Test
	public void testCalculateCurWhenFullResultsPage() {
		buildSearchContainer(2);

		_searchContainer.setResultsAndTotal(() -> _getResultsOfSize(20), 20);

		Assert.assertEquals(1, _searchContainer.getCur());
	}

	@Test
	public void testCalculateCurWhenNoResults() {
		buildSearchContainer(2);

		_searchContainer.setResultsAndTotal(Collections::emptyList, 0);

		Assert.assertEquals(1, _searchContainer.getCur());
	}

	@Test
	public void testCalculateCurWhenResultsPage() {
		buildSearchContainer(2);

		_searchContainer.setResultsAndTotal(() -> _getResultsOfSize(80), 80);

		Assert.assertEquals(2, _searchContainer.getCur());
	}

	@Test
	public void testCalculateStartAndEndWhenEmptyResultsPage() {
		buildSearchContainer(2);

		_searchContainer.setResultsAndTotal(() -> _getResultsOfSize(10), 10);

		Assert.assertEquals(0, _searchContainer.getStart());
		Assert.assertEquals(20, _searchContainer.getEnd());
	}

	@Test
	public void testCalculateStartAndEndWhenFullResultsPage() {
		buildSearchContainer(2);

		_searchContainer.setResultsAndTotal(() -> _getResultsOfSize(20), 20);

		Assert.assertEquals(0, _searchContainer.getStart());
		Assert.assertEquals(20, _searchContainer.getEnd());
	}

	@Test
	public void testCalculateStartAndEndWhenNoResults() {
		buildSearchContainer(2);

		_searchContainer.setResultsAndTotal(Collections::emptyList, 0);

		Assert.assertEquals(0, _searchContainer.getStart());
		Assert.assertEquals(20, _searchContainer.getEnd());
	}

	@Test
	public void testCalculateStartAndEndWhenResultsPage() {
		buildSearchContainer(2);

		_searchContainer.setResultsAndTotal(() -> _getResultsOfSize(80), 80);

		Assert.assertEquals(20, _searchContainer.getStart());
		Assert.assertEquals(40, _searchContainer.getEnd());
	}

	@Test
	public void testNotCalculateCurWhenNoResultsAndInitialPage() {
		buildSearchContainer(1);

		_searchContainer.setResultsAndTotal(Collections::emptyList, 0);

		Assert.assertFalse(_searchContainer.isRecalculateCur());
	}

	@Test
	public void testNotCalculateStartAndEndWhenNoResultsAndInitialPage() {
		buildSearchContainer(1);

		_searchContainer.setResultsAndTotal(Collections::emptyList, 0);

		Assert.assertEquals(0, _searchContainer.getStart());
		Assert.assertEquals(20, _searchContainer.getEnd());
	}

	protected void buildSearchContainer(int cur) {
		_searchContainer = new SearchContainer<>(
			ProxyFactory.newDummyInstance(PortletRequest.class), null, null,
			SearchContainer.DEFAULT_CUR_PARAM, cur, _DEFAULT_DELTA,
			ProxyFactory.newDummyInstance(PortletURL.class), null, null);
	}

	private List<Object> _getResultsOfSize(int size) {
		List<Object> objects = new ArrayList<>();

		while (objects.size() < size) {
			objects.add(new Object());
		}

		return objects;
	}

	private static final int _DEFAULT_DELTA = 20;

	private SearchContainer<Object> _searchContainer;

}