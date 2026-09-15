/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutSetBranchPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker,
			LayoutSetBranch layoutSetBranch, String actionId)
		throws PortalException {

		_layoutSetBranchPermission.check(
			permissionChecker, layoutSetBranch, actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, long layoutSetBranchId,
			String actionId)
		throws PortalException {

		_layoutSetBranchPermission.check(
			permissionChecker, layoutSetBranchId, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, LayoutSetBranch layoutSetBranch,
		String actionId) {

		return _layoutSetBranchPermission.contains(
			permissionChecker, layoutSetBranch, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long layoutSetBranchId,
			String actionId)
		throws PortalException {

		return _layoutSetBranchPermission.contains(
			permissionChecker, layoutSetBranchId, actionId);
	}

	public static LayoutSetBranchPermission getLayoutSetBranchPermission() {
		return _layoutSetBranchPermission;
	}

	public void setLayoutSetBranchPermission(
		LayoutSetBranchPermission layoutSetBranchPermission) {

		_layoutSetBranchPermission = layoutSetBranchPermission;
	}

	private static LayoutSetBranchPermission _layoutSetBranchPermission;

}