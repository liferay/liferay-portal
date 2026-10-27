/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.searcher;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Joshua Cords
 */
public class SearchRequestImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSearchContextSetStartAndEnd() {
		SearchContext searchContext = new SearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		_assertWindow(0, 5, searchContext, searchRequestImpl);

		searchContext.setStart(3);

		_assertWindow(3, 2, searchContext, searchRequestImpl);

		searchContext.setEnd(15);

		_assertWindow(3, 12, searchContext, searchRequestImpl);

		searchContext.setEnd(QueryUtil.ALL_POS);

		_assertUnboundedEnd(3, searchContext, searchRequestImpl);

		searchContext.setStart(QueryUtil.ALL_POS);

		Assert.assertEquals(QueryUtil.ALL_POS, searchContext.getEnd());
		Assert.assertEquals(QueryUtil.ALL_POS, searchContext.getStart());

		Assert.assertNull(searchRequestImpl.getFrom());
		Assert.assertNull(searchRequestImpl.getSize());

		searchContext.setEnd(5);

		_assertUnboundedStart(5, searchContext, searchRequestImpl);

		searchRequestImpl.setFrom(0);
		searchRequestImpl.setSize(8);

		searchContext.setEnd(22);
		searchContext.setStart(10);

		_assertWindow(10, 12, searchContext, searchRequestImpl);
	}

	@Test
	public void testSetFromAndSize() {
		SearchContext searchContext = new SearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		searchRequestImpl.setFrom(10);

		_assertWindow(10, 5, searchContext, searchRequestImpl);

		searchRequestImpl.setFrom(null);

		_assertUnboundedStart(5, searchContext, searchRequestImpl);

		searchRequestImpl.setSize(20);

		_assertUnboundedStart(20, searchContext, searchRequestImpl);

		searchRequestImpl.setFrom(10);

		_assertWindow(10, 20, searchContext, searchRequestImpl);

		searchRequestImpl.setFrom(30);
		searchRequestImpl.setSize(40);

		_assertWindow(30, 40, searchContext, searchRequestImpl);

		searchRequestImpl.setSize(0);

		_assertWindow(30, 0, searchContext, searchRequestImpl);
	}

	@Test
	public void testSetFromAndSizeWithOverflowingValues() {
		SearchContext searchContext = new SearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		searchRequestImpl.setSize(Integer.MAX_VALUE);

		try {
			searchRequestImpl.setFrom(1);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_assertWindow(
				0, Integer.MAX_VALUE, searchContext, searchRequestImpl);
		}

		searchRequestImpl.setSize(5);
		searchRequestImpl.setFrom(10);

		try {
			searchRequestImpl.setSize(Integer.MAX_VALUE);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_assertWindow(10, 5, searchContext, searchRequestImpl);
		}
	}

	@Test
	public void testSetFromWithNegativeFrom() {
		SearchContext searchContext = new SearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		try {
			searchRequestImpl.setFrom(-1);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_assertWindow(0, 5, searchContext, searchRequestImpl);
		}
	}

	@Test
	public void testSetFromWithUnboundedSearchContext() {
		SearchContext searchContext = _getUnboundedSearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		searchRequestImpl.setFrom(10);

		_assertUnboundedEnd(10, searchContext, searchRequestImpl);
	}

	@Test
	public void testSetSizeWithNegativeSize() {
		SearchContext searchContext = new SearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		try {
			searchRequestImpl.setSize(-1);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_assertWindow(0, 5, searchContext, searchRequestImpl);
		}
	}

	@Test
	public void testSetSizeWithNullSize() {
		SearchContext searchContext = new SearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		searchRequestImpl.setFrom(10);
		searchRequestImpl.setSize(null);

		_assertUnboundedEnd(10, searchContext, searchRequestImpl);

		searchRequestImpl.setSize(5);

		_assertWindow(10, 5, searchContext, searchRequestImpl);
	}

	@Test
	public void testSetSizeWithUnboundedSearchContext() {
		SearchContext searchContext = _getUnboundedSearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		searchRequestImpl.setSize(20);

		_assertUnboundedStart(20, searchContext, searchRequestImpl);
	}

	private void _assertUnboundedEnd(
		int from, SearchContext searchContext,
		SearchRequestImpl searchRequestImpl) {

		Assert.assertEquals(QueryUtil.ALL_POS, searchContext.getEnd());
		Assert.assertEquals(from, searchContext.getStart());
		Assert.assertEquals(Integer.valueOf(from), searchRequestImpl.getFrom());
		Assert.assertNull(searchRequestImpl.getSize());
	}

	private void _assertUnboundedStart(
		int size, SearchContext searchContext,
		SearchRequestImpl searchRequestImpl) {

		Assert.assertEquals(size, searchContext.getEnd());
		Assert.assertEquals(QueryUtil.ALL_POS, searchContext.getStart());
		Assert.assertNull(searchRequestImpl.getFrom());
		Assert.assertEquals(Integer.valueOf(size), searchRequestImpl.getSize());
	}

	private void _assertWindow(
		int from, int size, SearchContext searchContext,
		SearchRequestImpl searchRequestImpl) {

		Assert.assertEquals(from + size, searchContext.getEnd());
		Assert.assertEquals(from, searchContext.getStart());
		Assert.assertEquals(Integer.valueOf(from), searchRequestImpl.getFrom());
		Assert.assertEquals(Integer.valueOf(size), searchRequestImpl.getSize());
	}

	private SearchContext _getUnboundedSearchContext() {
		SearchContext searchContext = new SearchContext();

		searchContext.setEnd(QueryUtil.ALL_POS);
		searchContext.setStart(QueryUtil.ALL_POS);

		return searchContext;
	}

}