/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.exception.EntryScoreException;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class RatingsEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetEntriesWithEmptyClassPKs() throws Exception {
		String className = RandomTestUtil.randomString();

		RatingsEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), className, RandomTestUtil.randomLong(),
			1, ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Map<Long, RatingsEntry> ratingsEntries =
			RatingsEntryLocalServiceUtil.getEntries(
				TestPropsValues.getUserId(), className, new long[0]);

		Assert.assertTrue(ratingsEntries.toString(), ratingsEntries.isEmpty());
	}

	@Test
	public void testRatingScore0IsValidScore() throws Exception {
		RatingsEntry ratingsEntry = RatingsEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), 0,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(0, ratingsEntry.getScore(), 0.001);
	}

	@Test
	public void testRatingScore1IsValidScore() throws Exception {
		RatingsEntry ratingsEntry = RatingsEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), 1,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(1, ratingsEntry.getScore(), 0.001);
	}

	@Test(expected = EntryScoreException.class)
	public void testRatingScoreGreaterThan1IsInvalidScore() throws Exception {
		RatingsEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), 4,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = EntryScoreException.class)
	public void testRatingScoreLessThan0IsInvalidScore() throws Exception {
		RatingsEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), -1,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = EntryScoreException.class)
	public void testRatingScoreNaNIsInvalidScore() throws Exception {
		RatingsEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), Double.NaN,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@DeleteAfterTestRun
	private Group _group;

}