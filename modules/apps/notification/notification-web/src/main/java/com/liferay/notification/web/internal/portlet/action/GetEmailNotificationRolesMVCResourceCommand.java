/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.action;

import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"jakarta.portlet.name=" + NotificationPortletKeys.NOTIFICATION_TEMPLATES,
		"mvc.command.name=/notification_templates/get_email_notification_roles"
	},
	service = MVCResourceCommand.class
)
public class GetEmailNotificationRolesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray accountRolesJSONArray = _jsonFactory.createJSONArray();
		JSONArray organizationRolesJSONArray = _jsonFactory.createJSONArray();
		JSONArray regularRolesJSONArray = _jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (Role role :
				_roleLocalService.getRoles(
					themeDisplay.getCompanyId(),
					new int[] {
						RoleConstants.TYPE_ACCOUNT,
						RoleConstants.TYPE_ORGANIZATION,
						RoleConstants.TYPE_REGULAR
					})) {

			JSONObject roleJSONObject = JSONUtil.put(
				"label", role.getTitle(themeDisplay.getLocale())
			).put(
				"name", role.getName()
			);

			if (role.getType() == RoleConstants.TYPE_ACCOUNT) {
				accountRolesJSONArray.put(roleJSONObject);
			}
			else if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
				organizationRolesJSONArray.put(roleJSONObject);
			}
			else {
				if (StringUtil.equals(role.getName(), RoleConstants.GUEST)) {
					continue;
				}

				regularRolesJSONArray.put(roleJSONObject);
			}
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"accountRoles", accountRolesJSONArray
			).put(
				"organizationRoles", organizationRolesJSONArray
			).put(
				"regularRoles", regularRolesJSONArray
			));
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RoleLocalService _roleLocalService;

}