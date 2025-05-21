/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.permission.resource;

import com.liferay.alloy.mvc.AlloyPermission;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Zsolt Balogh
 */
public class PatcherPermission extends AlloyPermission {

	public static boolean contains(
		ThemeDisplay themeDisplay, BaseModel<?> scopeBaseModel,
		String actionId) {

		return contains(
			themeDisplay, scopeBaseModel, actionId,
			themeDisplay.getScopeGroupId());
	}

	public static boolean contains(
		ThemeDisplay themeDisplay, BaseModel<?> scopeBaseModel, String actionId,
		long groupId) {

		if (scopeBaseModel == null) {
			return false;
		}

		return contains(
			getPermissionChecker(themeDisplay), groupId,
			scopeBaseModel.getModelClassName(),
			GetterUtil.getLong(scopeBaseModel.getPrimaryKeyObj()),
			formatAction(actionId));
	}

	public static boolean contains(
		ThemeDisplay themeDisplay, String controller, String action) {

		if (contains(
				getPermissionChecker(themeDisplay),
				themeDisplay.getScopeGroupId(), PatcherPortletKeys.PATCHER,
				themeDisplay.getScopeGroupId(),
				formatActionId(controller, action))) {

			return true;
		}

		return false;
	}

	public static boolean contains(
		ThemeDisplay themeDisplay, String controller, String action,
		long... groupIds) {

		return contains(
			themeDisplay, null, controller, formatAction(action), groupIds);
	}

	public static boolean contains(
		ThemeDisplay themeDisplay, String name, String controller,
		String action, long... groupIds) {

		String actionId = formatActionId(controller, action);

		if ((name == null) || name.isEmpty()) {
			name = PatcherPortletKeys.PATCHER;
		}
		else {
			actionId = actionId.substring(
				0, actionId.indexOf(StringPool.POUND));
		}

		for (long groupId : groupIds) {
			if (contains(
					getPermissionChecker(themeDisplay), groupId, name, groupId,
					actionId)) {

				return true;
			}
		}

		return false;
	}

	public static PermissionChecker getPermissionChecker(
		ThemeDisplay themeDisplay) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			permissionChecker = themeDisplay.getPermissionChecker();
		}

		return permissionChecker;
	}

}