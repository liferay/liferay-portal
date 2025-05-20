/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyPermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.theme.ThemeDisplay;

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
				themeDisplay.getScopeGroupId(), PortletKeys.OSB_PATCHER,
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
			name = PortletKeys.OSB_PATCHER;
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