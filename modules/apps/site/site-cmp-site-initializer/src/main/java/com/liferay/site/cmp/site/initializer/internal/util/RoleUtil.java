/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.util;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;

/**
 * @author Pedro Leite
 */
public class RoleUtil {

	public static Role getOrAddProjectRole(
			long companyId, String name, long userId)
		throws Exception {

		Role role = RoleLocalServiceUtil.fetchRole(companyId, name);

		if (role != null) {
			return role;
		}

		return RoleLocalServiceUtil.addRole(
			RoleConstants.toSystemRoleExternalReferenceCode(name), userId, null,
			0, name, null, null, RoleConstants.TYPE_DEPOT,
			DepotRolesConstants.SUBTYPE_PROJECT, null);
	}

}