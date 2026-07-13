/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Prates
 */
@RunWith(Arquillian.class)
public class DesignLibraryRolesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-57283")
	@Test
	public void testAddCompany() throws Exception {
		Company company = null;

		try {
			company = CompanyTestUtil.addCompany();

			long companyId = company.getCompanyId();

			for (String name : DepotRolesConstants.DESIGN_LIBRARY_ROLE_NAMES) {
				Role role = _roleLocalService.getRole(companyId, name);

				Assert.assertEquals(RoleConstants.TYPE_DEPOT, role.getType());
				Assert.assertEquals(
					DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
					role.getSubtype());
				Assert.assertEquals(
					RoleConstants.toSystemRoleExternalReferenceCode(name),
					role.getExternalReferenceCode());
			}

			List<String> administratorResourceActions =
				ResourceActionsUtil.getResourceActions(
					DepotEntry.class.getName());

			administratorResourceActions.remove(ActionKeys.ASSIGN_USER_ROLES);

			_assertResourcePermissions(
				companyId, DepotEntry.class.getName(),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
				administratorResourceActions);

			_assertResourcePermissions(
				companyId, _ASSET_TAGS_RESOURCE_NAME,
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
				List.of(ActionKeys.MANAGE_TAG));

			_assertResourcePermissions(
				companyId, _ASSET_TAGS_RESOURCE_NAME,
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER,
				List.of(ActionKeys.MANAGE_TAG));

			_assertResourcePermissions(
				companyId, DepotEntry.class.getName(),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				DepotRolesConstants.DESIGN_LIBRARY_MEMBER,
				List.of(ActionKeys.VIEW));

			Role administratorRole = _roleLocalService.getRole(
				companyId, DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR);

			for (String name : DepotRolesConstants.DESIGN_LIBRARY_ROLE_NAMES) {
				Role role = _roleLocalService.getRole(companyId, name);

				_assertResourcePermissions(
					companyId, Role.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(role.getRoleId()),
					administratorRole.getName(), List.of(ActionKeys.VIEW));
			}
		}
		finally {
			if (company != null) {
				_companyLocalService.deleteCompany(company.getCompanyId());
			}
		}
	}

	private void _assertResourcePermissions(
			long companyId, String resourceName, int scope, String primKey,
			String roleName, List<String> actionIds)
		throws PortalException {

		Role role = _roleLocalService.getRole(companyId, roleName);

		for (String actionId : actionIds) {
			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, resourceName, scope, primKey, role.getRoleId(),
					actionId));
		}
	}

	private static final String _ASSET_TAGS_RESOURCE_NAME =
		"com.liferay.asset.tags";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}