/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.action;

import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Paulo Albuquerque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + NotificationPortletKeys.NOTIFICATION_TEMPLATES,
		"mvc.command.name=/notification_templates/get_general_notification_template_terms"
	},
	service = MVCResourceCommand.class
)
public class GetGeneralNotificationTemplateTermsMVCResourceCommand
	extends BaseNotificationTemplateTermsMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			getTermsJSONArray(
				null, null,
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY)));
	}

	@Override
	protected Set<Map.Entry<String, String>> getTermNamesEntries(
		List<ObjectField> objectFields, String partialTermName,
		ThemeDisplay themeDisplay) {

		return _termNames.entrySet();
	}

	private final Map<String, String> _termNames = HashMapBuilder.put(
		"current-date", "[%CURRENT_DATE%]"
	).put(
		"current-user-email-address", "[%CURRENT_USER_EMAIL_ADDRESS%]"
	).put(
		"current-user-first-name", "[%CURRENT_USER_FIRST_NAME%]"
	).put(
		"current-user-id", "[%CURRENT_USER_ID%]"
	).put(
		"current-user-last-name", "[%CURRENT_USER_LAST_NAME%]"
	).put(
		"current-user-middle-name", "[%CURRENT_USER_MIDDLE_NAME%]"
	).put(
		"current-user-prefix", "[%CURRENT_USER_PREFIX%]"
	).put(
		"current-user-suffix", "[%CURRENT_USER_SUFFIX%]"
	).build();

}