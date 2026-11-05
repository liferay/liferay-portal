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

		_assertWindow(0, searchContext, searchRequestImpl, 5);

		searchContext.setStart(3);

		_assertWindow(3, searchContext, searchRequestImpl, 2);

		searchContext.setEnd(15);

		_assertWindow(3, searchContext, searchRequestImpl, 12);

		searchContext.setEnd(QueryUtil.ALL_POS);

		_assertUnboundedEnd(3, searchContext, searchRequestImpl);

		searchContext.setStart(QueryUtil.ALL_POS);

		Assert.assertEquals(QueryUtil.ALL_POS, searchContext.getEnd());
		Assert.assertEquals(QueryUtil.ALL_POS, searchContext.getStart());

		Assert.assertNull(searchRequestImpl.getFrom());
		Assert.assertNull(searchRequestImpl.getSize());

		searchContext.setEnd(5);

		_assertUnboundedStart(searchContext, searchRequestImpl, 5);

		searchRequestImpl.setFrom(0);
		searchRequestImpl.setSize(8);

		searchContext.setEnd(22);
		searchContext.setStart(10);

		_assertWindow(10, searchContext, searchRequestImpl, 12);
	}

	@Test
	public void testSetFromAndSize() {
		SearchContext searchContext = new SearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		searchRequestImpl.setFrom(10);

		_assertWindow(10, searchContext, searchRequestImpl, 5);

		searchRequestImpl.setFrom(null);

		_assertUnboundedStart(searchContext, searchRequestImpl, 5);

		searchRequestImpl.setSize(20);

		_assertUnboundedStart(searchContext, searchRequestImpl, 20);

		searchRequestImpl.setFrom(10);

		_assertWindow(10, searchContext, searchRequestImpl, 20);

		searchRequestImpl.setFrom(30);
		searchRequestImpl.setSize(40);

		_assertWindow(30, searchContext, searchRequestImpl, 40);

		searchRequestImpl.setSize(0);

		_assertWindow(30, searchContext, searchRequestImpl, 0);
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
				0, searchContext, searchRequestImpl, Integer.MAX_VALUE);
		}

		searchRequestImpl.setFrom(10);
		searchRequestImpl.setSize(5);

		try {
			searchRequestImpl.setSize(Integer.MAX_VALUE);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_assertWindow(10, searchContext, searchRequestImpl, 5);
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
			_assertWindow(0, searchContext, searchRequestImpl, 5);
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
			_assertWindow(0, searchContext, searchRequestImpl, 5);
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

		_assertWindow(10, searchContext, searchRequestImpl, 5);
	}

	@Test
	public void testSetSizeWithUnboundedSearchContext() {
		SearchContext searchContext = _getUnboundedSearchContext();

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			searchContext);

		searchRequestImpl.setSize(20);

		_assertUnboundedStart(searchContext, searchRequestImpl, 20);
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
		SearchContext searchContext, SearchRequestImpl searchRequestImpl,
		int size) {

		Assert.assertEquals(size, searchContext.getEnd());
		Assert.assertEquals(QueryUtil.ALL_POS, searchContext.getStart());
		Assert.assertNull(searchRequestImpl.getFrom());
		Assert.assertEquals(Integer.valueOf(size), searchRequestImpl.getSize());
	}

	private void _assertWindow(
		int from, SearchContext searchContext,
		SearchRequestImpl searchRequestImpl, int size) {

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