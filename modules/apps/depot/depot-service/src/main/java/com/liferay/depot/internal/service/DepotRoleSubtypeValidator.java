/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RoleSubtypeException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

/**
 * @author Lianne Louie
 */
public class DepotRoleSubtypeValidator {

	public static void validate(long groupId, long[] roleIds)
		throws PortalException {

		String subtype = DepotRoleUtil.getSubtype(groupId);

		if (Validator.isNull(subtype)) {
			return;
		}

		List<Role> roles = TransformUtil.unsafeTransformToList(
			roleIds,
			roleId -> {
				Role role = RoleLocalServiceUtil.getRole(roleId);

				if (role.getType() != RoleConstants.TYPE_DEPOT) {
					return null;
				}

				return role;
			});

		if (roles.isEmpty()) {
			return;
		}

		List<Role> filteredRoles = DepotRoleUtil.filter(roles, subtype);

		for (Role role : roles) {
			if (!filteredRoles.contains(role)) {
				throw new RoleSubtypeException(
					StringBundler.concat(
						"Unable to assign role ", role.getRoleId(),
						" with subtype \"", role.getSubtype(), "\" in group ",
						groupId));
			}
		}
	}

}
