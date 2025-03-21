/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.web.internal.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlEscapableObject;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.UserNotificationEventImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class FlagsUserNotificationHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testBodyShouldBeEscaped() throws Exception {
		UserNotificationEvent userNotificationEvent =
			new UserNotificationEventImpl();

		String userName = "'\"></option><img src=x onerror=alert(userName)>";
		long groupId = TestPropsValues.getGroupId();
		String content = "'\"></option><img src=x onerror=alert(content)>";

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		MBMessage mbMessage = MBMessageLocalServiceUtil.addMessage(
			null, TestPropsValues.getUserId(), userName, groupId,
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, 0L,
			MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID,
			StringUtil.randomString(), content,
			MBMessageConstants.DEFAULT_FORMAT, null, false, 0.0, false,
			serviceContext);

		MBThread mbThread = mbMessage.getThread();

		String siteName = "'\"></option><img src=x onerror=alert(siteName)>";

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"className", MBThread.class.getName()
			).put(
				"classPK", mbThread.getThreadId()
			).put(
				"context",
				_getContext(
					content,
					ResourceActionsUtil.getModelResource(
						serviceContext.getLocale(), MBThread.class.getName()),
					siteName, userName)
			).put(
				"notificationType",
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY
			).put(
				"portletId", "com_liferay_flags_web_portlet_FlagsPortlet"
			).toString());

		UserNotificationFeedEntry userNotificationFeedEntry =
			_userNotificationHandler.interpret(
				userNotificationEvent, serviceContext);

		String body = userNotificationFeedEntry.getBody();

		Assert.assertTrue(
			String.format("%s should be escaped", userName),
			body.contains(HtmlUtil.escape(userName)));
		Assert.assertTrue(
			String.format("%s should be escaped", content),
			body.contains(HtmlUtil.escape(content)));
		Assert.assertTrue(
			String.format("%s should be escaped", siteName),
			body.contains(HtmlUtil.escape(siteName)));
	}

	@Test
	public void testGetBody() throws Exception {
		UserNotificationEvent userNotificationEvent =
			new UserNotificationEventImpl();

		long groupId = TestPropsValues.getGroupId();
		String content = "#63;";

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		MBMessage mbMessage = MBMessageLocalServiceUtil.addMessage(
			null, TestPropsValues.getUserId(), StringUtil.randomString(),
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, 0L,
			MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID,
			StringUtil.randomString(), content,
			MBMessageConstants.DEFAULT_FORMAT, null, false, 0.0, false,
			serviceContext);

		MBThread mbThread = mbMessage.getThread();

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"className", MBThread.class.getName()
			).put(
				"classPK", mbThread.getThreadId()
			).put(
				"context",
				_getContext(
					content,
					ResourceActionsUtil.getModelResource(
						serviceContext.getLocale(), MBThread.class.getName()),
					StringUtil.randomString(), StringUtil.randomString())
			).put(
				"notificationType",
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY
			).put(
				"portletId", "com_liferay_flags_web_portlet_FlagsPortlet"
			).toString());

		UserNotificationFeedEntry userNotificationFeedEntry =
			_userNotificationHandler.interpret(
				userNotificationEvent, serviceContext);

		String body = userNotificationFeedEntry.getBody();

		Assert.assertTrue(
			String.format("%s should contain %s", body, content),
			body.contains(content));
	}

	private Map<String, HtmlEscapableObject<String>> _getContext(
		String content, String contentType, String siteName, String userName) {

		return HashMapBuilder.<String, HtmlEscapableObject<String>>put(
			"[$CONTENT_TITLE$]", new HtmlEscapableObject<>(content)
		).put(
			"[$CONTENT_TYPE$]", new HtmlEscapableObject<>(contentType)
		).put(
			"[$CONTENT_URL$]",
			new HtmlEscapableObject<>(StringUtil.randomString())
		).put(
			"[$REASON|uri$]",
			new HtmlEscapableObject<>(StringUtil.randomString())
		).put(
			"[$REPORTER_USER_NAME$]", new HtmlEscapableObject<>(userName)
		).put(
			"[$SITE_NAME$]", new HtmlEscapableObject<>(siteName)
		).build();
	}

	@Inject(
		filter = "jakarta.portlet.name=com_liferay_flags_web_portlet_FlagsPortlet"
	)
	private UserNotificationHandler _userNotificationHandler;

}