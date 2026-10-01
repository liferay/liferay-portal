/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;

/**
 * @author Pedro Leite
 */
public class RoleUtil {

	public static Role getOrAddCMSAdministratorRole(long companyId, long userId)
		throws Exception {

		String name = RoleConstants.CMS_ADMINISTRATOR;

		Role role = RoleLocalServiceUtil.fetchRole(companyId, name);

		if (role != null) {
			return role;
		}

		return RoleLocalServiceUtil.addRole(
			RoleConstants.toSystemRoleExternalReferenceCode(name), userId, null,
			0, name, null, null, RoleConstants.TYPE_REGULAR, null, null);
	}

}