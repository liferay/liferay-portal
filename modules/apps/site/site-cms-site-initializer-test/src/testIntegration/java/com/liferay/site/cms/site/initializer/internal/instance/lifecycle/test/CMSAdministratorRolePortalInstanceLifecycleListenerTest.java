/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class CMSAdministratorRolePortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPortalInstanceRegistered() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Role role = _roleLocalService.getRole(
			company.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		_resourcePermissionLocalService.addResourcePermission(
			company.getCompanyId(), User.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(company.getCompanyId()), role.getRoleId(),
			ActionKeys.UPDATE);
		_resourcePermissionLocalService.addResourcePermission(
			company.getCompanyId(), User.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(company.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);

		Assert.assertTrue(
			_hasUserResourcePermission(company, role, ActionKeys.UPDATE));
		Assert.assertTrue(
			_hasUserResourcePermission(company, role, ActionKeys.VIEW));

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		Assert.assertFalse(
			_hasUserResourcePermission(company, role, ActionKeys.UPDATE));
		Assert.assertTrue(
			_hasUserResourcePermission(company, role, ActionKeys.VIEW));
	}

	private boolean _hasUserResourcePermission(
			Company company, Role role, String actionId)
		throws Exception {

		return _resourcePermissionLocalService.hasResourcePermission(
			company.getCompanyId(), User.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(company.getCompanyId()), role.getRoleId(), actionId);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.instance.lifecycle.CMSAdministratorRolePortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}