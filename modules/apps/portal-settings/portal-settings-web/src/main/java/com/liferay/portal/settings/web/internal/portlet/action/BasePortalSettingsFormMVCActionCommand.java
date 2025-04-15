/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseFormMVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.settings.portlet.action.PortalSettingsFormContributor;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

/**
 * @author Michael C. Han
 */
public abstract class BasePortalSettingsFormMVCActionCommand
	extends BaseFormMVCActionCommand {

	public BasePortalSettingsFormMVCActionCommand(
		PortalSettingsFormContributor portalSettingsFormContributor) {

		this.portalSettingsFormContributor = portalSettingsFormContributor;
	}

	protected String getSettingsId() {
		return portalSettingsFormContributor.getSettingsId();
	}

	protected boolean hasPermissions(
		ActionRequest actionRequest, ActionResponse actionResponse,
		ThemeDisplay themeDisplay) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin(themeDisplay.getCompanyId())) {
			SessionErrors.add(actionRequest, PrincipalException.class);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return false;
		}

		return true;
	}

	protected final PortalSettingsFormContributor portalSettingsFormContributor;

}