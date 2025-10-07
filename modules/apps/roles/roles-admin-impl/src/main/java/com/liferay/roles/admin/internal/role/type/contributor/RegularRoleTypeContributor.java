/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.internal.role.type.contributor;

import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "service.ranking:Integer=100",
	service = RoleTypeContributor.class
)
public class RegularRoleTypeContributor implements RoleTypeContributor {

	@Override
	public String getIcon() {
		return "user";
	}

	@Override
	public String getName() {
		return "regular";
	}

	@Override
	public String[] getSubtypes() {
		return PropsValues.ROLES_REGULAR_SUBTYPES;
	}

	@Override
	public String getTabTitle(Locale locale) {
		return "regular-roles";
	}

	@Override
	public String getTitle(Locale locale) {
		return "regular-role";
	}

	@Override
	public int getType() {
		return RoleConstants.TYPE_REGULAR;
	}

	@Override
	public boolean isAllowAssignMembers(Role role) {
		return !ArrayUtil.contains(
			_AUTOMATICALLY_ASSIGNED_ROLE_NAMES, role.getName());
	}

	@Override
	public boolean isAllowDefinePermissions(Role role) {
		String name = role.getName();

		if (name.equals(RoleConstants.ADMINISTRATOR) ||
			name.equals(RoleConstants.ANALYTICS_ADMINISTRATOR) ||
			name.equals(RoleConstants.OWNER)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isAllowDelete(Role role) {
		if (role == null) {
			return false;
		}

		return !_portal.isSystemRole(role.getName());
	}

	@Override
	public boolean isAutomaticallyAssigned(Role role) {
		return ArrayUtil.contains(
			_AUTOMATICALLY_ASSIGNED_ROLE_NAMES, role.getName());
	}

	private static final String[] _AUTOMATICALLY_ASSIGNED_ROLE_NAMES = {
		RoleConstants.GUEST, RoleConstants.OWNER, RoleConstants.USER
	};

	@Reference
	private Portal _portal;

}