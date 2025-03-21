/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.notifications;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(
	property = "jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
	service = UserNotificationHandler.class
)
public class MFASystemConfigurationUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public MFASystemConfigurationUserNotificationHandler() {
		setPortletId(ConfigurationAdminPortletKeys.INSTANCE_SETTINGS);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		boolean mfaDisableGlobally = jsonObject.getBoolean(
			"mfaDisableGlobally");

		String body = null;

		if (mfaDisableGlobally) {
			body = _language.get(
				serviceContext.getLocale(),
				"multi-factor-authentication-has-been-disabled-by-the-system-" +
					"administrator-and-is-unavailable-to-all-instances");
		}
		else {
			body = _language.get(
				serviceContext.getLocale(),
				"multi-factor-authentication-has-been-enabled-by-the-system-" +
					"administrator");
		}

		return StringUtil.replace(
			getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
			new String[] {
				body, getTitle(userNotificationEvent, serviceContext)
			});
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return _language.get(
			serviceContext.getLocale(), "multi-factor-authentication");
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}