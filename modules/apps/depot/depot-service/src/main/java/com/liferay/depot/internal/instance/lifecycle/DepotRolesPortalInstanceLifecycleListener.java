/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.instance.lifecycle;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.internal.roles.DepotDesignLibraryRolesHelper;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.role.contributor.DepotRolePermissionsContributor;
import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Cristina González
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class DepotRolesPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company)
		throws PortalException {

		Role assetLibraryAdministratorRole = _getOrCreateRole(
			company.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		List<String> assetLibraryAdministratorResourceActions =
			ResourceActionsUtil.getResourceActions(DepotEntry.class.getName());

		assetLibraryAdministratorResourceActions.remove(
			ActionKeys.ASSIGN_USER_ROLES);

		_resourcePermissionLocalService.setResourcePermissions(
			company.getCompanyId(), DepotEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(company.getCompanyId()),
			assetLibraryAdministratorRole.getRoleId(),
			assetLibraryAdministratorResourceActions.toArray(new String[0]));

		_resourcePermissionLocalService.addResourcePermission(
			company.getCompanyId(), _ASSET_TAGS_RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(company.getCompanyId()),
			assetLibraryAdministratorRole.getRoleId(), ActionKeys.MANAGE_TAG);

		Role assetLibraryContentReviewerRole = _getOrCreateRole(
			company.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		_resourcePermissionLocalService.addResourcePermission(
			company.getCompanyId(), _ASSET_TAGS_RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(company.getCompanyId()),
			assetLibraryContentReviewerRole.getRoleId(), ActionKeys.MANAGE_TAG);

		Role assetLibraryMemberRole = _getOrCreateRole(
			company.getCompanyId(), DepotRolesConstants.ASSET_LIBRARY_MEMBER);

		_resourcePermissionLocalService.addResourcePermission(
			company.getCompanyId(), DepotEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(company.getCompanyId()),
			assetLibraryMemberRole.getRoleId(), ActionKeys.VIEW);

		for (String name : DepotRolesConstants.DEPOT_ROLE_NAMES) {
			Role role = _getOrCreateRole(company.getCompanyId(), name);

			_resourceLocalService.addResources(
				company.getCompanyId(), 0, 0, Role.class.getName(),
				role.getRoleId(), false, false, false);

			_resourcePermissionLocalService.setResourcePermissions(
				company.getCompanyId(), Role.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(role.getRoleId()),
				assetLibraryAdministratorRole.getRoleId(),
				new String[] {ActionKeys.VIEW});
		}

		if (FeatureFlagManagerUtil.isEnabled(
				company.getCompanyId(), "LPD-57283")) {

			DepotDesignLibraryRolesHelper depotDesignLibraryRolesHelper =
				new DepotDesignLibraryRolesHelper(
					_language, _resourceLocalService,
					_resourcePermissionLocalService, _roleLocalService,
					_userLocalService);

			depotDesignLibraryRolesHelper.setUpDesignLibraryRoles(
				company.getCompanyId());

			depotDesignLibraryRolesHelper.setUpResourcePermissions(
				company.getCompanyId(), _depotRolePermissionsContributors);
		}
	}

	private Role _getOrCreateRole(long companyId, String name)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(companyId, name);

		if (role == null) {
			boolean addResource = PermissionThreadLocal.isAddResource();

			try {
				PermissionThreadLocal.setAddResource(false);

				User user = _userLocalService.getGuestUser(companyId);

				return _roleLocalService.addRole(
					RoleConstants.toSystemRoleExternalReferenceCode(name),
					user.getUserId(), null, 0, name,
					DepotRoleUtil.getTitleMap(_language, name),
					DepotRoleUtil.getDescriptionMap(_language, name),
					RoleConstants.TYPE_DEPOT, null, null);
			}
			finally {
				PermissionThreadLocal.setAddResource(addResource);
			}
		}

		return role;
	}

	private static final String _ASSET_TAGS_RESOURCE_NAME =
		"com.liferay.asset.tags";

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile List<DepotRolePermissionsContributor>
		_depotRolePermissionsContributors;

	@Reference
	private Language _language;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}