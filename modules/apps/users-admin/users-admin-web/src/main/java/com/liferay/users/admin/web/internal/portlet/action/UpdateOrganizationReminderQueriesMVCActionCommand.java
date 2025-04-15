/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/update_organization_reminder_queries"
	},
	service = MVCActionCommand.class
)
public class UpdateOrganizationReminderQueriesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateReminderQueries(actionRequest);
		}
		catch (Exception exception) {
			String mvcPath = "/edit_organization.jsp";

			if (exception instanceof NoSuchOrganizationException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				mvcPath = "/error.jsp";
			}

			actionResponse.setRenderParameter("mvcPath", mvcPath);
		}
	}

	private void _updateReminderQueries(PortletRequest portletRequest)
		throws Exception {

		long organizationId = ParamUtil.getLong(
			portletRequest, "organizationId");

		Organization organization = _organizationService.getOrganization(
			organizationId);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OrganizationPermissionUtil.check(
			themeDisplay.getPermissionChecker(), organization,
			ActionKeys.UPDATE);

		String reminderQueries = portletRequest.getParameter("reminderQueries");

		PortletPreferences portletPreferences = organization.getPreferences();

		_localization.setLocalizedPreferencesValues(
			portletRequest, portletPreferences, "reminderQueries");

		portletPreferences.setValue("reminderQueries", reminderQueries);

		portletPreferences.store();
	}

	@Reference
	private Localization _localization;

	@Reference
	private OrganizationService _organizationService;

}