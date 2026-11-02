/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsStatsUser;
import com.liferay.blogs.service.BlogsStatsUserLocalServiceUtil;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;

import java.util.List;

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
public class BlogsStatsUserLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user1 = UserTestUtil.addOmniadminUser();
		_user2 = UserTestUtil.addOmniadminUser();
		_user3 = UserTestUtil.addOmniadminUser();
	}

	@Test
	public void testAddFirstUserBlogsEntryAddsNewBlogsStatsUser()
		throws Exception {

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user1.getUserId()));

		BlogsStatsUser blogsStatsUser =
			BlogsStatsUserLocalServiceUtil.getStatsUser(
				blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(1, blogsStatsUser.getEntryCount());
		Assert.assertEquals(0, blogsStatsUser.getRatingsTotalEntries());
		Assert.assertEquals(0, blogsStatsUser.getRatingsTotalScore(), 0);
		Assert.assertEquals(0, blogsStatsUser.getRatingsAverageScore(), 0);
	}

	@Test
	public void testAddNewBlogsEntryIncrementBlogsStatsUserEntryEntryCount()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _user1.getUserId());

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		BlogsStatsUser blogsStatsUser =
			BlogsStatsUserLocalServiceUtil.getStatsUser(
				blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(2, blogsStatsUser.getEntryCount());
		Assert.assertEquals(0, blogsStatsUser.getRatingsTotalEntries());
		Assert.assertEquals(0, blogsStatsUser.getRatingsTotalScore(), 0);
		Assert.assertEquals(0, blogsStatsUser.getRatingsAverageScore(), 0);
	}

	@Test
	public void testAddNewRatingsEntryIncrementsBlogsStatsUserRatings()
		throws Exception {

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user1.getUserId()));

		RatingsEntryLocalServiceUtil.updateEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1, null);

		BlogsStatsUser blogsStatsUser =
			BlogsStatsUserLocalServiceUtil.getStatsUser(
				blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(1, blogsStatsUser.getRatingsTotalEntries());
		Assert.assertEquals(1.0, blogsStatsUser.getRatingsAverageScore(), 0);
		Assert.assertEquals(1.0, blogsStatsUser.getRatingsTotalScore(), 0);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user3.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1, null);

		blogsStatsUser = BlogsStatsUserLocalServiceUtil.getStatsUser(
			blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(2, blogsStatsUser.getRatingsTotalEntries());
		Assert.assertEquals(1.0, blogsStatsUser.getRatingsAverageScore(), 0);
		Assert.assertEquals(2.0, blogsStatsUser.getRatingsTotalScore(), 0);
	}

	@Test
	public void testDeleteRatingsEntryDecreasesBlogsStatsUserEntryEntryCount()
		throws Exception {

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user1.getUserId()));

		RatingsEntryLocalServiceUtil.updateEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1, null);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user3.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 0.2, null);

		RatingsEntryLocalServiceUtil.deleteEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId());

		BlogsStatsUser blogsStatsUser =
			BlogsStatsUserLocalServiceUtil.getStatsUser(
				blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(1, blogsStatsUser.getRatingsTotalEntries());

		RatingsEntryLocalServiceUtil.deleteEntry(
			_user3.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId());

		blogsStatsUser = BlogsStatsUserLocalServiceUtil.getStatsUser(
			blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(0, blogsStatsUser.getRatingsTotalEntries());
	}

	@Test
	public void testDeleteRatingsEntryUpdatesBlogsStatsUserRatings()
		throws Exception {

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user1.getUserId()));

		RatingsEntryLocalServiceUtil.updateEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1, null);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user3.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 0.2, null);

		RatingsEntryLocalServiceUtil.deleteEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId());

		BlogsStatsUser blogsStatsUser =
			BlogsStatsUserLocalServiceUtil.getStatsUser(
				blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(0.2, blogsStatsUser.getRatingsAverageScore(), 0.01);
		Assert.assertEquals(0.2, blogsStatsUser.getRatingsTotalScore(), 0.01);

		RatingsEntryLocalServiceUtil.deleteEntry(
			_user3.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId());

		blogsStatsUser = BlogsStatsUserLocalServiceUtil.getStatsUser(
			blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(0, blogsStatsUser.getRatingsAverageScore(), 0);
		Assert.assertEquals(0, blogsStatsUser.getRatingsTotalScore(), 0);
	}

	@Test
	public void testGetOrganizationStatsUsers() throws Exception {
		BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user1.getUserId()));

		BlogsTestUtil.addEntry(
			_user2.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user2.getUserId()));

		Organization organization = OrganizationTestUtil.addOrganization();

		List<BlogsStatsUser> blogsStatsUsers =
			BlogsStatsUserLocalServiceUtil.getOrganizationStatsUsers(
				organization.getOrganizationId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			blogsStatsUsers.toString(), 0, blogsStatsUsers.size());

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), _user1.getUserId());

		blogsStatsUsers =
			BlogsStatsUserLocalServiceUtil.getOrganizationStatsUsers(
				organization.getOrganizationId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			blogsStatsUsers.toString(), 1, blogsStatsUsers.size());

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), _user2.getUserId());

		blogsStatsUsers =
			BlogsStatsUserLocalServiceUtil.getOrganizationStatsUsers(
				organization.getOrganizationId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			blogsStatsUsers.toString(), 2, blogsStatsUsers.size());

		_userLocalService.unsetOrganizationUsers(
			organization.getOrganizationId(),
			new long[] {_user1.getUserId(), _user2.getUserId()});

		blogsStatsUsers =
			BlogsStatsUserLocalServiceUtil.getOrganizationStatsUsers(
				organization.getOrganizationId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			blogsStatsUsers.toString(), 0, blogsStatsUsers.size());
	}

	@Test
	public void testUpdateRatingsEntryDoesNotIncreaseBlogsStatsUserEntryEntryCount()
		throws Exception {

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user1.getUserId()));

		RatingsEntryLocalServiceUtil.updateEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1, null);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user3.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1, null);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 0.2, null);

		BlogsStatsUser blogsStatsUser =
			BlogsStatsUserLocalServiceUtil.getStatsUser(
				blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(2, blogsStatsUser.getRatingsTotalEntries());
	}

	@Test
	public void testUpdateRatingsEntryUpdatesBlogsStatsUserRatings()
		throws Exception {

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user1.getUserId()));

		RatingsEntryLocalServiceUtil.updateEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1, null);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user3.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1, null);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user2.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 0.2, null);

		BlogsStatsUser blogsStatsUser =
			BlogsStatsUserLocalServiceUtil.getStatsUser(
				blogsEntry.getGroupId(), blogsEntry.getUserId());

		Assert.assertEquals(0.6, blogsStatsUser.getRatingsAverageScore(), 0);
		Assert.assertEquals(1.2, blogsStatsUser.getRatingsTotalScore(), 0);
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	@DeleteAfterTestRun
	private User _user3;

	@Inject
	private UserLocalService _userLocalService;

}