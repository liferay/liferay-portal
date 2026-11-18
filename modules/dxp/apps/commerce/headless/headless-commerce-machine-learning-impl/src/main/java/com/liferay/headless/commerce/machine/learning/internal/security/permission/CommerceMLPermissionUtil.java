/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.security.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;

/**
 * @author Stefano Motta
 */
public class CommerceMLPermissionUtil {

	public static void check(long companyId, RoleLocalService roleLocalService)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			throw new PrincipalException(
				"Unable to access commerce machine learning entities without " +
					"a permission checker");
		}

		if (permissionChecker.isCompanyAdmin(companyId) ||
			roleLocalService.hasUserRole(
				permissionChecker.getUserId(), companyId,
				RoleConstants.ANALYTICS_ADMINISTRATOR, true)) {

			return;
		}

		throw new PrincipalException.MustHavePermission(
			permissionChecker.getUserId(), Company.class.getName(), companyId,
			ActionKeys.VIEW);
	}

}