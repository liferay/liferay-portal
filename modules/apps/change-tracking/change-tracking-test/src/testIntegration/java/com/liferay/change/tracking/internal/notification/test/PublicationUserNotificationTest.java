/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.notification.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.internal.test.util.CTCollectionTestUtil;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class PublicationUserNotificationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = CTCollectionTestUtil.createCTCollectionWithConflict(
			TestPropsValues.getUser());

		_group = _groupLocalService.addGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			CTCollection.class.getName(), _ctCollection.getCtCollectionId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			RandomTestUtil.randomLocaleStringMap(), null,
			GroupConstants.TYPE_SITE_OPEN, null, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, false,
			true, null);

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testUserNotificationForPublicationAdministrator()
		throws Exception {

		_addPublicationUserGroupRole(PublicationRoleConstants.NAME_ADMIN);

		CTCollectionTestUtil.publishCTCollectionWithError(
			_ctCollection.getCtCollectionId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				_user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());
	}

	@Test
	public void testUserNotificationForPublicationEditor() throws Exception {
		_addPublicationUserGroupRole(PublicationRoleConstants.NAME_EDITOR);

		CTCollectionTestUtil.publishCTCollectionWithError(
			_ctCollection.getCtCollectionId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				_user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());
	}

	@Test
	public void testUserNotificationForPublicationOwner() throws Exception {
		CTCollection ctCollection =
			CTCollectionTestUtil.createCTCollectionWithConflict(_user);

		CTCollectionTestUtil.publishCTCollectionWithError(
			ctCollection.getCtCollectionId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				_user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());
	}

	@Test
	public void testUserNotificationForPublicationPublisher() throws Exception {
		_addPublicationUserGroupRole(PublicationRoleConstants.NAME_PUBLISHER);

		CTCollectionTestUtil.publishCTCollectionWithError(
			_ctCollection.getCtCollectionId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				_user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());
	}

	@Test
	public void testUserNotificationForPublicationViewer() throws Exception {
		_addPublicationUserGroupRole(PublicationRoleConstants.NAME_VIEWER);

		CTCollectionTestUtil.publishCTCollectionWithError(
			_ctCollection.getCtCollectionId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				_user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 0,
			userNotificationEvents.size());
	}

	@Test
	public void testUserWithMultipleRolesNotificationEventsCount()
		throws Exception {

		_addPublicationUserGroupRole(PublicationRoleConstants.NAME_ADMIN);

		_addPublicationUserGroupRole(PublicationRoleConstants.NAME_EDITOR);

		CTCollectionTestUtil.publishCTCollectionWithError(
			_ctCollection.getCtCollectionId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				_user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());
	}

	private void _addPublicationUserGroupRole(String roleName)
		throws Exception {

		Role role = _roleLocalService.fetchRole(_user.getCompanyId(), roleName);

		if (role == null) {
			role = RoleTestUtil.addRole(
				roleName, RoleConstants.TYPE_PUBLICATIONS);
		}

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), _group.getGroupId(), role.getRoleId());
	}

	private CTCollection _ctCollection;
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}