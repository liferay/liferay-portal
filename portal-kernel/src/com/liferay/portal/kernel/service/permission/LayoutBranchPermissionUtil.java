/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutBranchPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker, LayoutBranch layoutBranch,
			String actionId)
		throws PortalException {

		_layoutBranchPermission.check(
			permissionChecker, layoutBranch, actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		_layoutBranchPermission.check(
			permissionChecker, layoutBranchId, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, LayoutBranch layoutBranch,
		String actionId) {

		return _layoutBranchPermission.contains(
			permissionChecker, layoutBranch, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		return _layoutBranchPermission.contains(
			permissionChecker, layoutBranchId, actionId);
	}

	public static LayoutBranchPermission getLayoutBranchPermission() {
		return _layoutBranchPermission;
	}

	public void setLayoutBranchPermission(
		LayoutBranchPermission layoutBranchPermission) {

		_layoutBranchPermission = layoutBranchPermission;
	}

	private static LayoutBranchPermission _layoutBranchPermission;

}