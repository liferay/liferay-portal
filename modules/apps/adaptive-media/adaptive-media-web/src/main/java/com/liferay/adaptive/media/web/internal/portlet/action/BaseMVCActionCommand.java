/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.portlet.action;

import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

/**
 * @author Sergio González
 */
public abstract class BaseMVCActionCommand
	extends com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand {

	protected abstract void doPermissionCheckedProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception;

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_checkPermission(themeDisplay.getPermissionChecker());

		doPermissionCheckedProcessAction(actionRequest, actionResponse);
	}

	private void _checkPermission(PermissionChecker permissionChecker)
		throws Exception {

		if (!permissionChecker.isCompanyAdmin()) {
			throw new PrincipalException();
		}
	}

}