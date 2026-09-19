/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.pagination.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class SearchPaginationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		int initialUsersCount = 0;

		do {
			_randomLastName = RandomTestUtil.randomString(10);

			Hits hits = getHits(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			initialUsersCount = hits.getLength();
		}
		while (initialUsersCount > 0);

		for (int i = 0; i < _USERS_COUNT; i++) {
			User user = UserTestUtil.addUser(
				RandomTestUtil.randomString(
					NumericStringRandomizerBumper.INSTANCE,
					UniqueStringRandomizerBumper.INSTANCE),
				LocaleUtil.getDefault(), RandomTestUtil.randomString(),
				_randomLastName, new long[] {TestPropsValues.getGroupId()});

			_users.add(user);
		}

		Collections.sort(
			_users,
			new Comparator<User>() {

				@Override
				public int compare(User user1, User user2) {
					String screenName1 = user1.getScreenName();
					String screenName2 = user2.getScreenName();

					return screenName1.compareTo(screenName2);
				}

			});
	}

	@Test
	public void testResultsWhenTotalLessThanStartAndDeltaIsBiggerThanTotal()
		throws Exception {

		testResults(10, 20, _USERS_COUNT, 0);
	}

	@Test
	public void testResultsWhenTotalLessThanStartAndDeltaIsOne()
		throws Exception {

		testResults(10, 11, 1, 4);
	}

	@Test
	public void testResultsWhenTotalLessThanStartAndDeltaIsThree()
		throws Exception {

		testResults(10, 13, 2, 3);
	}

	@Test
	public void testSearchWithOneResult() throws Exception {
		Hits hits = getSearchWithOneResult(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	@Test
	public void testSearchWithOneResultWhenTotalEqualsStart() throws Exception {
		Hits hits = getSearchWithOneResult(_USERS_COUNT, 2 * _USERS_COUNT);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	@Test
	public void testSearchWithOneResultWhenTotalLessThanStart()
		throws Exception {

		Hits hits = getSearchWithOneResult(1000, 1000 + _USERS_COUNT);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	@Test
	public void testSearchWithResults() throws Exception {
		Hits hits = getHits(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(hits.toString(), _USERS_COUNT, hits.getLength());

		Document[] docs = hits.getDocs();

		Assert.assertEquals(hits.toString(), 5, docs.length);
	}

	@Test
	public void testSearchWithResultsWhenTotalEqualsStart() throws Exception {
		Hits hits = getHits(_USERS_COUNT, 2 * _USERS_COUNT);

		Assert.assertEquals(hits.toString(), _USERS_COUNT, hits.getLength());

		Document[] docs = hits.getDocs();

		Assert.assertEquals(hits.toString(), _USERS_COUNT, docs.length);
	}

	@Test
	public void testSearchWithResultsWhenTotalLessThanStart() throws Exception {
		Hits hits = getHits(1000, 1000 + _USERS_COUNT);

		Assert.assertEquals(hits.toString(), _USERS_COUNT, hits.getLength());
	}

	@Test
	public void testSearchWithResultsWhenTotalLessThanStartAndDeltaIsOne()
		throws Exception {

		Hits hits = getHits(1000, 1001);

		Assert.assertEquals(hits.toString(), _USERS_COUNT, hits.getLength());

		Document[] docs = hits.getDocs();

		Assert.assertEquals(hits.toString(), 1, docs.length);
	}

	@Test
	public void testSearchWithoutResults() throws Exception {
		Hits hits = getSearchWithoutResults(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	@Test
	public void testSearchWithoutResultsWhenTotalEqualsStart()
		throws Exception {

		Hits hits = getSearchWithoutResults(_USERS_COUNT, 2 * _USERS_COUNT);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	@Test
	public void testSearchWithoutResultsWhenTotalLessThanStart()
		throws Exception {

		Hits hits = getSearchWithoutResults(1000, 1000 + _USERS_COUNT);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	@Test
	public void testSearchWithoutResultsWhenTotalLessThanStartAndDeltaIsOne()
		throws Exception {

		Hits hits = getSearchWithoutResults(1000, 1001);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		Document[] docs = hits.getDocs();

		Assert.assertEquals(hits.toString(), 0, docs.length);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Hits getHits(int start, int end) throws Exception {
		return getHits(_randomLastName, start, end);
	}

	protected Hits getHits(String keyword, int start, int end)
		throws Exception {

		Indexer<User> indexer = IndexerRegistryUtil.getIndexer(User.class);

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(TestPropsValues.getCompanyId());
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {TestPropsValues.getGroupId()});
		searchContext.setKeywords(keyword);
		searchContext.setSorts(new Sort("screenName", false));
		searchContext.setStart(start);

		return indexer.search(searchContext);
	}

	protected Hits getSearchWithOneResult(int start, int end) throws Exception {
		User user = _users.get(0);

		return getHits(user.getFirstName(), start, end);
	}

	protected Hits getSearchWithoutResults(int start, int end)
		throws Exception {

		return getHits("invalidKeyword", start, end);
	}

	protected void testResults(
			int start, int end, int expectedTotal,
			int expectedRecalculatedStart)
		throws Exception {

		Hits hits = getHits(start, end);

		Document[] docs = hits.getDocs();

		Assert.assertEquals(hits.toString(), expectedTotal, docs.length);

		List<User> returnedUsers = new ArrayList<>();

		for (int i = 0; i < docs.length; i++) {
			Document doc = hits.doc(i);

			long userId = GetterUtil.getLong(doc.get(Field.USER_ID));

			returnedUsers.add(UserLocalServiceUtil.getUser(userId));
		}

		Assert.assertEquals(
			StringBundler.concat(
				"{end=", end, ", expectedRecalculatedStart=",
				expectedRecalculatedStart, ", expectedTotal=", expectedTotal,
				", returnedUsers=", returnedUsers, ", start=", start,
				", _users=", _users, "}"),
			_users.subList(
				expectedRecalculatedStart,
				expectedRecalculatedStart + docs.length),
			returnedUsers);
	}

	private static final int _USERS_COUNT = 5;

	private String _randomLastName;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}