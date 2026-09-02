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

		String externalReferenceCode =
			RoleConstants.toSystemRoleExternalReferenceCode(
				RoleConstants.CMS_ADMINISTRATOR);

		Role role = RoleLocalServiceUtil.fetchRoleByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (role != null) {
			return role;
		}

		role = RoleLocalServiceUtil.fetchRole(
			companyId, RoleConstants.CMS_ADMINISTRATOR);

		if (role != null) {
			return role;
		}

		return RoleLocalServiceUtil.addRole(
			externalReferenceCode, userId, null, 0,
			RoleConstants.CMS_ADMINISTRATOR, null, null,
			RoleConstants.TYPE_REGULAR, null, null);
	}

}