/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.roles.admin.role.type.contributor;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "service.ranking:Integer=400",
	service = RoleTypeContributor.class
)
public class DepotRoleTypeContributor implements RoleTypeContributor {

	@Override
	public String[] getExcludedRoleNames() {
		return _EXCLUDED_ROLE_NAMES;
	}

	@Override
	public String getIcon() {
		return "globe";
	}

	@Override
	public String getName() {
		if (FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-17564")) {

			return "depot";
		}

		return "asset-library";
	}

	@Override
	public String[] getSubtypes() {
		List<String> subtypes = new ArrayList<>(3);

		if (FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-57283")) {

			subtypes.add(DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY);
		}

		if (FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-58677")) {

			subtypes.add(DepotRolesConstants.SUBTYPE_PROJECT);
		}

		if (FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-17564")) {

			subtypes.add(DepotRolesConstants.SUBTYPE_SPACE);
		}

		return subtypes.toArray(new String[0]);
	}

	@Override
	public String getTabTitle(Locale locale) {
		if (FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-17564")) {

			return _language.get(locale, "depot-roles");
		}

		return _language.get(locale, "asset-library-roles");
	}

	@Override
	public String getTitle(Locale locale) {
		if (FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-17564")) {

			return _language.get(locale, "depot-role");
		}

		return _language.get(locale, "asset-library-role");
	}

	@Override
	public int getType() {
		return RoleConstants.TYPE_DEPOT;
	}

	@Override
	public boolean isAllowAssignMembers(Role role) {
		return false;
	}

	@Override
	public boolean isAllowDelete(Role role) {
		if ((role == null) ||
			Objects.equals(
				role.getName(),
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR) ||
			Objects.equals(
				role.getName(), DepotRolesConstants.ASSET_LIBRARY_MEMBER) ||
			Objects.equals(
				role.getName(),
				DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER) ||
			Objects.equals(
				role.getName(), DepotRolesConstants.ASSET_LIBRARY_OWNER) ||
			Objects.equals(
				role.getName(),
				DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR) ||
			Objects.equals(
				role.getName(), DepotRolesConstants.DESIGN_LIBRARY_MEMBER) ||
			Objects.equals(
				role.getName(), DepotRolesConstants.DESIGN_LIBRARY_OWNER)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isAutomaticallyAssigned(Role role) {
		if (Objects.equals(
				role.getName(), DepotRolesConstants.ASSET_LIBRARY_MEMBER) ||
			Objects.equals(
				role.getName(),
				DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER) ||
			Objects.equals(
				role.getName(), DepotRolesConstants.DESIGN_LIBRARY_MEMBER)) {

			return true;
		}

		return false;
	}

	private static final String[] _EXCLUDED_ROLE_NAMES = {
		DepotRolesConstants.ASSET_LIBRARY_OWNER,
		DepotRolesConstants.DESIGN_LIBRARY_OWNER
	};

	@Reference
	private Language _language;

}