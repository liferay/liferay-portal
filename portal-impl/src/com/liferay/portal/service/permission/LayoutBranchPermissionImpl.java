/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutBranchLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutBranchPermission;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutBranchPermissionImpl implements LayoutBranchPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, LayoutBranch layoutBranch,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layoutBranch, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutBranch.class.getName(),
				layoutBranch.getLayoutBranchId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layoutBranchId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutBranch.class.getName(), layoutBranchId,
				actionId);
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, LayoutBranch layoutBranch,
		String actionId) {

		return permissionChecker.hasPermission(
			layoutBranch.getGroupId(), LayoutBranch.class.getName(),
			layoutBranch.getLayoutBranchId(), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long layoutBranchId,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			LayoutBranchLocalServiceUtil.getLayoutBranch(layoutBranchId),
			actionId);
	}

}