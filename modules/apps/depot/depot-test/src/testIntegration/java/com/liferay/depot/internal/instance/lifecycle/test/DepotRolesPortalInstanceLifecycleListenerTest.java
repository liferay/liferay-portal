/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class DepotRolesPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-17564"), @FeatureFlag("LPD-57283")
		}
	)
	@Test
	public void testAddCompany() throws Exception {
		long companyId = _company.getCompanyId();

		_assertRole(
			companyId,
			"space-administrators-are-super-users-of-their-space-but-cannot-" +
				"make-other-users-into-space-administrators",
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
			"space-administrator");
		_assertRole(
			companyId,
			"all-users-who-belong-to-a-space-have-this-role-within-that-space",
			DepotRolesConstants.ASSET_LIBRARY_MEMBER, "space-member");
		_assertRole(
			companyId,
			"space-owners-are-super-users-of-their-space-and-can-assign-" +
				"space-roles-to-users",
			DepotRolesConstants.ASSET_LIBRARY_OWNER, "space-owner");

		_assertRoleResourcePermissions(
			companyId, DepotEntry.class.getName(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
			ResourceActionsUtil.getResourceActions(DepotEntry.class.getName()));
		_assertRoleResourcePermissions(
			companyId, DepotEntry.class.getName(),
			DepotRolesConstants.ASSET_LIBRARY_MEMBER, List.of(ActionKeys.VIEW));

		_assertRoleResourcePermissions(
			companyId, _ASSET_TAGS_RESOURCE_NAME,
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
			List.of(ActionKeys.MANAGE_TAG));
		_assertRoleResourcePermissions(
			companyId, _ASSET_TAGS_RESOURCE_NAME,
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
			List.of(ActionKeys.MANAGE_TAG));

		for (String name : DepotRolesConstants.DEPOT_ROLE_NAMES) {
			_assertRoleResourcePermissions(
				companyId, Role.class.getName(), name,
				List.of(ActionKeys.VIEW));
		}

		for (String name : DepotRolesConstants.DESIGN_LIBRARY_ROLE_NAMES) {
			Role role = _roleLocalService.getRole(companyId, name);

			Assert.assertEquals(RoleConstants.TYPE_DEPOT, role.getType());
			Assert.assertEquals(
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY, role.getSubtype());
			Assert.assertEquals(
				RoleConstants.toSystemRoleExternalReferenceCode(name),
				role.getExternalReferenceCode());
		}

		List<String> administratorResourceActions =
			ResourceActionsUtil.getResourceActions(DepotEntry.class.getName());

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
				String.valueOf(role.getRoleId()), administratorRole.getName(),
				List.of(ActionKeys.VIEW));
		}
	}

	private void _assertResourcePermissions(
			long companyId, String resourceName, int scope, String primKey,
			String roleName, List<String> actionIds)
		throws Exception {

		Role role = _roleLocalService.getRole(companyId, roleName);

		for (String actionId : actionIds) {
			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, resourceName, scope, primKey, role.getRoleId(),
					actionId));
		}
	}

	private void _assertRole(
			long companyId, String descriptionKey, String name, String titleKey)
		throws Exception {

		try {
			Role role = _roleLocalService.getRole(companyId, name);

			Assert.assertEquals(
				RoleConstants.toSystemRoleExternalReferenceCode(name),
				role.getExternalReferenceCode());
			Assert.assertEquals(
				2,
				_resourcePermissionLocalService.getResourcePermissionsCount(
					companyId, Role.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(role.getRoleId())));

			Locale locale = LocaleUtil.getDefault();

			Assert.assertEquals(
				role.getDescription(locale),
				LanguageUtil.get(locale, descriptionKey));
			Assert.assertEquals(
				role.getTitle(locale), LanguageUtil.get(locale, titleKey));
		}
		catch (NoSuchRoleException noSuchRoleException) {
			throw new AssertionError(noSuchRoleException.getMessage());
		}
		catch (NoSuchResourcePermissionException
					noSuchResourcePermissionException) {

			throw new AssertionError(
				noSuchResourcePermissionException.getMessage());
		}
	}

	private void _assertRoleResourcePermissions(
			long companyId, String resourceName, String roleName,
			List<String> actionIds)
		throws Exception {

		Role administratorRole = _roleLocalService.getRole(
			companyId, DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);
		Role role = _roleLocalService.getRole(companyId, roleName);

		for (String actionId : actionIds) {
			if (StringUtil.equals(resourceName, DepotEntry.class.getName()) ||
				StringUtil.equals(resourceName, _ASSET_TAGS_RESOURCE_NAME)) {

				if (Objects.equals(actionId, ActionKeys.ASSIGN_USER_ROLES)) {
					continue;
				}

				Assert.assertTrue(
					_resourcePermissionLocalService.hasResourcePermission(
						companyId, resourceName,
						ResourceConstants.SCOPE_COMPANY,
						String.valueOf(companyId), role.getRoleId(), actionId));

				continue;
			}

			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(role.getRoleId()),
					administratorRole.getRoleId(), actionId));
		}
	}

	private static final String _ASSET_TAGS_RESOURCE_NAME =
		"com.liferay.asset.tags";

	private Company _company;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}