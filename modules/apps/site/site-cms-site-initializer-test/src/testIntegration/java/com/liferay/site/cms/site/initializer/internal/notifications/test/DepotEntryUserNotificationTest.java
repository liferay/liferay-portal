/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DepotEntryUserNotificationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddGroupUser() throws Exception {
		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			_depotEntry.getGroupId(), user.getUserId());

		_assertUserNotificationEvent(user);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddUserUserGroup() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroup(
			_depotEntry.getGroupId(), userGroup);

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertUserNotificationEvent(user);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testInterpret() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroup(
			_depotEntry.getGroupId(), userGroup);

		User user1 = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user1.getUserId(), userGroup);

		User user2 = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user2.getUserId(), userGroup);

		_assertUserNotificationEvent(user1);
		_assertUserNotificationEvent(user2);

		_userLocalService.deleteUserGroupUser(
			userGroup.getUserGroupId(), user1.getUserId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setLanguageId("en_US");
		serviceContext.setUserId(user1.getUserId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user1.getUserId());

		_userNotificationHandler.interpret(
			userNotificationEvents.get(0), serviceContext);

		userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user1.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 0,
			userNotificationEvents.size());

		userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user2.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		_userGroupLocalService.deleteGroupUserGroup(
			_depotEntry.getGroupId(), userGroup.getUserGroupId());

		serviceContext.setUserId(user2.getUserId());

		_userNotificationHandler.interpret(
			userNotificationEvents.get(0), serviceContext);

		userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user2.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 0,
			userNotificationEvents.size());
	}

	private void _assertUserNotificationEvent(User user) {
		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationEvent userNotificationEvent =
			userNotificationEvents.get(0);

		Assert.assertEquals(
			DepotPortletKeys.DEPOT_ADMIN, userNotificationEvent.getType());

		String payload = userNotificationEvent.getPayload();

		Assert.assertTrue(
			payload.contains("classPK\":" + _depotEntry.getDepotEntryId()));
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Inject(filter = "jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN)
	private UserNotificationHandler _userNotificationHandler;

}