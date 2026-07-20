/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ResourcePermissionTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsActionKeys;
import com.liferay.segments.constants.SegmentsConstants;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class SegmentsEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_groupUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.POWER_USER);
	}

	@Test
	public void testAddSegmentsEntryWithManageSegmentsEntriesPermission()
		throws Exception {

		_segmentsEntryService.addSegmentsEntry(
			null, RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), true,
			CriteriaSerializer.serialize(new Criteria()),
			SegmentsEntryConstants.SOURCE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	@Test
	public void testAddSegmentsEntryWithManageSegmentsEntriesPermissionWithoutSource()
		throws Exception {

		long roleId = RoleTestUtil.addGroupRole(_group.getGroupId());

		_userLocalService.addRoleUser(roleId, _groupUser);

		_resourcePermissionLocalService.addResourcePermission(
			_group.getCompanyId(), SegmentsConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			roleId, SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			_segmentsEntryService.addSegmentsEntry(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				CriteriaSerializer.serialize(new Criteria()),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					_group, _groupUser.getUserId()));
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSegmentsEntryWithoutManageSegmentsEntriesPermission()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			_segmentsEntryService.addSegmentsEntry(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				RandomTestUtil.randomString(),
				SegmentsEntryConstants.SOURCE_DEFAULT,
				ServiceContextTestUtil.getServiceContext(
					_group, _groupUser.getUserId()));
		}
	}

	@Test
	public void testDeleteSegmentsEntryWithDeletePermission() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		_segmentsEntryService.deleteSegmentsEntry(
			segmentsEntry.getSegmentsEntryId());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteSegmentsEntryWithoutDeletePermission()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			_segmentsEntryService.deleteSegmentsEntry(
				segmentsEntry.getSegmentsEntryId());
		}
	}

	@Test
	public void testGetSegmentsEntriesCountWithoutViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			serviceContext);

		ResourcePermissionTestUtil.deleteResourcePermissions(segmentsEntry);

		SegmentsTestUtil.addSegmentsEntry(serviceContext);
		SegmentsTestUtil.addSegmentsEntry(serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			Assert.assertEquals(
				2,
				_segmentsEntryService.getSegmentsEntriesCount(
					_group.getGroupId()));
		}
	}

	@Test
	public void testGetSegmentsEntriesCountWithViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		SegmentsTestUtil.addSegmentsEntry(serviceContext);
		SegmentsTestUtil.addSegmentsEntry(serviceContext);
		SegmentsTestUtil.addSegmentsEntry(serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			Assert.assertEquals(
				3,
				_segmentsEntryService.getSegmentsEntriesCount(
					_group.getGroupId()));
		}
	}

	@Test
	public void testGetSegmentsEntriesWithoutViewPermission() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			serviceContext);
		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			serviceContext);
		SegmentsEntry segmentsEntry3 = SegmentsTestUtil.addSegmentsEntry(
			serviceContext);

		ResourcePermissionTestUtil.deleteResourcePermissions(segmentsEntry2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			List<SegmentsEntry> segmentsEntries =
				_segmentsEntryService.getSegmentsEntries(_group.getGroupId());

			Assert.assertEquals(
				segmentsEntries.toString(), 2, segmentsEntries.size());

			Assert.assertTrue(segmentsEntries.contains(segmentsEntry1));
			Assert.assertTrue(segmentsEntries.contains(segmentsEntry3));
		}
	}

	@Test
	public void testGetSegmentsEntriesWithViewPermission() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			serviceContext);
		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			serviceContext);
		SegmentsEntry segmentsEntry3 = SegmentsTestUtil.addSegmentsEntry(
			serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			List<SegmentsEntry> segmentsEntries =
				_segmentsEntryService.getSegmentsEntries(
					_group.getGroupId(), 0, 100, null);

			Assert.assertEquals(
				segmentsEntries.toString(), 3, segmentsEntries.size());

			Assert.assertTrue(segmentsEntries.contains(segmentsEntry1));
			Assert.assertTrue(segmentsEntries.contains(segmentsEntry2));
			Assert.assertTrue(segmentsEntries.contains(segmentsEntry3));
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetSegmentsEntryWithoutViewPermission() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		List<Role> roles = _roleLocalService.getRoles(_group.getCompanyId());

		for (Role role : roles) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			_resourcePermissionLocalService.removeResourcePermission(
				_group.getCompanyId(), SegmentsEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsEntry.getSegmentsEntryId()),
				role.getRoleId(), ActionKeys.VIEW);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			_segmentsEntryService.getSegmentsEntry(
				segmentsEntry.getSegmentsEntryId());
		}
	}

	@Test
	public void testGetSegmentsEntryWithViewPermission() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			_segmentsEntryService.getSegmentsEntry(
				segmentsEntry.getSegmentsEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateSegmentsEntryFromExternalSource() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			CriteriaSerializer.serialize(new Criteria()),
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND, serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, PermissionCheckerFactoryUtil.create(_groupUser))) {

			_segmentsEntryService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(),
				segmentsEntry.getSegmentsEntryKey(), segmentsEntry.getNameMap(),
				segmentsEntry.getDescriptionMap(), segmentsEntry.isActive(),
				segmentsEntry.getCriteria(), serviceContext);
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateSegmentsEntryWithoutUpdatePermission()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				guestUser, PermissionCheckerFactoryUtil.create(guestUser))) {

			_segmentsEntryService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					_group, guestUser.getUserId()));
		}
	}

	@Test
	public void testUpdateSegmentsEntryWithUpdatePermission() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		_segmentsEntryService.updateSegmentsEntry(
			segmentsEntry.getSegmentsEntryId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), true,
			CriteriaSerializer.serialize(new Criteria()),
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _groupUser;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsEntryService _segmentsEntryService;

	@Inject
	private UserLocalService _userLocalService;

}