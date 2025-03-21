/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.web.internal.notifications;

import com.liferay.flags.web.internal.constants.FlagsPortletKeys;
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
 * @author Roberto Díaz
 */
@Component(
	property = "jakarta.portlet.name=" + FlagsPortletKeys.FLAGS,
	service = UserNotificationHandler.class
)
public class FlagsUserNotificationHandler extends BaseUserNotificationHandler {

	public FlagsUserNotificationHandler() {
		setPortletId(FlagsPortletKeys.FLAGS);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		JSONObject contextJSONObject = jsonObject.getJSONObject("context");

		return StringUtil.replace(
			getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
			new String[] {
				_language.format(
					serviceContext.getLocale(),
					"a-x-named-x-was-flagged-as-x-by-x",
					new String[] {
						_getEscapedValue(
							contextJSONObject.getJSONObject(
								"[$CONTENT_TYPE$]")),
						_getEscapedValue(
							contextJSONObject.getJSONObject(
								"[$CONTENT_TITLE$]")),
						_getEscapedValue(
							contextJSONObject.getJSONObject("[$REASON|uri$]")),
						_getEscapedValue(
							contextJSONObject.getJSONObject(
								"[$REPORTER_USER_NAME$]"))
					}),
				_language.format(
					serviceContext.getLocale(),
					"inappropriate-content-flagged-in-x",
					_getEscapedValue(
						contextJSONObject.getJSONObject("[$SITE_NAME$]")))
			});
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		JSONObject contextJSONObject = jsonObject.getJSONObject("context");

		return _getEscapedValue(
			contextJSONObject.getJSONObject("[$CONTENT_URL$]"));
	}

	private String _getEscapedValue(JSONObject jsonObject) {
		return jsonObject.getString("escapedValue");
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}