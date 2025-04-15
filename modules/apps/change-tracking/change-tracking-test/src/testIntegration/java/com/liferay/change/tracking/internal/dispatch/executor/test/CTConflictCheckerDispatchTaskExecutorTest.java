/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.dispatch.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.internal.test.util.CTCollectionTestUtil;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTConflictCheckerDispatchTaskExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testExecute() throws Exception {
		User user = UserTestUtil.addUser();

		CTCollection ctCollection =
			CTCollectionTestUtil.createCTCollectionWithConflict(user);

		ctCollection.setStatus(WorkflowConstants.STATUS_SCHEDULED);

		ctCollection = _ctCollectionLocalService.updateCTCollection(
			ctCollection);

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.fetchDispatchTrigger(
				TestPropsValues.getCompanyId(),
				"scheduled-publications-conflict-checks");

		_simulateSchedulerEvent(dispatchTrigger.getDispatchTriggerId());

		List<DispatchLog> dispatchLogs =
			_dispatchLogLocalService.getDispatchLogs(
				dispatchTrigger.getDispatchTriggerId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		DispatchLog dispatchLog = dispatchLogs.get(0);

		Assert.assertEquals(
			DispatchTaskStatus.SUCCESSFUL,
			DispatchTaskStatus.valueOf(dispatchLog.getStatus()));

		UserNotificationEvent userNotificationEvent = _getUserNotificationEvent(
			ctCollection.getCtCollectionId(), user.getUserId());

		Assert.assertNotNull(userNotificationEvent);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(new MockHttpServletRequest());

		UserNotificationFeedEntry userNotificationFeedEntry =
			_userNotificationHandler.interpret(
				userNotificationEvent, serviceContext);

		Assert.assertEquals(
			StringBundler.concat(
				"<div class=\"title\">", ctCollection.getName(),
				" scheduled publication has conflicts with production.</div>",
				"<div class=\"body\">Click on this notification to see the ",
				"list of conflicts that need to be manually resolved.</div>"),
			userNotificationFeedEntry.getBody());
	}

	private UserNotificationEvent _getUserNotificationEvent(
			long ctCollectionId, long userId)
		throws Exception {

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				userId);

		for (UserNotificationEvent userNotificationEvent :
				userNotificationEvents) {

			if (!Objects.equals(
					CTPortletKeys.PUBLICATIONS,
					userNotificationEvent.getType())) {

				continue;
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				userNotificationEvent.getPayload());

			if ((jsonObject.getLong("ctCollectionId") == ctCollectionId) &&
				(jsonObject.getInt("notificationType") ==
					UserNotificationDefinition.
						NOTIFICATION_TYPE_REVIEW_ENTRY) &&
				(jsonObject.getBoolean("showConflicts") == true)) {

				return userNotificationEvent;
			}
		}

		return null;
	}

	private void _simulateSchedulerEvent(long dispatchTriggerId)
		throws Exception {

		Message message = new Message();

		message.setPayload(
			String.format("{\"dispatchTriggerId\": %d}", dispatchTriggerId));

		_messageListener.receive(message);
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DispatchLogLocalService _dispatchLogLocalService;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "destination.name=" + DispatchConstants.EXECUTOR_DESTINATION_NAME
	)
	private MessageListener _messageListener;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Inject(filter = "jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS)
	private UserNotificationHandler _userNotificationHandler;

}