/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.util.v1_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;

import java.util.List;

/**
 * @author Stefano Motta
 */
public class ResourcePermissionUtil {

	public static <T> T setResourcePermissions(
			BaseModel<T> baseModel, long companyId, Permission[] permissions,
			ResourcePermissionLocalService resourcePermissionLocalService,
			RoleService roleService,
			RoleTypeContributorProvider roleTypeContributorProvider)
		throws Exception {

		if (ArrayUtil.isEmpty(permissions)) {
			return baseModel.toUnescapedModel();
		}

		for (Permission permission : permissions) {
			String[] actionIds = permission.getActionIds();
			String externalReferenceCode =
				permission.getRoleExternalReferenceCode();
			String name = permission.getRoleName();

			if (ArrayUtil.isEmpty(actionIds) ||
				Validator.isNull(externalReferenceCode) ||
				Validator.isNull(name)) {

				return baseModel.toUnescapedModel();
			}

			String className = StringPool.BLANK;

			List<RoleTypeContributor> roleTypeContributors = ListUtil.filter(
				roleTypeContributorProvider.getRoleTypeContributors(),
				roleTypeContributor -> {
					if (Validator.isNull(permission.getRoleType())) {
						return false;
					}

					return StringUtil.equals(
						roleTypeContributor.getTypeLabel(),
						permission.getRoleType());
				});

			if (ListUtil.isNotEmpty(roleTypeContributors)) {
				RoleTypeContributor roleTypeContributor =
					roleTypeContributors.get(0);

				className = roleTypeContributor.getClassName();
			}

			Role role = roleService.getOrAddEmptyRole(
				externalReferenceCode, className, 0, name,
				RoleConstants.getLabelType(permission.getRoleType()));

			resourcePermissionLocalService.setResourcePermissions(
				companyId, baseModel.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(
					GetterUtil.getLong(baseModel.getPrimaryKeyObj())),
				role.getRoleId(), actionIds);
		}

		return baseModel.toUnescapedModel();
	}

}