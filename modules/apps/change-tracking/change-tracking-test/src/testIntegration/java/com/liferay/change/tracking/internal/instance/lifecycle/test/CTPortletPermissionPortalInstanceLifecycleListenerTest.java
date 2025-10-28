/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Gislayne Vitorino
 */
@RunWith(Arquillian.class)
public class CTPortletPermissionPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testCheckPublicationsRegularRoles() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		Bundle bundle = FrameworkUtil.getBundle(
			CTPortletPermissionPortalInstanceLifecycleListenerTest.class);

		String symbolicName = "com.liferay.change.tracking.web";

		bundle = BundleUtil.getBundle(bundle.getBundleContext(), symbolicName);

		Assert.assertNotNull(
			"Unable to find bundle with symbolic name: " + symbolicName,
			bundle);

		Class<?> clazz = bundle.loadClass(
			"com.liferay.change.tracking.web.internal.util." +
				"PublicationsRegularRolesUtil");

		String[] publicationsRegularRoleNames =
			ReflectionTestUtil.getFieldValue(
				clazz, "PUBLICATIONS_REGULAR_ROLE_NAMES");

		for (String publicationsRegularRoleName :
				publicationsRegularRoleNames) {

			Role role = _roleLocalService.fetchRole(
				company.getCompanyId(), publicationsRegularRoleName);

			List<Role> roles = _roleLocalService.getRoles(
				company.getCompanyId());

			Assert.assertTrue(roles.contains(role));
		}

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		for (String publicationsRegularRoleName :
				publicationsRegularRoleNames) {

			Role role = _roleLocalService.getRole(
				company.getCompanyId(), publicationsRegularRoleName);

			Method method = clazz.getMethod(
				"getModelResourceActions", String.class);

			Constructor<?> constructor = clazz.getConstructor();

			String[] actionIds = (String[])method.invoke(
				constructor.newInstance(), publicationsRegularRoleName);

			for (String actionId : actionIds) {
				Assert.assertTrue(
					_resourcePermissionLocalService.hasResourcePermission(
						company.getCompanyId(), CTCollection.class.getName(),
						ResourceConstants.SCOPE_COMPANY,
						String.valueOf(company.getCompanyId()),
						role.getRoleId(), actionId));
			}
		}
	}

	@Test
	public void testCheckPublicationsReviewerRole() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		long companyId = company.getCompanyId();

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.PUBLICATIONS_USER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				companyId,
				_resourceActions.getPortletRootModelResource(
					CTPortletKeys.PUBLICATIONS),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId(), CTActionKeys.ADD_PUBLICATION));

		for (String actionId :
				Arrays.asList(
					ActionKeys.ACCESS_IN_CONTROL_PANEL, ActionKeys.VIEW)) {

			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, CTPortletKeys.PUBLICATIONS,
					ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
					role.getRoleId(), actionId));
		}

		ResourcePermission rootModelResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				companyId,
				_resourceActions.getPortletRootModelResource(
					CTPortletKeys.PUBLICATIONS),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId());

		rootModelResourcePermission.removeResourceAction(
			CTActionKeys.ADD_PUBLICATION);

		rootModelResourcePermission =
			_resourcePermissionLocalService.updateResourcePermission(
				rootModelResourcePermission);

		Assert.assertFalse(
			rootModelResourcePermission.hasActionId(
				CTActionKeys.ADD_PUBLICATION));

		ResourcePermission portletResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				companyId, CTPortletKeys.PUBLICATIONS,
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId());

		portletResourcePermission.removeResourceAction(
			ActionKeys.ACCESS_IN_CONTROL_PANEL);
		portletResourcePermission.removeResourceAction(ActionKeys.VIEW);

		portletResourcePermission =
			_resourcePermissionLocalService.updateResourcePermission(
				portletResourcePermission);

		Assert.assertFalse(
			portletResourcePermission.hasActionId(
				ActionKeys.ACCESS_IN_CONTROL_PANEL));
		Assert.assertFalse(
			portletResourcePermission.hasActionId(ActionKeys.VIEW));

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		rootModelResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				companyId,
				_resourceActions.getPortletRootModelResource(
					CTPortletKeys.PUBLICATIONS),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId());

		Assert.assertFalse(
			rootModelResourcePermission.hasActionId(
				CTActionKeys.ADD_PUBLICATION));

		portletResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				companyId, CTPortletKeys.PUBLICATIONS,
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId());

		Assert.assertFalse(
			portletResourcePermission.hasActionId(
				ActionKeys.ACCESS_IN_CONTROL_PANEL));
		Assert.assertFalse(
			portletResourcePermission.hasActionId(ActionKeys.VIEW));

		rootModelResourcePermission.addResourceAction(
			CTActionKeys.ADD_PUBLICATION);

		_resourcePermissionLocalService.updateResourcePermission(
			rootModelResourcePermission);

		portletResourcePermission.addResourceAction(
			ActionKeys.ACCESS_IN_CONTROL_PANEL);
		portletResourcePermission.addResourceAction(ActionKeys.VIEW);

		_resourcePermissionLocalService.updateResourcePermission(
			portletResourcePermission);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.change.tracking.web.internal.instance.lifecycle.CTPortletPermissionPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}