/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.user.notification.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.user.notification.client.dto.v1_0.UserNotification;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.NotificationEvent;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Date;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class UserNotificationResourceTest
	extends BaseUserNotificationResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_irrelevantUser = UserTestUtil.addGroupAdminUser(irrelevantGroup);
		_testUser = UserTestUtil.addGroupAdminUser(testGroup);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"read"};
	}

	@Override
	protected UserNotification
			testGetMyUserNotificationsPage_addUserNotification(
				UserNotification userNotification)
		throws Exception {

		return _addUserNotification(
			false, testGroup.getCreatorUserId(), userNotification);
	}

	@Override
	protected UserNotification
			testGetUserAccountUserNotificationsPage_addUserNotification(
				Long userAccountId, UserNotification userNotification)
		throws Exception {

		return _addUserNotification(false, userAccountId, userNotification);
	}

	@Override
	protected Long
			testGetUserAccountUserNotificationsPage_getIrrelevantUserAccountId()
		throws Exception {

		return _irrelevantUser.getUserId();
	}

	@Override
	protected Long testGetUserAccountUserNotificationsPage_getUserAccountId()
		throws Exception {

		return _testUser.getUserId();
	}

	@Override
	protected UserNotification testGetUserNotification_addUserNotification()
		throws Exception {

		return _addUserNotification(false);
	}

	@Override
	protected UserNotification
			testGraphQLGetUserNotification_addUserNotification()
		throws Exception {

		return _addUserNotification(false);
	}

	@Override
	protected UserNotification testPutUserNotificationRead_addUserNotification()
		throws Exception {

		return _addUserNotification(true);
	}

	@Override
	protected UserNotification
			testPutUserNotificationUnread_addUserNotification()
		throws Exception {

		return _addUserNotification(false);
	}

	private UserNotification _addUserNotification(boolean read)
		throws Exception {

		return _addUserNotification(read, _testUser.getUserId(), null);
	}

	private UserNotification _addUserNotification(
			boolean read, long userId, UserNotification userNotification)
		throws Exception {

		Date date = RandomTestUtil.nextDate();

		if (userNotification != null) {
			date = userNotification.getDateCreated();
		}

		UserNotificationEvent userNotificationEvent =
			_userNotificationEventLocalService.addUserNotificationEvent(
				userId,
				new NotificationEvent(
					date.getTime(), RandomTestUtil.randomString(),
					_jsonFactory.createJSONObject()));

		if (read) {
			userNotificationEvent =
				_userNotificationEventLocalService.updateUserNotificationEvent(
					userNotificationEvent.getUuid(),
					userNotificationEvent.getCompanyId(), read);
		}

		return userNotificationResource.getUserNotification(
			userNotificationEvent.getUserNotificationEventId());
	}

	private User _irrelevantUser;

	@Inject
	private JSONFactory _jsonFactory;

	private User _testUser;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}