/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.security.permission.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryPermissionUtil {

	public static boolean hasAccountEntryPermission(
			AccountEntry accountEntry, String actionId, String name,
			PermissionChecker permissionChecker)
		throws PortalException {

		if (permissionChecker.hasPermission(
				accountEntry.getAccountEntryGroupId(), name, 0, actionId)) {

			return true;
		}

		for (AccountEntryOrganizationRel accountEntryOrganizationRel :
				AccountEntryOrganizationRelLocalServiceUtil.
					getAccountEntryOrganizationRels(
						accountEntry.getAccountEntryId())) {

			Organization organization =
				accountEntryOrganizationRel.getOrganization();

			if (permissionChecker.hasPermission(
					organization.getGroupId(), name, 0, actionId)) {

				return true;
			}

			for (Organization ancestorOrganization :
					organization.getAncestors()) {

				if (permissionChecker.hasPermission(
						ancestorOrganization.getGroupId(), name, 0, actionId)) {

					return true;
				}
			}
		}

		return false;
	}

}