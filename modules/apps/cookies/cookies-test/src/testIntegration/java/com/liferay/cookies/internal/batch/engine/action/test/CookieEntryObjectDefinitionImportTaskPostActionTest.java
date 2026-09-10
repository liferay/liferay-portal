/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.batch.engine.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Álvaro Saugar
 */
@RunWith(Arquillian.class)
public class CookieEntryObjectDefinitionImportTaskPostActionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-101999")
	public void testRun() throws Exception {
		_testRun("L_FUNCTIONAL_COOKIE_ENTRY");
		_testRun("L_NECESSARY_COOKIE_ENTRY");
		_testRun("L_PERFORMANCE_COOKIE_ENTRY");
		_testRun("L_PERSONALIZATION_COOKIE_ENTRY");
	}

	private void _testRun(String externalReferenceCode) throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		for (String roleName : _ROLE_NAMES) {
			Role role = _roleLocalService.getRole(companyId, roleName);

			_resourcePermissionLocalService.removeResourcePermission(
				companyId, objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId(), ActionKeys.VIEW);
			_resourcePermissionLocalService.removeResourcePermission(
				companyId, objectDefinition.getPortletId(),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId(), ActionKeys.VIEW);
		}

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskLocalService.createBatchEngineImportTask(
				RandomTestUtil.randomLong());

		batchEngineImportTask.setCompanyId(companyId);

		com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition
			restObjectDefinition =
				new com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition();

		restObjectDefinition.setExternalReferenceCode(externalReferenceCode);

		_importTaskPostAction.run(
			batchEngineImportTask, null, null, null, restObjectDefinition);

		for (String roleName : _ROLE_NAMES) {
			Role role = _roleLocalService.getRole(companyId, roleName);

			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, objectDefinition.getClassName(),
					ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
					role.getRoleId(), ActionKeys.VIEW));
			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, objectDefinition.getPortletId(),
					ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
					role.getRoleId(), ActionKeys.ADD_TO_PAGE));
			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, objectDefinition.getPortletId(),
					ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
					role.getRoleId(), ActionKeys.VIEW));
		}
	}

	private static final String[] _ROLE_NAMES = {
		RoleConstants.GUEST, RoleConstants.USER
	};

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Inject(
		filter = "component.name=com.liferay.cookies.internal.batch.engine.action.CookieEntryObjectDefinitionImportTaskPostAction"
	)
	private ImportTaskPostAction _importTaskPostAction;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}