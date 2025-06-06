/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.permission.resource;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherPermission {

	public static boolean contains(
		PermissionChecker permissionChecker, BaseModel<?> scopeBaseModel,
		String actionId, long ownerId) {

		if (scopeBaseModel == null) {
			return false;
		}

		return contains(
			permissionChecker, GroupThreadLocal.getGroupId(),
			scopeBaseModel.getModelClassName(),
			GetterUtil.getLong(scopeBaseModel.getPrimaryKeyObj()),
			formatAction(actionId), ownerId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long groupId, String name,
		long primKey, String actionId, long ownerId) {

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			name);

		if (!resourceActions.contains(actionId)) {
			return true;
		}

		if ((name.indexOf(CharPool.PERIOD) != -1) && (ownerId > 0) &&
			permissionChecker.hasOwnerPermission(
				permissionChecker.getCompanyId(), name, primKey, ownerId,
				actionId)) {

			return true;
		}

		return permissionChecker.hasPermission(
			groupId, name, primKey, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, String controller, String action) {

		return contains(
			permissionChecker, GroupThreadLocal.getGroupId(),
			PatcherPortletKeys.PATCHER, GroupThreadLocal.getGroupId(),
			formatActionId(controller, action), 0);
	}

	protected static String formatAction(String action) {
		StringBuilder sb = new StringBuilder(StringUtil.toUpperCase(action));

		for (int i = 0; i < action.length(); i++) {
			char c = action.charAt(i);

			if (Character.isUpperCase(c) && (i > 0)) {
				int delta = sb.length() - action.length();

				sb.insert(i + delta, CharPool.UNDERLINE);

				if (((i + 1) >= action.length()) ||
					Character.isLowerCase(action.charAt(i + 1))) {

					continue;
				}

				while (i < action.length()) {
					c = action.charAt(i);

					if (Character.isLowerCase(c)) {
						break;
					}

					i++;
				}

				if (i == action.length()) {
					continue;
				}

				sb.insert(i + delta, CharPool.UNDERLINE);
			}
		}

		return sb.toString();
	}

	protected static String formatActionId(String controller, String action) {
		return formatAction(action) + StringPool.POUND +
			StringUtil.toUpperCase(controller);
	}

}