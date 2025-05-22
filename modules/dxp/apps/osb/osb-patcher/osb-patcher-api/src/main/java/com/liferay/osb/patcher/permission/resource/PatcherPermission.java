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
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherPermission {

	public static boolean contains(
		PermissionChecker permissionChecker, long groupId, String name,
		long primKey, String actionId) {

		return contains(permissionChecker, groupId, name, primKey, actionId, 0);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long groupId, String name,
		long primKey, String actionId, long ownerId) {

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			name);

		if (!resourceActions.contains(actionId)) {
			return true;
		}

		if (name.indexOf(CharPool.PERIOD) != -1) {
			if (ownerId <= 0) {
				ownerId = getOwnerId(name, primKey);
			}

			if (permissionChecker.hasOwnerPermission(
					permissionChecker.getCompanyId(), name, primKey, ownerId,
					actionId)) {

				return true;
			}
		}

		return permissionChecker.hasPermission(
			groupId, name, primKey, actionId);
	}

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

	protected static long getOwnerId(String className, long classPK) {
		BaseModel<?> baseModel = null;

		try {
			AlloyServiceInvoker alloyServiceInvoker = new AlloyServiceInvoker(
				className);

			baseModel = alloyServiceInvoker.fetchModel(classPK);
		}
		catch (Exception e) {
		}

		return BeanPropertiesUtil.getLongSilent(baseModel, "userId");
	}

}