/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Charles May
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public interface LayoutSetBranchPermission {

	public void check(
			PermissionChecker permissionChecker,
			LayoutSetBranch layoutSetBranch, String actionId)
		throws PortalException;

	public void check(
			PermissionChecker permissionChecker, long layoutSetBranchId,
			String actionId)
		throws PortalException;

	public boolean contains(
		PermissionChecker permissionChecker, LayoutSetBranch layoutSetBranch,
		String actionId);

	public boolean contains(
			PermissionChecker permissionChecker, long layoutSetBranchId,
			String actionId)
		throws PortalException;

}