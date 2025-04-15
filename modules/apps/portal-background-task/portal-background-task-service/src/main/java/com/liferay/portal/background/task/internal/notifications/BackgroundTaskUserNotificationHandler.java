/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.notifications;

import com.liferay.portal.background.task.constants.BackgroundTaskPortletKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(
	property = "jakarta.portlet.name=" + BackgroundTaskPortletKeys.BACKGROUND_TASK,
	service = UserNotificationHandler.class
)
public class BackgroundTaskUserNotificationHandler
	extends BaseUserNotificationHandler {

	public BackgroundTaskUserNotificationHandler() {
		setPortletId(BackgroundTaskPortletKeys.BACKGROUND_TASK);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		return StringUtil.replace(
			getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
			new String[] {
				_language.format(
					serviceContext.getLocale(), "background-task-has-failed",
					new String[] {
						jsonObject.getString("name"),
						jsonObject.getString("taskExecutorClassName")
					}),
				_language.get(serviceContext.getLocale(), "background-task")
			});
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}