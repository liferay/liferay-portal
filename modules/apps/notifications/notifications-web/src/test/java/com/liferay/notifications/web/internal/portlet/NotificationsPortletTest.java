/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.portlet;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDelivery;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.UserNotificationDeliveryLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.model.impl.UserNotificationDeliveryImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Marco Galluzzi
 */
public class NotificationsPortletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_notificationsPortlet = new NotificationsPortlet();

		ReflectionTestUtil.setFieldValue(
			_notificationsPortlet, "_language", Mockito.mock(Language.class));
		ReflectionTestUtil.setFieldValue(
			_notificationsPortlet, "_userNotificationDeliveryLocalService",
			_userNotificationDeliveryLocalService);

		_user = new UserImpl();

		_user.setUserId(RandomTestUtil.randomLong());

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	@After
	public void tearDown() {
		_userNotificationManagerUtilMockedStatic.close();
	}

	@Test
	public void testUpdateUserNotificationDeliveryWithoutUserNotificationDefinition()
		throws Exception {

		UserNotificationDelivery userNotificationDelivery =
			_addUserNotificationDelivery();

		_setUserNotificationDefinition(null, userNotificationDelivery);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"userNotificationDeliveryIds",
			String.valueOf(
				userNotificationDelivery.getUserNotificationDeliveryId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				NotificationsPortlet.class.getName(), LoggerTestUtil.WARN)) {

			_notificationsPortlet.updateUserNotificationDelivery(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				_getExpectedWarningMessage(userNotificationDelivery),
				logEntry.getMessage());
		}
	}

	private UserNotificationDelivery _addUserNotificationDelivery() {
		UserNotificationDelivery userNotificationDelivery =
			new UserNotificationDeliveryImpl();

		userNotificationDelivery.setUserNotificationDeliveryId(
			RandomTestUtil.randomLong());
		userNotificationDelivery.setUserId(_user.getUserId());
		userNotificationDelivery.setPortletId(RandomTestUtil.randomString());
		userNotificationDelivery.setClassNameId(RandomTestUtil.randomLong());
		userNotificationDelivery.setNotificationType(
			RandomTestUtil.randomInt());

		Mockito.when(
			_userNotificationDeliveryLocalService.fetchUserNotificationDelivery(
				userNotificationDelivery.getUserNotificationDeliveryId())
		).thenReturn(
			userNotificationDelivery
		);

		return userNotificationDelivery;
	}

	private String _getExpectedWarningMessage(
		UserNotificationDelivery userNotificationDelivery) {

		return String.format(
			"No user notification definition found for class name ID %d, " +
				"notification type %d, and portlet %s",
			userNotificationDelivery.getClassNameId(),
			userNotificationDelivery.getNotificationType(),
			userNotificationDelivery.getPortletId());
	}

	private MockLiferayPortletActionRequest
		_getMockLiferayPortletActionRequest() {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setUser(_user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private void _setUserNotificationDefinition(
		UserNotificationDefinition userNotificationDefinition,
		UserNotificationDelivery userNotificationDelivery) {

		_userNotificationManagerUtilMockedStatic.when(
			() -> UserNotificationManagerUtil.fetchUserNotificationDefinition(
				userNotificationDelivery.getPortletId(),
				userNotificationDelivery.getClassNameId(),
				userNotificationDelivery.getNotificationType())
		).thenReturn(
			userNotificationDefinition
		);
	}

	private static final MockedStatic<UserNotificationManagerUtil>
		_userNotificationManagerUtilMockedStatic = Mockito.mockStatic(
			UserNotificationManagerUtil.class);

	private NotificationsPortlet _notificationsPortlet;
	private User _user;
	private final UserNotificationDeliveryLocalService
		_userNotificationDeliveryLocalService = Mockito.mock(
			UserNotificationDeliveryLocalService.class);

}