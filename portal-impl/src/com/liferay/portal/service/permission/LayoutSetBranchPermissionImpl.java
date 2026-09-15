/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutSetBranchPermission;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutSetBranchPermissionImpl
	implements LayoutSetBranchPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker,
			LayoutSetBranch layoutSetBranch, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layoutSetBranch, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutSetBranch.class.getName(),
				layoutSetBranch.getLayoutSetBranchId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long layoutSetBranchId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layoutSetBranchId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, LayoutSetBranch.class.getName(),
				layoutSetBranchId, actionId);
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, LayoutSetBranch layoutSetBranch,
		String actionId) {

		return permissionChecker.hasPermission(
			layoutSetBranch.getGroupId(), LayoutSetBranch.class.getName(),
			layoutSetBranch.getLayoutSetBranchId(), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long layoutSetBranchId,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			LayoutSetBranchLocalServiceUtil.getLayoutSetBranch(
				layoutSetBranchId),
			actionId);
	}

}