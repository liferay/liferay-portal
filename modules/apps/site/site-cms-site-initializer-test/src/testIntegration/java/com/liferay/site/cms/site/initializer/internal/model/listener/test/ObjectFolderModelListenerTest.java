/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class ObjectFolderModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testOnAfterCreate() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(company);

		Role role = _roleLocalService.getRole(
			company.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		_assertResourcePermissions(
			company,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
			role);
		_assertResourcePermissions(
			company, ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
			role);
		_assertResourcePermissions(
			company,
			ObjectFolderConstants.
				EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS,
			role);
	}

	private void _assertResourcePermission(
			String actionId, Company company, ObjectFolder objectFolder,
			Role role)
		throws Exception {

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), ObjectFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectFolder.getObjectFolderId()),
				role.getRoleId(), actionId));
	}

	private void _assertResourcePermissions(
			Company company, String externalReferenceCode, Role role)
		throws Exception {

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				externalReferenceCode, company.getCompanyId());

		_assertResourcePermission(
			ObjectActionKeys.ADD_OBJECT_DEFINITION, company, objectFolder,
			role);
		_assertResourcePermission(
			ActionKeys.DELETE, company, objectFolder, role);
		_assertResourcePermission(
			ActionKeys.PERMISSIONS, company, objectFolder, role);
		_assertResourcePermission(
			ActionKeys.UPDATE, company, objectFolder, role);
		_assertResourcePermission(ActionKeys.VIEW, company, objectFolder, role);
	}

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}