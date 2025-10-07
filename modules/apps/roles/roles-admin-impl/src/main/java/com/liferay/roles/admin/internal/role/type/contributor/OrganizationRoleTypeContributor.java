/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.internal.role.type.contributor;

import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "service.ranking:Integer=300",
	service = RoleTypeContributor.class
)
public class OrganizationRoleTypeContributor implements RoleTypeContributor {

	@Override
	public String[] getExcludedRoleNames() {
		return new String[] {
			RoleConstants.ORGANIZATION_ADMINISTRATOR,
			RoleConstants.ORGANIZATION_OWNER
		};
	}

	@Override
	public String getIcon() {
		return "community";
	}

	@Override
	public String getName() {
		return "organization";
	}

	@Override
	public String[] getSubtypes() {
		return PropsValues.ROLES_ORGANIZATION_SUBTYPES;
	}

	@Override
	public String getTabTitle(Locale locale) {
		return "organization-roles";
	}

	@Override
	public String getTitle(Locale locale) {
		return "organization-role";
	}

	@Override
	public int getType() {
		return RoleConstants.TYPE_ORGANIZATION;
	}

	@Override
	public boolean isAllowAssignMembers(Role role) {
		return false;
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
		return Objects.equals(RoleConstants.ORGANIZATION_USER, role.getName());
	}

	@Reference
	private Portal _portal;

}