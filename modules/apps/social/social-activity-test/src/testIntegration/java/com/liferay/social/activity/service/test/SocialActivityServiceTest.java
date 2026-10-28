/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.activity.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.portlet.social.util.SocialActivityHierarchyEntryThreadLocal;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.service.SocialActivityLocalServiceUtil;
import com.liferay.social.kernel.service.SocialActivityServiceUtil;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class SocialActivityServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		RoleTestUtil.addResourcePermission(
			RoleConstants.GUEST, DLConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			ActionKeys.VIEW);
	}

	@After
	public void tearDown() throws Exception {
		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, DLConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			ActionKeys.VIEW);

		SocialActivityHierarchyEntryThreadLocal.clear();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testFilterActivities() throws Exception {
		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString() + ".txt", _group.getGroupId(),
			RandomTestUtil.randomString());

		deleteGuestPermission(fileEntry);

		List<SocialActivity> activities =
			SocialActivityLocalServiceUtil.getGroupActivities(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(activities.toString(), 1, activities.size());

		UserTestUtil.setUser(_user);

		activities = SocialActivityServiceUtil.getGroupActivities(
			_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(activities.toString(), 0, activities.size());
	}

	@Test
	public void testGetActivitiesCount() throws Exception {
		FileEntry fileEntry1 = _addFileEntry(
			RandomTestUtil.randomString() + ".txt", _group.getGroupId(),
			RandomTestUtil.randomString());

		FileEntry fileEntry2 = _addFileEntry(
			RandomTestUtil.randomString() + ".txt", _group.getGroupId(),
			RandomTestUtil.randomString());

		deleteGuestPermission(fileEntry2);

		UserTestUtil.setUser(_user);

		long classNameId = PortalUtil.getClassNameId(DLFileEntry.class);

		Assert.assertEquals(
			1,
			SocialActivityServiceUtil.getActivitiesCount(
				0, classNameId, fileEntry1.getFileEntryId()));
		Assert.assertEquals(
			0,
			SocialActivityServiceUtil.getActivitiesCount(
				0, classNameId, fileEntry2.getFileEntryId()));
	}

	@Test
	public void testGetGroupActivitiesCount() throws Exception {
		_group.setType(GroupConstants.TYPE_SITE_PRIVATE);

		_group = GroupLocalServiceUtil.updateGroup(_group);

		_addFileEntry(
			RandomTestUtil.randomString() + ".txt", _group.getGroupId(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			1,
			SocialActivityServiceUtil.getGroupActivitiesCount(
				_group.getGroupId()));

		UserTestUtil.setUser(_user);

		Assert.assertEquals(
			0,
			SocialActivityServiceUtil.getGroupActivitiesCount(
				_group.getGroupId()));
	}

	@Test
	public void testGetOrganizationActivitiesCount() throws Exception {
		_organization = OrganizationTestUtil.addOrganization(true);

		_addFileEntry(
			RandomTestUtil.randomString() + ".txt", _organization.getGroupId(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			1,
			SocialActivityServiceUtil.getOrganizationActivitiesCount(
				_organization.getOrganizationId()));

		UserTestUtil.setUser(_user);

		Assert.assertEquals(
			0,
			SocialActivityServiceUtil.getOrganizationActivitiesCount(
				_organization.getOrganizationId()));
	}

	@Test
	public void testGetUserActivitiesCount() throws Exception {
		_addFileEntry(
			RandomTestUtil.randomString() + ".txt", _group.getGroupId(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			SocialActivityLocalServiceUtil.getUserActivitiesCount(
				TestPropsValues.getUserId()),
			SocialActivityServiceUtil.getUserActivitiesCount(
				TestPropsValues.getUserId()));

		UserTestUtil.setUser(_user);

		Assert.assertEquals(
			0,
			SocialActivityServiceUtil.getUserActivitiesCount(
				TestPropsValues.getUserId()));
	}

	@Test
	public void testPagingFilterActivities() throws Exception {
		for (int i = 0; i < 4; i++) {
			_addFileEntry(
				RandomTestUtil.randomString() + ".txt", _group.getGroupId(),
				String.valueOf(i));

			FileEntry fileEntry = _addFileEntry(
				RandomTestUtil.randomString() + ".txt", _group.getGroupId(),
				RandomTestUtil.randomString());

			deleteGuestPermission(fileEntry);
		}

		UserTestUtil.setUser(_user);

		Assert.assertEquals(
			8,
			SocialActivityServiceUtil.getGroupActivitiesCount(
				_group.getGroupId()));

		List<SocialActivity> activities =
			SocialActivityServiceUtil.getGroupActivities(
				_group.getGroupId(), 0, 2);

		Assert.assertEquals(activities.toString(), 2, activities.size());

		int index = 3;

		for (SocialActivity activity : activities) {
			String title = String.valueOf(index);

			Assert.assertEquals(title, activity.getExtraDataValue("title"));

			index--;
		}

		activities = SocialActivityServiceUtil.getGroupActivities(
			_group.getGroupId(), 2, 4);

		Assert.assertEquals(activities.toString(), 2, activities.size());

		for (SocialActivity activity : activities) {
			String title = String.valueOf(index);

			Assert.assertEquals(title, activity.getExtraDataValue("title"));

			index--;
		}
	}

	protected void deleteGuestPermission(FileEntry fileEntry) throws Exception {
		Role role = RoleLocalServiceUtil.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			_group.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
			new String[0]);
	}

	private FileEntry _addFileEntry(String fileName, long groupId, String title)
		throws Exception {

		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), groupId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.TEXT_PLAIN, title, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, TestDataConstants.TEST_BYTE_ARRAY, null, null,
			null,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Organization _organization;

	@DeleteAfterTestRun
	private User _user;

}