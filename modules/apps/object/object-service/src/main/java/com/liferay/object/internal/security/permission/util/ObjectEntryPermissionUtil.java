/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.security.permission.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;

import java.util.Arrays;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryPermissionUtil {

	public static Predicate getPermissionWherePredicate(
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable, long groupId,
		InlineSQLHelper inlineSQLHelper) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return null;
		}

		ObjectDefinition objectDefinition =
			dynamicObjectDefinitionTable.getObjectDefinition();

		if (!inlineSQLHelper.isEnabled(
				objectDefinition.getCompanyId(), groupId)) {

			return null;
		}

		return inlineSQLHelper.getPermissionWherePredicate(
			objectDefinition.getClassName(),
			dynamicObjectDefinitionTable.getPrimaryKeyColumn(), groupId);
	}

	public static boolean hasAccountEntryPermission(
			AccountEntry accountEntry, String actionId, String name,
			PermissionChecker permissionChecker)
		throws PortalException {

		if (_hasPermission(
				actionId, accountEntry.getAccountEntryGroupId(), name,
				permissionChecker)) {

			return true;
		}

		for (AccountEntryOrganizationRel accountEntryOrganizationRel :
				AccountEntryOrganizationRelLocalServiceUtil.
					getAccountEntryOrganizationRels(
						accountEntry.getAccountEntryId())) {

			Organization organization =
				accountEntryOrganizationRel.getOrganization();

			if (_hasPermission(
					actionId, organization.getGroupId(), name,
					permissionChecker)) {

				return true;
			}

			for (Organization ancestorOrganization :
					organization.getAncestors()) {

				if (_hasPermission(
						actionId, ancestorOrganization.getGroupId(), name,
						permissionChecker)) {

					return true;
				}
			}
		}

		return false;
	}

	private static boolean _hasPermission(
			String actionId, long groupId, String name,
			PermissionChecker permissionChecker)
		throws PortalException {

		if (!permissionChecker.hasPermission(groupId, name, 0, actionId)) {
			return false;
		}

		long companyId = permissionChecker.getCompanyId();

		if (permissionChecker.isCompanyAdmin(companyId) ||
			!permissionChecker.isGroupAdmin(groupId)) {

			return true;
		}

		String primKey = "0";

		int resourcePermissionsCount =
			ResourcePermissionLocalServiceUtil.getResourcePermissionsCount(
				companyId, name, ResourceConstants.SCOPE_INDIVIDUAL, primKey);

		if (resourcePermissionsCount == 0) {
			primKey = name;
		}

		return ResourcePermissionLocalServiceUtil.hasResourcePermission(
			Arrays.asList(
				ResourceLocalServiceUtil.getResource(
					companyId, name, ResourceConstants.SCOPE_INDIVIDUAL,
					primKey),
				ResourceLocalServiceUtil.getResource(
					companyId, name, ResourceConstants.SCOPE_GROUP,
					String.valueOf(groupId)),
				ResourceLocalServiceUtil.getResource(
					companyId, name, ResourceConstants.SCOPE_GROUP_TEMPLATE,
					String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID)),
				ResourceLocalServiceUtil.getResource(
					companyId, name, ResourceConstants.SCOPE_COMPANY,
					String.valueOf(companyId))),
			permissionChecker.getRoleIds(
				permissionChecker.getUserId(), groupId),
			actionId);
	}

}