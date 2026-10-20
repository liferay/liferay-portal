/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.service.test;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.service.AnnouncementsEntryLocalService;
import com.liferay.announcements.kernel.service.AnnouncementsEntryService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class AnnouncementsEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-110153")
	public void testAddEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		User user = UserTestUtil.addUser();

		UserTestUtil.setUser(user);

		long classNameId = _portal.getClassNameId(Group.class);

		try {
			PrincipalException.MustHavePermission mustHavePermission =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> _addEntry(classNameId, group.getGroupId()));

			Assert.assertEquals(
				Group.class.getName(), mustHavePermission.resourceName);
			Assert.assertEquals(
				group.getGroupId(), mustHavePermission.resourceId);
			Assert.assertArrayEquals(
				new String[] {ActionKeys.MANAGE_ANNOUNCEMENTS},
				mustHavePermission.actionId);
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}

		AnnouncementsEntry announcementsEntry = _addEntry(
			classNameId, group.getGroupId());

		Assert.assertEquals(
			group.getGroupId(), announcementsEntry.getClassPK());

		GroupTestUtil.deleteGroup(group);

		_userLocalService.deleteUser(user);
	}

	@Test
	@TestInfo("LPD-110153")
	public void testAddEntryWithUnsupportedClassNameId() throws Exception {
		PrincipalException.MustHavePermission mustHavePermission =
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _addEntry(
					_portal.getClassNameId(Company.class),
					TestPropsValues.getCompanyId()));

		Assert.assertEquals(
			Company.class.getName(), mustHavePermission.resourceName);
		Assert.assertEquals(
			TestPropsValues.getCompanyId(), mustHavePermission.resourceId);
		Assert.assertArrayEquals(
			new String[] {ActionKeys.MANAGE_ANNOUNCEMENTS},
			mustHavePermission.actionId);
	}

	@Test
	@TestInfo("LPD-110153")
	public void testAddEntryWithUserClassNameId() throws Exception {
		User user1 = UserTestUtil.addUser();

		User user2 = UserTestUtil.addUser();

		UserTestUtil.setUser(user2);

		long classNameId = _portal.getClassNameId(User.class);

		AnnouncementsEntry announcementsEntry = null;

		try {
			PrincipalException.MustHavePermission mustHavePermission =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> _addEntry(classNameId, user1.getUserId()));

			Assert.assertEquals(
				User.class.getName(), mustHavePermission.resourceName);
			Assert.assertEquals(
				user1.getUserId(), mustHavePermission.resourceId);
			Assert.assertArrayEquals(
				new String[] {ActionKeys.UPDATE}, mustHavePermission.actionId);

			announcementsEntry = _addEntry(classNameId, user2.getUserId());

			Assert.assertEquals(
				user2.getUserId(), announcementsEntry.getClassPK());
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}

		_announcementsEntryLocalService.deleteEntry(announcementsEntry);

		announcementsEntry = _addEntry(classNameId, user1.getUserId());

		Assert.assertEquals(user1.getUserId(), announcementsEntry.getClassPK());

		_announcementsEntryLocalService.deleteEntry(announcementsEntry);

		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);
	}

	private AnnouncementsEntry _addEntry(long classNameId, long classPK)
		throws Exception {

		return _announcementsEntryService.addEntry(
			classNameId, classPK, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), "http://localhost", "general",
			new Date(), new Date(System.currentTimeMillis() + Time.DAY), 0,
			false);
	}

	@Inject
	private AnnouncementsEntryLocalService _announcementsEntryLocalService;

	@Inject
	private AnnouncementsEntryService _announcementsEntryService;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}